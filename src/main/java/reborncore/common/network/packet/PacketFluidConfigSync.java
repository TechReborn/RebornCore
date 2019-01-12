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
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.TileMachineBase;

import java.io.IOException;

/**
 * Used to sync all the slot details to the client
 */
public class PacketFluidConfigSync implements INetworkPacket {

	BlockPos pos;
	FluidConfiguration fluidConfiguration;

	public PacketFluidConfigSync(BlockPos pos, FluidConfiguration fluidConfiguration) {
		this.pos = pos;
		this.fluidConfiguration = fluidConfiguration;
	}

	public PacketFluidConfigSync() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeCompoundTag(fluidConfiguration.serializeNBT());
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		fluidConfiguration = new FluidConfiguration(buffer.readCompoundTag());
	}

	@Override
	public void processData(MessageContext context) {
		if (!RebornCore.proxy.getClientWorld().isBlockLoaded(pos, false)) {
			return;
		}
		TileMachineBase machineBase = (TileMachineBase) RebornCore.proxy.getClientWorld().getTileEntity(pos);
		if (machineBase == null || machineBase.fluidConfiguration == null || fluidConfiguration == null) {
			RebornCore.LOGGER.error("Failed to sync fluid config data to " + pos);
		}
		Minecraft.getInstance().addScheduledTask(() -> {
			fluidConfiguration.getAllSides().forEach(fluidConfig -> machineBase.fluidConfiguration.updateFluidConfig(fluidConfig));
			machineBase.fluidConfiguration.setInput(fluidConfiguration.autoInput());
			machineBase.fluidConfiguration.setOutput(fluidConfiguration.autoOutput());
		});


	}
}
