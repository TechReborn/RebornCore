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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

import java.io.IOException;

public class CustomDescriptionPacket implements INetworkPacket<CustomDescriptionPacket> {

	private BlockPos blockPos;
	private NBTTagCompound nbt;

	public CustomDescriptionPacket(BlockPos blockPos, NBTTagCompound nbt) {
		this.blockPos = blockPos;
		this.nbt = nbt;
	}

	public CustomDescriptionPacket() {
	}

	public CustomDescriptionPacket(TileEntity tileEntity) {
		this.blockPos = tileEntity.getPos();
		this.nbt = tileEntity.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) {
		buffer.writeBlockPos(blockPos);
		buffer.writeCompoundTag(nbt);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		blockPos = buffer.readBlockPos();
		nbt = buffer.readCompoundTag();
	}

	@Override
	public void processData(CustomDescriptionPacket message, MessageContext context) {
		if (message.blockPos == null || message.nbt == null) {
			return;
		}
		World world = RebornCore.proxy.getClientWorld();
		if (world.isBlockLoaded(message.blockPos)) {
			TileEntity tileentity = world.getTileEntity(message.blockPos);
			if (tileentity != null && message.nbt != null) {
				tileentity.readFromNBT(message.nbt);
			}
		}
	}
}
