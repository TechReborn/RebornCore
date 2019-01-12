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

package reborncore.common.network.packet;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileMachineBase;

/**
 * Used to update certian slot detilas on the server
 */
public class PacketIOSave implements INetworkPacket {

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
	public void writeData(ExtendedPacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(slotID);
		buffer.writeBoolean(input);
		buffer.writeBoolean(output);
		buffer.writeBoolean(filter);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) {
		pos = buffer.readBlockPos();
		slotID = buffer.readInt();
		input = buffer.readBoolean();
		output = buffer.readBoolean();
		filter = buffer.readBoolean();
	}

	@Override
	public void processData(NetworkEvent.Context context) {
		TileMachineBase legacyMachineBase = (TileMachineBase) context.getSender().world.getTileEntity(pos);
		SlotConfiguration.SlotConfigHolder holder = legacyMachineBase.slotConfiguration.getSlotDetails(slotID);
		if (holder == null) {
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
