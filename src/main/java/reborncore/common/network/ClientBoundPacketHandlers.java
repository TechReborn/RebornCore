package reborncore.common.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.RebornCore;
import reborncore.client.ClientChunkManager;
import reborncore.client.containerBuilder.builder.IExtendedContainerListener;
import reborncore.common.blockentity.FluidConfiguration;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blockentity.SlotConfiguration;
import reborncore.common.chunkloading.ChunkLoaderManager;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ClientBoundPacketHandlers {

	public static void init() {
		NetworkManager.registerClientBoundHandler(new Identifier("reborncore", "custom_description"), (extendedPacketBuffer, context) -> {
			BlockPos pos = extendedPacketBuffer.readBlockPos();
			CompoundTag tagCompound = extendedPacketBuffer.readCompoundTag();
			context.getTaskQueue().execute(() -> {
				World world = MinecraftClient.getInstance().world;
				if (world.isChunkLoaded(pos)) {
					BlockEntity blockentity = world.getBlockEntity(pos);
					if (blockentity != null && tagCompound != null) {
						blockentity.fromTag(tagCompound);
					}
				}
			});
		});

		NetworkManager.registerClientBoundHandler(new Identifier("reborncore", "fluid_config_sync"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			CompoundTag compoundTag = packetBuffer.readCompoundTag();

			context.getTaskQueue().execute(() -> {
				FluidConfiguration fluidConfiguration = new FluidConfiguration(compoundTag);
				if (!MinecraftClient.getInstance().world.isChunkLoaded(pos)) {
					return;
				}
				MachineBaseBlockEntity machineBase = (MachineBaseBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
				if (machineBase == null || machineBase.fluidConfiguration == null || fluidConfiguration == null) {
					RebornCore.LOGGER.error("Failed to sync fluid config data to " + pos);
					return;
				}
				fluidConfiguration.getAllSides().forEach(fluidConfig -> machineBase.fluidConfiguration.updateFluidConfig(fluidConfig));
				machineBase.fluidConfiguration.setInput(fluidConfiguration.autoInput());
				machineBase.fluidConfiguration.setOutput(fluidConfiguration.autoOutput());

			});
		});

		NetworkManager.registerClientBoundHandler(new Identifier("reborncore", "slot_sync"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			CompoundTag compoundTag = packetBuffer.readCompoundTag();

			context.getTaskQueue().execute(() -> {
				SlotConfiguration slotConfig = new SlotConfiguration(compoundTag);
				if (!MinecraftClient.getInstance().world.isChunkLoaded(pos)) {
					return;
				}
				MachineBaseBlockEntity machineBase = (MachineBaseBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
				if (machineBase == null || machineBase.getSlotConfiguration() == null || slotConfig == null || slotConfig.getSlotDetails() == null) {
					RebornCore.LOGGER.error("Failed to sync slot data to " + pos);
					return;
				}
				MinecraftClient.getInstance().execute(() -> slotConfig.getSlotDetails().forEach(slotConfigHolder -> machineBase.getSlotConfiguration().updateSlotDetails(slotConfigHolder)));
			});
		});

		NetworkManager.registerClientBoundHandler(new Identifier("reborncore", "send_object"), (packetBuffer, context) -> {
			int id = packetBuffer.readInt();
			Object value = packetBuffer.readObject();
			String container = packetBuffer.readString(packetBuffer.readInt());
			context.getTaskQueue().execute(() -> {
				Screen gui = MinecraftClient.getInstance().currentScreen;
				if (gui instanceof AbstractContainerScreen) {
					Container container1 = ((AbstractContainerScreen) gui).getContainer();
					if (container1 instanceof IExtendedContainerListener) {
						((IExtendedContainerListener) container1).handleObject(id, value);
					}
				}
			});
		});

		NetworkManager.registerClientBoundHandler(new Identifier("reborncore", "sync_chunks"), (extendedPacketBuffer, context) -> {
			CompoundTag tag = extendedPacketBuffer.readCompoundTag();
			ListTag listTag = tag.getList("chunks", tag.getType());
			List<ChunkLoaderManager.LoadedChunk> chunks = listTag.stream()
				.map(tag1 -> (CompoundTag) tag1)
				.map(ChunkLoaderManager.LoadedChunk::new)
				.collect(Collectors.toList());

			context.getTaskQueue().execute(() -> ClientChunkManager.setLoadedChunks(chunks));

		});
	}

}
