/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.tile.RebornMachineTile;

import java.io.IOException;

/**
 * Used to update certian slot detilas on the server
 */
public class PacketConfigSave implements INetworkPacket<PacketConfigSave> {

	BlockPos pos;
	SlotConfiguration slotConfig;
	NBTTagCompound tagCompound;

	public PacketConfigSave(BlockPos pos, SlotConfiguration slotConfig) {
		this.pos = pos;
		this.slotConfig = slotConfig;
	}

	public PacketConfigSave() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeCompoundTag(slotConfig.serializeNBT());
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		tagCompound = buffer.readCompoundTag();
	}

	@Override
	public void processData(PacketConfigSave message, MessageContext context) {
		RebornMachineTile legacyMachineBase = (RebornMachineTile) context.getServerHandler().player.world.getTileEntity(pos);
		legacyMachineBase.slotConfiguration.deserializeNBT(tagCompound);
		legacyMachineBase.markDirty();

		PacketSlotSync packetSlotSync = new PacketSlotSync(pos, legacyMachineBase.slotConfiguration);
		NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
	}
}
