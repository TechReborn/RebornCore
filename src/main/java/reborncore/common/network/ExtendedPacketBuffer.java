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

package reborncore.common.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import reborncore.RebornCore;

import java.io.*;
import java.math.BigInteger;

public class ExtendedPacketBuffer extends PacketBuffer {
	public ExtendedPacketBuffer(ByteBuf wrapped) {
		super(wrapped);
	}

	public void writeObject(Object object){
		ObjectBufferUtils.writeObject(object, this);
	}

	public Object readObject(){
		return ObjectBufferUtils.readObject(this);
	}

	public void writeBigInt(BigInteger bigInteger){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(baos);
			outputStream.writeObject(bigInteger);
			writeByteArray(baos.toByteArray());
		} catch (Exception e){
			throw new RuntimeException("Failed to write big int");
		}
	}

	public BigInteger readBigInt(){
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(readByteArray()));
			return (BigInteger) inputStream.readObject();
		} catch (Exception e){
			throw new RuntimeException("Failed to read big int");
		}
	}

	public void writeFluidStack(FluidStack fluidStack) {
		try {
			if (!(fluidStack != null && FluidRegistry.getFluidName(fluidStack) != null)) {
				this.writeShort(-1);
			} else {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				CompressedStreamTools.writeCompressed(fluidStack.writeToNBT(new NBTTagCompound()), byteStream);
				byte[] bytes = byteStream.toByteArray();
				this.writeShort((short) bytes.length);
				this.writeByteArray(bytes);
			}
		} catch (IOException exception) {
			RebornCore.logHelper.fatal(exception);
		}
	}

	public FluidStack readFluidStack() {
		try {
			short length = this.readShort();

			if (length < 0) {
				return null;
			} else {
				byte[] bytes = this.readByteArray(length);
				ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
				return FluidStack.loadFluidStackFromNBT(CompressedStreamTools.readCompressed(byteStream));
			}
		} catch (IOException exception) {
			RebornCore.logHelper.fatal(exception);
			return null;
		}
	}
}
