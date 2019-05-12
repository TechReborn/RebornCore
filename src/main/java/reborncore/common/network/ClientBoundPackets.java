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
import net.minecraft.container.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.RebornCore;
import reborncore.client.containerBuilder.builder.IExtendedContainerListener;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileMachineBase;

public class ClientBoundPackets {

	public static void init() {
		NetworkManager.registerPacketHandler(new Identifier("reborncore", "custom_description"), (extendedPacketBuffer, context) -> {
			BlockPos pos = extendedPacketBuffer.readBlockPos();
			CompoundTag tagCompound = extendedPacketBuffer.readCompoundTag();
			context.enqueueWork(() -> {
				World world = RebornCore.proxy.getClientWorld();
				if (world.isBlockLoaded(pos)) {
					TileEntity tileentity = world.getTileEntity(pos);
					if (tileentity != null && tagCompound != null) {
						tileentity.read(tagCompound);
					}
				}
			});
		});

		NetworkManager.registerPacketHandler(new Identifier("reborncore", "fluid_config_sync"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			FluidConfiguration fluidConfiguration = new FluidConfiguration(packetBuffer.readCompoundTag());
			context.enqueueWork(() -> {
				if (!RebornCore.proxy.getClientWorld().isBlockLoaded(pos, false)) {
					return;
				}
				TileMachineBase machineBase = (TileMachineBase) RebornCore.proxy.getClientWorld().getTileEntity(pos);
				if (machineBase == null || machineBase.fluidConfiguration == null || fluidConfiguration == null) {
					RebornCore.LOGGER.error("Failed to sync fluid config data to " + pos);
				}
				fluidConfiguration.getAllSides().forEach(fluidConfig -> machineBase.fluidConfiguration.updateFluidConfig(fluidConfig));
				machineBase.fluidConfiguration.setInput(fluidConfiguration.autoInput());
				machineBase.fluidConfiguration.setOutput(fluidConfiguration.autoOutput());

			});
		});

		NetworkManager.registerPacketHandler(new Identifier("reborncore", "slot_sync"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			SlotConfiguration slotConfig = new SlotConfiguration(packetBuffer.readCompoundTag());
			context.enqueueWork(() -> {
				if (!RebornCore.proxy.getClientWorld().isBlockLoaded(pos, false)) {
					return;
				}
				TileMachineBase machineBase = (TileMachineBase) RebornCore.proxy.getClientWorld().getTileEntity(pos);
				if (machineBase == null || machineBase.slotConfiguration == null || slotConfig == null || slotConfig.getSlotDetails() == null) {
					RebornCore.LOGGER.error("Failed to sync slot data to " + pos);
				}
				Minecraft.getInstance().addScheduledTask(() -> slotConfig.getSlotDetails().forEach(slotConfigHolder -> machineBase.slotConfiguration.updateSlotDetails(slotConfigHolder)));
			});
		});

		NetworkManager.registerPacketHandler(new Identifier("reborncore", "send_object"), (packetBuffer, context) -> {
			int id = packetBuffer.readInt();
			Object value = packetBuffer.readObject();
			String container = packetBuffer.readString(packetBuffer.readInt());
			context.enqueueWork(() -> {
				GuiScreen gui = Minecraft.getInstance().currentScreen;
				if (gui instanceof GuiContainer) {
					Container container1 = ((GuiContainer) gui).inventorySlots;
					if (container1 instanceof IExtendedContainerListener) {
						((IExtendedContainerListener) container1).handleObject(id, value);
					}
				}
			});
		});
	}

	public static NetworkPacket createCustomDescriptionPacket(BlockEntity tileEntity) {
		return createCustomDescriptionPacket(tileEntity.getPos(), tileEntity.toTag(new CompoundTag()));
	}

	public static NetworkPacket createCustomDescriptionPacket(BlockPos blockPos, CompoundTag nbt) {
		return NetworkManager.createPacket(new Identifier("reborncore", "custom_description"), packetBuffer -> {
			packetBuffer.writeBlockPos(blockPos);
			packetBuffer.writeCompoundTag(nbt);
		});
	}

	public static NetworkPacket createPacketFluidConfigSync(BlockPos pos, FluidConfiguration fluidConfiguration) {
		return NetworkManager.createPacket(new Identifier("reborncore", "fluid_config_sync"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(fluidConfiguration.serializeNBT());
		});
	}

	public static NetworkPacket createPacketSlotSync(BlockPos pos, SlotConfiguration slotConfig) {
		return NetworkManager.createPacket(new Identifier("reborncore", "slot_sync"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.serializeNBT());
		});
	}

	public static NetworkPacket createPacketSendObject(int id, Object value, Container container) {
		return NetworkManager.createPacket(new Identifier("reborncore", "send_object"), packetBuffer -> {
			packetBuffer.writeInt(id);
			packetBuffer.writeObject(value);
			packetBuffer.writeInt(container.getClass().getName().length());
			packetBuffer.writeString(container.getClass().getName());
		});
	}

}
