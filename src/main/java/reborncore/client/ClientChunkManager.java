package reborncore.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import reborncore.common.chunkloading.ChunkLoaderManager;
import reborncore.common.network.ServerBoundPackets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ClientChunkManager {

	private static List<ChunkLoaderManager.LoadedChunk> loadedChunks = new ArrayList<>();

	public static void setLoadedChunks(List<ChunkLoaderManager.LoadedChunk> chunks){
		loadedChunks = chunks;
	}

	public static void toggleLoadedChunks(BlockPos chunkLoader){
		if(loadedChunks.size() == 0){
			MinecraftClient.getInstance().getNetworkHandler().sendPacket(ServerBoundPackets.requestChunkloaderChunks(chunkLoader));
		} else {
			loadedChunks.clear();
		}
	}

	public static boolean hasChunksForLoader(BlockPos pos){
		return loadedChunks.stream()
			.filter(loadedChunk -> loadedChunk.getChunkLoader().equals(pos))
			.anyMatch(loadedChunk -> loadedChunk.getWorld().equals(Registry.DIMENSION.getId(MinecraftClient.getInstance().world.getDimension().getType())));
	}

	public static void render() {
		if(loadedChunks.size() == 0){
			return;
		}
		MinecraftClient minecraftClient = MinecraftClient.getInstance();

		Camera camera = minecraftClient.gameRenderer.getCamera();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		double x = camera.getPos().x;
		double y = camera.getPos().y;
		double z = camera.getPos().z;

		GlStateManager.disableTexture();
		GlStateManager.disableBlend();

		GlStateManager.lineWidth(5.0F);

		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		loadedChunks.stream()
			.filter(loadedChunk -> loadedChunk.getWorld().equals(Registry.DIMENSION.getId(minecraftClient.world.getDimension().getType())))
			.forEach(loadedChunk -> {
			double chunkX = (double) loadedChunk.getChunk().getStartX() - x;
			double chunkY = (double) loadedChunk.getChunk().getStartZ() - z;
			bufferBuilder.vertex(chunkX + 8, 0.0D - y, chunkY + 8).color(1.0F, 0.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(chunkX + 8, 0.0D - y, chunkY + 8).color(1.0F, 0.0F, 0.0F, 0.5F).next();
			bufferBuilder.vertex(chunkX + 8, 256.0D - y, chunkY + 8).color(1.0F, 0.0F, 0.0F, 0.5F).next();
			bufferBuilder.vertex(chunkX + 8, 256.0D - y, chunkY + 8).color(1.0F, 0.0F, 0.0F, 0.0F).next();
		});

		tessellator.draw();
		GlStateManager.lineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
	}

}
