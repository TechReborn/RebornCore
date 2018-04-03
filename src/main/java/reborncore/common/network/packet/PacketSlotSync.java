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

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.TileLegacyMachineBase;

import java.io.IOException;

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
		if(!RebornCore.proxy.getClientWorld().isBlockLoaded(pos, false)){
			return;
		}
		TileLegacyMachineBase machineBase = (TileLegacyMachineBase) RebornCore.proxy.getClientWorld().getTileEntity(pos);
		if(machineBase == null || machineBase.slotConfiguration == null || slotConfig == null || slotConfig.getSlotDetails() == null){
			RebornCore.logHelper.error("Failed to sync slot data to " + pos);
		}
		Minecraft.getMinecraft().addScheduledTask(() -> slotConfig.getSlotDetails().forEach(slotConfigHolder -> machineBase.slotConfiguration.updateSlotDetails(slotConfigHolder)));

	}
}
