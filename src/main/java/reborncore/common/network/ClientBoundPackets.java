/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.network;


import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.RebornCore;
import reborncore.client.containerBuilder.builder.IExtendedContainerListener;
import reborncore.common.blockentity.FluidConfiguration;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blockentity.SlotConfiguration;

public class ClientBoundPackets {

	public static void init() {
		NetworkManager.registerClientBoundHandler(new Identifier("reborncore", "custom_description"), (extendedPacketBuffer, context) -> {
			BlockPos pos = extendedPacketBuffer.readBlockPos();
			CompoundTag tagCompound = extendedPacketBuffer.readCompoundTag();
			context.getTaskQueue().execute(() -> {
				World world = RebornCore.proxy.getClientWorld();
				if (world.isBlockLoaded(pos)) {
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
				if (!RebornCore.proxy.getClientWorld().isBlockLoaded(pos)) {
					return;
				}
				MachineBaseBlockEntity machineBase = (MachineBaseBlockEntity) RebornCore.proxy.getClientWorld().getBlockEntity(pos);
				if (machineBase == null || machineBase.fluidConfiguration == null || fluidConfiguration == null) {
					RebornCore.LOGGER.error("Failed to sync fluid config data to " + pos);
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
				if (!RebornCore.proxy.getClientWorld().isBlockLoaded(pos)) {
					return;
				}
				MachineBaseBlockEntity machineBase = (MachineBaseBlockEntity) RebornCore.proxy.getClientWorld().getBlockEntity(pos);
				if (machineBase == null || machineBase.getSlotConfiguration() == null || slotConfig == null || slotConfig.getSlotDetails() == null) {
					RebornCore.LOGGER.error("Failed to sync slot data to " + pos);
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
	}

	public static Packet createCustomDescriptionPacket(BlockEntity blockEntity) {
		return createCustomDescriptionPacket(blockEntity.getPos(), blockEntity.toTag(new CompoundTag()));
	}

	public static Packet createCustomDescriptionPacket(BlockPos blockPos, CompoundTag nbt) {
		return NetworkManager.createClientBoundPacket(new Identifier("reborncore", "custom_description"), packetBuffer -> {
			packetBuffer.writeBlockPos(blockPos);
			packetBuffer.writeCompoundTag(nbt);
		});
	}

	public static Packet createPacketFluidConfigSync(BlockPos pos, FluidConfiguration fluidConfiguration) {
		return NetworkManager.createClientBoundPacket(new Identifier("reborncore", "fluid_config_sync"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(fluidConfiguration.toTag());
		});
	}

	public static Packet createPacketSlotSync(BlockPos pos, SlotConfiguration slotConfig) {
		return NetworkManager.createClientBoundPacket(new Identifier("reborncore", "slot_sync"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.toTag());
		});
	}

	public static Packet createPacketSendObject(int id, Object value, Container container) {
		return NetworkManager.createClientBoundPacket(new Identifier("reborncore", "send_object"), packetBuffer -> {
			packetBuffer.writeInt(id);
			packetBuffer.writeObject(value);
			packetBuffer.writeInt(container.getClass().getName().length());
			packetBuffer.writeString(container.getClass().getName());
		});
	}

}
