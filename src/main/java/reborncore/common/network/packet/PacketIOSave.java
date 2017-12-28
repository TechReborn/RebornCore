package reborncore.common.network.packet;

import net.minecraft.util.math.BlockPos;
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
public class PacketIOSave implements INetworkPacket<PacketIOSave> {

	BlockPos pos;
	int slotID;
	boolean input, output, filter;

	public PacketIOSave(BlockPos pos, int slotID, boolean input, boolean output, boolean filter) {
		this.pos = pos;
		this.slotID = slotID;
		this.input = input;
		this.output = output;
		this.filter = filter;
	}

	public PacketIOSave() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeInt(slotID);
		buffer.writeBoolean(input);
		buffer.writeBoolean(output);
		buffer.writeBoolean(filter);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		slotID = buffer.readInt();
		input = buffer.readBoolean();
		output = buffer.readBoolean();
		filter = buffer.readBoolean();
	}

	@Override
	public void processData(PacketIOSave message, MessageContext context) {
		TileLegacyMachineBase legacyMachineBase = (TileLegacyMachineBase) context.getServerHandler().player.world.getTileEntity(pos);
		SlotConfiguration.SlotConfigHolder holder = legacyMachineBase.slotConfiguration.getSlotDetails(slotID);
		if(holder == null){
			return;
		}
		holder.setInput(input);
		holder.setOutput(output);
		holder.setfilter(filter);

		//Syncs back to the client
		PacketSlotSync packetSlotSync = new PacketSlotSync(pos, legacyMachineBase.slotConfiguration);
		NetworkManager.sendToAll(packetSlotSync);
	}
}
