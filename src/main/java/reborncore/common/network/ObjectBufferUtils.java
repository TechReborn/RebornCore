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

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;

import reborncore.RebornCore;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public enum ObjectBufferUtils {

	STRING(String.class, (string, buffer) -> {
		buffer.writeInt(string.length());
		buffer.writeString(string);
	}, buffer -> {
		return buffer.readString(buffer.readInt());
	}),

	INT(Integer.class, (value, buffer) -> {
		buffer.writeInt(value);
	}, PacketBuffer::readInt),

	LONG(Long.class, (pos, buffer) -> {
		buffer.writeLong(pos);
	}, ExtendedPacketBuffer::readLong),

	DOUBLE(Double.class, (pos, buffer) -> {
		buffer.writeDouble(pos);
	}, ExtendedPacketBuffer::readDouble),

	FLOAT(Float.class, (pos, buffer) -> {
		buffer.writeFloat(pos);
	}, ExtendedPacketBuffer::readFloat),

	BLOCK_POS(BlockPos.class, (pos, buffer) -> {
		buffer.writeBlockPos(pos);
	}, PacketBuffer::readBlockPos),

	BIG_INT(BigInteger.class, (pos, buffer) -> {
		buffer.writeBigInt(pos);
	}, ExtendedPacketBuffer::readBigInt),

	FLUID_STACK(FluidStack.class, (pos, buffer) -> {
		buffer.writeFluidStack(pos);
	}, ExtendedPacketBuffer::readFluidStack),;

	Class clazz;
	ObjectWriter writer;
	ObjectReader reader;

	<T> ObjectBufferUtils(Class<T> clazz, ObjectWriter<T> writer, ObjectReader<T> reader) {
		this.clazz = clazz;
		this.writer = writer;
		this.reader = reader;
	}

	public static void writeObject(Object object, ExtendedPacketBuffer buffer){
		RebornCore.logHelper.fatal(object.getClass());
		ObjectBufferUtils utils = Arrays.stream(values()).filter(objectBufferUtils -> objectBufferUtils.clazz == object.getClass()).findFirst().orElse(null);
		Objects.requireNonNull(utils, "No support found for " + object.getClass());
		buffer.writeInt(utils.ordinal());
		utils.writer.write(object, buffer);
	}

	public static Object readObject(ExtendedPacketBuffer buffer){
		ObjectBufferUtils utils = values()[buffer.readInt()];
		Objects.requireNonNull(utils, "Could not find reader");
		return utils.reader.read(buffer);
	}

	private interface ObjectWriter<T> {
		void write(T object, ExtendedPacketBuffer buffer);
	}

	private interface ObjectReader<T> {
		T read(ExtendedPacketBuffer buffer);
	}

}
