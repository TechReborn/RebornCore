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

public class PacketSendLong implements INetworkPacket<PacketSendLong> {

	int id;
	long value;
	String container;

	public PacketSendLong(int id, long value, Container container) {
		this.id = id;
		this.value = value;
		this.container = container.getClass().getName();
	}

	public PacketSendLong() {
	}

	public PacketSendLong(int id, long value) {
		this.id = id;
		this.value = value;
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeInt(id);
		buffer.writeLong(value);
		buffer.writeInt(container.length());
		buffer.writeString(container);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		id = buffer.readInt();
		value = buffer.readLong();
		container = buffer.readString(buffer.readInt());
	}

	@Override
	public void processData(PacketSendLong message, MessageContext context) {
		handle();
	}

	@SideOnly(Side.CLIENT)
	public void handle(){
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof GuiContainer){
			Container container = ((GuiContainer) gui).inventorySlots;
			if(container instanceof IExtendedContainerListener){
				((IExtendedContainerListener) container).handleLong(id, value);
			}
		}
	}
}
