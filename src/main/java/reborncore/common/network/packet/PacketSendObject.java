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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import reborncore.client.containerBuilder.builder.IExtendedContainerListener;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

import java.io.IOException;

public class PacketSendObject implements INetworkPacket {

	int id;
	Object value;
	String container;

	public PacketSendObject(int id, Object value, Container container) {
		this.id = id;
		this.value = value;
		this.container = container.getClass().getName();
	}

	public PacketSendObject() {
	}

	public PacketSendObject(int id, long value) {
		this.id = id;
		this.value = value;
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeInt(id);
		buffer.writeObject(value);
		buffer.writeInt(container.length());
		buffer.writeString(container);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		id = buffer.readInt();
		value = buffer.readObject();
		container = buffer.readString(buffer.readInt());
	}

	@Override
	public void processData(NetworkEvent.Context context) {
		handle();
	}

	@OnlyIn(Dist.CLIENT)
	public void handle() {
		GuiScreen gui = Minecraft.getInstance().currentScreen;
		if (gui instanceof GuiContainer) {
			Container container = ((GuiContainer) gui).inventorySlots;
			if (container instanceof IExtendedContainerListener) {
				((IExtendedContainerListener) container).handleObject(id, value);
			}
		}
	}
}
