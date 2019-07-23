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

import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.blockentity.FluidConfiguration;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blockentity.SlotConfiguration;

import java.util.function.BiConsumer;

public class ServerBoundPackets {

	public static void init() {
		registerPacketHandler(new Identifier("reborncore", "fluid_config_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			FluidConfiguration.FluidConfig fluidConfiguration = new FluidConfiguration.FluidConfig(packetBuffer.readCompoundTag());
			context.getTaskQueue().execute(() -> {
				MachineBaseBlockEntity legacyMachineBase = (MachineBaseBlockEntity) context.getPlayer().world.getBlockEntity(pos);
				legacyMachineBase.fluidConfiguration.updateFluidConfig(fluidConfiguration);
				legacyMachineBase.markDirty();

				NetworkPacket packetFluidConfigSync = ClientBoundPackets.createPacketFluidConfigSync(pos, legacyMachineBase.fluidConfiguration);
				NetworkManager.sendToTracking(packetFluidConfigSync, legacyMachineBase);

				//We update the block to allow pipes that are connecting to detctect the update and change their connection status if needed
				World world = legacyMachineBase.getWorld();
				BlockState blockState = world.getBlockState(legacyMachineBase.getPos());
				world.updateNeighborsAlways(legacyMachineBase.getPos(), blockState.getBlock());
			});
		});

		registerPacketHandler(new Identifier("reborncore", "config_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			CompoundTag tagCompound = packetBuffer.readCompoundTag();
			context.getTaskQueue().execute(() -> {
				MachineBaseBlockEntity legacyMachineBase = (MachineBaseBlockEntity) context.getPlayer().world.getBlockEntity(pos);
				legacyMachineBase.slotConfiguration.fromTag(tagCompound);
				legacyMachineBase.markDirty();

				NetworkPacket packetSlotSync = ClientBoundPackets.createPacketSlotSync(pos, legacyMachineBase.slotConfiguration);
				NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
			});
		});

		registerPacketHandler(new Identifier("reborncore", "fluid_io_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			boolean input = packetBuffer.readBoolean();
			boolean output = packetBuffer.readBoolean();
			context.getTaskQueue().execute(() -> {
				MachineBaseBlockEntity legacyMachineBase = (MachineBaseBlockEntity) context.getPlayer().world.getBlockEntity(pos);
				FluidConfiguration config = legacyMachineBase.fluidConfiguration;
				if (config == null) {
					return;
				}
				config.setInput(input);
				config.setOutput(output);

				//Syncs back to the client
				NetworkPacket packetFluidConfigSync = ClientBoundPackets.createPacketFluidConfigSync(pos, legacyMachineBase.fluidConfiguration);
				NetworkManager.sendToTracking(packetFluidConfigSync, legacyMachineBase);
			});
		});

		registerPacketHandler(new Identifier("reborncore", "io_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			int slotID = packetBuffer.readInt();
			boolean input = packetBuffer.readBoolean();
			boolean output = packetBuffer.readBoolean();
			boolean filter = packetBuffer.readBoolean();

			MachineBaseBlockEntity legacyMachineBase = (MachineBaseBlockEntity) context.getPlayer().world.getBlockEntity(pos);
			SlotConfiguration.SlotConfigHolder holder = legacyMachineBase.slotConfiguration.getSlotDetails(slotID);
			if (holder == null) {
				return;
			}

			context.getTaskQueue().execute(() -> {
				holder.setInput(input);
				holder.setOutput(output);
				holder.setfilter(filter);

				//Syncs back to the client
				NetworkPacket packetSlotSync = ClientBoundPackets.createPacketSlotSync(pos, legacyMachineBase.slotConfiguration);
				NetworkManager.sendToAll(packetSlotSync);
			});
		});

		registerPacketHandler(new Identifier("reborncore", "slot_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			SlotConfiguration.SlotConfig slotConfig = new SlotConfiguration.SlotConfig(packetBuffer.readCompoundTag());
			context.getTaskQueue().execute(() -> {
				MachineBaseBlockEntity legacyMachineBase = (MachineBaseBlockEntity) context.getPlayer().world.getBlockEntity(pos);
				legacyMachineBase.slotConfiguration.getSlotDetails(slotConfig.getSlotID()).updateSlotConfig(slotConfig);
				legacyMachineBase.markDirty();

				NetworkPacket packetSlotSync = ClientBoundPackets.createPacketSlotSync(pos, legacyMachineBase.slotConfiguration);
				NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
			});
		});

	}

	private static void registerPacketHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer){
		ServerSidePacketRegistry.INSTANCE.register(identifier, (packetContext, packetByteBuf) -> consumer.accept(new ExtendedPacketBuffer(packetByteBuf), packetContext));
	}

	public static NetworkPacket createPacketFluidConfigSave(BlockPos pos, FluidConfiguration.FluidConfig fluidConfiguration) {
		return NetworkManager.createPacket(new Identifier("reborncore", "fluid_config_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(fluidConfiguration.toTag());
		});
	}

	public static NetworkPacket createPacketConfigSave(BlockPos pos, SlotConfiguration slotConfig) {
		return NetworkManager.createPacket(new Identifier("reborncore", "config_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.toTag());
		});
	}

	public static NetworkPacket createPacketFluidIOSave(BlockPos pos, boolean input, boolean output) {
		return NetworkManager.createPacket(new Identifier("reborncore", "fluid_io_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeBoolean(input);
			packetBuffer.writeBoolean(output);
		});
	}

	public static NetworkPacket createPacketIOSave(BlockPos pos, int slotID, boolean input, boolean output, boolean filter) {
		return NetworkManager.createPacket(new Identifier("reborncore", "io_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeInt(slotID);
			packetBuffer.writeBoolean(input);
			packetBuffer.writeBoolean(output);
			packetBuffer.writeBoolean(filter);
		});
	}

	public static NetworkPacket createPacketSlotSave(BlockPos pos, SlotConfiguration.SlotConfig slotConfig) {
		return NetworkManager.createPacket(new Identifier("reborncore", "slot_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.toTag());
		});
	}
}
