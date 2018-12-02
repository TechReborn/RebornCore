package reborncore.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.client.containerBuilder.builder.IExtendedContainerListener;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

import java.io.IOException;

public class PacketSendObject implements INetworkPacket<PacketSendObject> {

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
	public void processData(PacketSendObject message, MessageContext context) {
		handle();
	}

	@SideOnly(Side.CLIENT)
	public void handle(){
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof GuiContainer){
			Container container = ((GuiContainer) gui).inventorySlots;
			if(container instanceof IExtendedContainerListener){
				((IExtendedContainerListener) container).handleObject(id, value);
			}
		}
	}
}
