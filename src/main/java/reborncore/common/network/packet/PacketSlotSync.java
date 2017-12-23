package reborncore.common.network.packet;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileLegacyMachineBase;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Used to sync all the slot details to the client
 */
public class  PacketSlotSync implements INetworkPacket<PacketSlotSync> {

	BlockPos pos;
	SlotConfiguration slotConfig;

	public PacketSlotSync(BlockPos pos, SlotConfiguration slotConfig) {
		this.pos = pos;
		this.slotConfig = slotConfig;
	}

	public PacketSlotSync() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeCompoundTag(slotConfig.serializeNBT());
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		slotConfig = new SlotConfiguration(buffer.readCompoundTag());
	}

	@Override
	public void processData(PacketSlotSync message, MessageContext context) {
		TileLegacyMachineBase machineBase = (TileLegacyMachineBase) RebornCore.proxy.getClientWorld().getTileEntity(pos);
		if(machineBase == null){
			RebornCore.logHelper.error("Failed to sync slot data to " + pos);
		}
		slotConfig.getSlotDetails().forEach(slotConfigHolder -> machineBase.slotConfiguration.updateSlotDetails(slotConfigHolder));
	}
}
