package reborncore.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.RebornCore;
import reborncore.client.containerBuilder.builder.IExtendedContainerListener;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileMachineBase;


public class ClientBoundPackets {

	public static void init() {
		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "custom_description"), (extendedPacketBuffer, context) -> {
			BlockPos pos = extendedPacketBuffer.readBlockPos();
			NBTTagCompound tagCompound = extendedPacketBuffer.readCompoundTag();
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


		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "fluid_config_sync"), (packetBuffer, context) -> {
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

		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "slot_sync"), (packetBuffer, context) -> {
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

		NetworkManager.registerPacketHandler(new ResourceLocation("reborncore", "send_object"), (packetBuffer, context) -> {
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

	public static NetworkPacket createCustomDescriptionPacket(TileEntity tileEntity) {
		return createCustomDescriptionPacket(tileEntity.getPos(), tileEntity.write(new NBTTagCompound()));
	}

	public static NetworkPacket createCustomDescriptionPacket(BlockPos blockPos, NBTTagCompound nbt) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "custom_description"), packetBuffer -> {
			packetBuffer.writeBlockPos(blockPos);
			packetBuffer.writeCompoundTag(nbt);
		});
	}

	public static NetworkPacket createPacketFluidConfigSync(BlockPos pos, FluidConfiguration fluidConfiguration) {
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "fluid_config_sync"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(fluidConfiguration.serializeNBT());
		});
	}

	public static NetworkPacket createPacketSlotSync(BlockPos pos, SlotConfiguration slotConfig){
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "slot_sync"), packetBuffer -> {
			packetBuffer.writeBlockPos(pos);
			packetBuffer.writeCompoundTag(slotConfig.serializeNBT());
		});
	}

	public static NetworkPacket createPacketSendObject(int id, Object value, Container container){
		return NetworkManager.createPacket(new ResourceLocation("reborncore", "send_object"), packetBuffer -> {
			packetBuffer.writeInt(id);
			packetBuffer.writeObject(value);
			packetBuffer.writeInt(container.getClass().getName().length());
			packetBuffer.writeString(container.getClass().getName());
		});
	}

}
