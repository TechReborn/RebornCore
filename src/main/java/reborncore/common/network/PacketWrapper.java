package reborncore.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.Validate;
import reborncore.RebornCore;

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
			if(message == null || message.packet == null){
				return null;
			}
			try{
				message.packet.processData(message.packet, ctx);
			} catch (Exception e){
				RebornCore.logHelper.error("Packet " + message.packet.getClass() + " could not be handled, it will be ignored, please report to the mod dev.");
				e.printStackTrace();
			}

			return null;
		}
	}
}
