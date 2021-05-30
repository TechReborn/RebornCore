/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import reborncore.RebornCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

public class ExtendedPacketBuffer extends PacketByteBuf {
	public ExtendedPacketBuffer(ByteBuf wrapped) {
		super(wrapped);
	}

	protected void writeObject(Object object) {
		ObjectBufferUtils.writeObject(object, this);
	}

	protected Object readObject() {
		return ObjectBufferUtils.readObject(this);
	}

	@Deprecated // Remove in 1.17
	public void writeBigInt(BigInteger bigInteger) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ObjectOutputStream outputStream = new ObjectOutputStream(baos);
			outputStream.writeObject(bigInteger);
			writeByteArray(baos.toByteArray());
		} catch (IOException e) {
			RebornCore.LOGGER.error(e);
		}
	}

	@Deprecated // Remove in 1.17
	public BigInteger readBigInt(){
		try (ValidatingObjectInputStream inputStream = new ValidatingObjectInputStream(new ByteArrayInputStream(readByteArray()))) {
			inputStream.accept(BigInteger.class);
			return (BigInteger) inputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			RebornCore.LOGGER.error(e);
			return BigInteger.ZERO;
		}
	}
}
