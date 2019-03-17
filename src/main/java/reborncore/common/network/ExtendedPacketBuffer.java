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
import net.minecraft.network.PacketBuffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
}
