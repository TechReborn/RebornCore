package reborncore.common.network.packet;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileLegacyMachineBase;

import java.io.IOException;

/**
 * Used to update certian slot detilas on the server
 */
public class PacketSlotSave implements INetworkPacket<PacketSlotSave> {

	BlockPos pos;
	SlotConfiguration.SlotConfig slotConfig;

	public PacketSlotSave(BlockPos pos, SlotConfiguration.SlotConfig slotConfig) {
		this.pos = pos;
		this.slotConfig = slotConfig;
	}

	public PacketSlotSave() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeCompoundTag(slotConfig.serializeNBT());
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		slotConfig = new SlotConfiguration.SlotConfig(buffer.readCompoundTag());
	}

	@Override
	public void processData(PacketSlotSave message, MessageContext context) {
		TileLegacyMachineBase legacyMachineBase = (TileLegacyMachineBase) context.getServerHandler().player.world.getTileEntity(pos);
		legacyMachineBase.slotConfiguration.getSlotDetails(slotConfig.getSlotID()).updateSlotConfig(slotConfig);
		legacyMachineBase.markDirty();

		PacketSlotSync packetSlotSync = new PacketSlotSync(pos, legacyMachineBase.slotConfiguration);
		NetworkManager.sendToAll(packetSlotSync);
	}
}
