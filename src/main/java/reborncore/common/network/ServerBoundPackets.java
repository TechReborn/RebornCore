package reborncore.common.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileMachineBase;

public class ServerBoundPackets {

	public static void init() {
		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "fluid_config_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			FluidConfiguration.FluidConfig fluidConfiguration = new FluidConfiguration.FluidConfig(packetBuffer.readCompoundTag());
			context.enqueueWork(() -> {
				TileMachineBase legacyMachineBase = (TileMachineBase) context.getSender().world.getTileEntity(pos);
				legacyMachineBase.fluidConfiguration.updateFluidConfig(fluidConfiguration);
				legacyMachineBase.markDirty();

				NetworkPacket packetFluidConfigSync = ClientBoundPackets.createPacketFluidConfigSync(pos, legacyMachineBase.fluidConfiguration);
				NetworkManager.sendToTracking(packetFluidConfigSync, legacyMachineBase);

				//We update the block to allow pipes that are connecting to detctect the update and change their connection status if needed
				World world = legacyMachineBase.getWorld();
				IBlockState blockState = world.getBlockState(legacyMachineBase.getPos());
				world.markAndNotifyBlock(legacyMachineBase.getPos(), world.getChunk(legacyMachineBase.getPos()), blockState, blockState, 3);
			});
		});

		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "config_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			NBTTagCompound tagCompound = packetBuffer.readCompoundTag();
			context.enqueueWork(() -> {
				TileMachineBase legacyMachineBase = (TileMachineBase) context.getSender().world.getTileEntity(pos);
				legacyMachineBase.slotConfiguration.deserializeNBT(tagCompound);
				legacyMachineBase.markDirty();

				NetworkPacket packetSlotSync = ClientBoundPackets.createPacketSlotSync(pos, legacyMachineBase.slotConfiguration);
				NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
			});
		});

		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "fluid_io_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			boolean input = packetBuffer.readBoolean();
			boolean output = packetBuffer.readBoolean();
			context.enqueueWork(() -> {
				TileMachineBase legacyMachineBase = (TileMachineBase) context.getSender().world.getTileEntity(pos);
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

		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "io_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			int slotID = packetBuffer.readInt();
			boolean input = packetBuffer.readBoolean();
			boolean output = packetBuffer.readBoolean();
			boolean filter = packetBuffer.readBoolean();

			TileMachineBase legacyMachineBase = (TileMachineBase) context.getSender().world.getTileEntity(pos);
			SlotConfiguration.SlotConfigHolder holder = legacyMachineBase.slotConfiguration.getSlotDetails(slotID);
			if (holder == null) {
				return;
			}

			context.enqueueWork(() -> {
				holder.setInput(input);
				holder.setOutput(output);
				holder.setfilter(filter);

				//Syncs back to the client
				NetworkPacket packetSlotSync = ClientBoundPackets.createPacketSlotSync(pos, legacyMachineBase.slotConfiguration);
				NetworkManager.sendToAll(packetSlotSync);
			});
		});

		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "slot_save"), (packetBuffer, context) -> {
			BlockPos pos = packetBuffer.readBlockPos();
			SlotConfiguration.SlotConfig slotConfig = new SlotConfiguration.SlotConfig(packetBuffer.readCompoundTag());
			context.enqueueWork(() -> {
				TileMachineBase legacyMachineBase = (TileMachineBase) context.getSender().world.getTileEntity(pos);
				legacyMachineBase.slotConfiguration.getSlotDetails(slotConfig.getSlotID()).updateSlotConfig(slotConfig);
				legacyMachineBase.markDirty();

				NetworkPacket packetSlotSync = ClientBoundPackets.createPacketSlotSync(pos, legacyMachineBase.slotConfiguration);
				NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
			});
		});

	}

	public static NetworkPacket createPacketFluidConfigSave(BlockPos pos, FluidConfiguration.FluidConfig fluidConfiguration) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "fluid_config_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(fluidConfiguration.serializeNBT());
		});
	}

	public static NetworkPacket createPacketConfigSave(BlockPos pos, SlotConfiguration slotConfig) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "config_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.serializeNBT());
		});
	}

	public static NetworkPacket createPacketFluidIOSave(BlockPos pos, boolean input, boolean output) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "fluid_io_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeBoolean(input);
			packetBuffer.writeBoolean(output);
		});
	}

	public static NetworkPacket createPacketIOSave(BlockPos pos, int slotID, boolean input, boolean output, boolean filter) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "io_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeInt(slotID);
			packetBuffer.writeBoolean(input);
			packetBuffer.writeBoolean(output);
			packetBuffer.writeBoolean(filter);
		});
	}

	public static NetworkPacket createPacketSlotSave(BlockPos pos, SlotConfiguration.SlotConfig slotConfig) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "slot_save"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.serializeNBT());
		});
	}
}
