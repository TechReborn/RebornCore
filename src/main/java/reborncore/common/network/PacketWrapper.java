/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

/**
 * Created by Mark on 01/09/2016.
 */
public class PacketWrapper implements IMessage {

	INetworkPacket packet;

	public PacketWrapper(INetworkPacket packet) {
		this.packet = packet;
	}

	public PacketWrapper() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			packet = NetworkManager.packetHashMap.get(buf.readInt()).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		try {
			packet.readData(new ExtendedPacketBuffer(buf));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Validate.notNull(packet);
		buf.writeInt(NetworkManager.packetHashMapReverse.get(packet.getClass()));
		try {
			packet.writeData(new ExtendedPacketBuffer(buf));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class PacketWrapperHandler implements IMessageHandler<PacketWrapper, IMessage> {

		@Override
		public IMessage onMessage(PacketWrapper message, MessageContext ctx) {
			message.packet.processData(message.packet, ctx);
			return null;
		}
	}
}
