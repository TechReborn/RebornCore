package reborncore.common.chunkloading;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import reborncore.common.network.ClientBoundPackets;
import reborncore.common.util.NBTSerializable;
import reborncore.common.world.DataAttachment;
import reborncore.common.world.DataAttachmentProvider;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

//This does not do the actual chunk loading, just keeps track of what chunks the chunk loader has loaded
public class ChunkLoaderManager implements DataAttachment {

	private static final ChunkTicketType<ChunkPos> CHUNK_LOADER = ChunkTicketType.create("reborncore:chunk_loader", Comparator.comparingLong(ChunkPos::toLong));

	public static ChunkLoaderManager get(World world){
		return DataAttachmentProvider.get(world, ChunkLoaderManager.class);
	}

	private final List<LoadedChunk> loadedChunks = new ArrayList<>();

	@Override
	public @Nonnull
	CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		ListTag listTag = new ListTag();

		listTag.addAll(loadedChunks.stream().map(LoadedChunk::write).collect(Collectors.toList()));
		tag.put("loadedchunks", listTag);

		return tag;
	}

	@Override
	public void read(@Nonnull CompoundTag tag) {
		loadedChunks.clear();
		ListTag listTag = tag.getList("loadedchunks", tag.getType());

		loadedChunks.addAll(listTag.stream()
			                    .map(tag1 -> (CompoundTag) tag1)
			                    .map(LoadedChunk::new)
			                    .collect(Collectors.toList())
		);
	}

	public Optional<LoadedChunk> getLoadedChunk(World world, ChunkPos chunkPos, BlockPos chunkLoader){
		return loadedChunks.stream()
			.filter(loadedChunk -> loadedChunk.getWorld().equals(getWorldName(world)))
			.filter(loadedChunk -> loadedChunk.getChunk().equals(chunkPos))
			.filter(loadedChunk -> loadedChunk.getChunkLoader().equals(chunkLoader))
			.findFirst();
	}

	public Optional<LoadedChunk> getLoadedChunk(World world, ChunkPos chunkPos){
		return loadedChunks.stream()
			.filter(loadedChunk -> loadedChunk.getWorld().equals(getWorldName(world)))
			.filter(loadedChunk -> loadedChunk.getChunk().equals(chunkPos))
			.findFirst();
	}

	public List<LoadedChunk> getLoadedChunks(World world, BlockPos chunkloader){
		return loadedChunks.stream()
			.filter(loadedChunk -> loadedChunk.getWorld().equals(getWorldName(world)))
			.filter(loadedChunk -> loadedChunk.getChunkLoader().equals(chunkloader))
			.collect(Collectors.toList());
	}

	public boolean isChunkLoaded(World world, ChunkPos chunkPos, BlockPos chunkLoader){
		return getLoadedChunk(world, chunkPos, chunkLoader).isPresent();
	}

	public boolean isChunkLoaded(World world, ChunkPos chunkPos){
		return getLoadedChunk(world, chunkPos).isPresent();
	}


	public void loadChunk(World world, ChunkPos chunkPos, BlockPos chunkLoader, String player){
		Validate.isTrue(!isChunkLoaded(world, chunkPos, chunkLoader), "chunk is already loaded");
		LoadedChunk loadedChunk = new LoadedChunk(chunkPos, getWorldName(world), player, chunkLoader);
		loadedChunks.add(loadedChunk);

		final ServerChunkManager serverChunkManager = ((ServerWorld) world).getChunkManager();
		serverChunkManager.addTicket(ChunkLoaderManager.CHUNK_LOADER, loadedChunk.getChunk(), 31, loadedChunk.getChunk());

	}

	public void unloadChunkLoader(World world, BlockPos chunkLoader){
		getLoadedChunks(world, chunkLoader).forEach(loadedChunk -> unloadChunk(world, loadedChunk.getChunk(), chunkLoader));
	}

	public void unloadChunk(World world, ChunkPos chunkPos, BlockPos chunkLoader){
		Optional<LoadedChunk> optionalLoadedChunk = getLoadedChunk(world, chunkPos, chunkLoader);
		Validate.isTrue(optionalLoadedChunk.isPresent(), "chunk is not loaded");

		LoadedChunk loadedChunk = optionalLoadedChunk.get();

		loadedChunks.remove(loadedChunk);

		if(!isChunkLoaded(world, loadedChunk.getChunk())){
			final ServerChunkManager serverChunkManager = ((ServerWorld) world).getChunkManager();
			serverChunkManager.removeTicket(ChunkLoaderManager.CHUNK_LOADER, loadedChunk.getChunk(), 31, loadedChunk.getChunk());
		}
	}

	public static Identifier getWorldName(World world){
		Validate.isTrue(world instanceof ServerWorld, "world must be a ServerWorld");
		return Registry.DIMENSION.getId(world.getDimension().getType());
	}

	public void syncChunkLoaderToClient(ServerPlayerEntity serverPlayerEntity, BlockPos chunkLoader){
		syncToClient(serverPlayerEntity, loadedChunks.stream().filter(loadedChunk -> loadedChunk.getChunkLoader().equals(chunkLoader)).collect(Collectors.toList()));
	}

	public void syncAllToClient(ServerPlayerEntity serverPlayerEntity) {
		syncToClient(serverPlayerEntity, loadedChunks);
	}

	public void clearClient(ServerPlayerEntity serverPlayerEntity) {
		syncToClient(serverPlayerEntity, Collections.emptyList());
	}

	public void syncToClient(ServerPlayerEntity serverPlayerEntity, List<LoadedChunk> chunks){
		serverPlayerEntity.networkHandler.sendPacket(
			ClientBoundPackets.createPacketSyncLoadedChunks(chunks)
		);
	}

	public static class LoadedChunk implements NBTSerializable {
		private ChunkPos chunk;
		private Identifier world;
		private String player;
		private BlockPos chunkLoader;

		public LoadedChunk(ChunkPos chunk, Identifier world, String player, BlockPos chunkLoader) {
			this.chunk = chunk;
			this.world = world;
			this.player = player;
			this.chunkLoader = chunkLoader;
			Validate.isTrue(!StringUtils.isBlank(player), "Player cannot be null");
		}

		public LoadedChunk(CompoundTag tag) {
			read(tag);
		}

		@Override
		public @Nonnull CompoundTag write() {
			CompoundTag tag = new CompoundTag();
			tag.putLong("chunk", chunk.toLong());
			tag.putString("world", world.toString());
			tag.putString("player", player);
			tag.putLong("chunkLoader", chunkLoader.asLong());
			return tag;
		}

		@Override
		public void read(@Nonnull CompoundTag tag) {
			chunk = new ChunkPos(tag.getLong("chunk"));
			world = new Identifier(tag.getString("world"));
			player = tag.getString("player");
			chunkLoader = BlockPos.fromLong(tag.getLong("chunkLoader"));
		}

		public ChunkPos getChunk() {
			return chunk;
		}

		public Identifier getWorld() {
			return world;
		}

		public String getPlayer() {
			return player;
		}

		public BlockPos getChunkLoader() {
			return chunkLoader;
		}
	}
}
