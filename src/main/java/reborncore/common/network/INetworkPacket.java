package reborncore.common.network;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public interface INetworkPacket<T> {

	void writeData(ExtendedPacketBuffer buffer) throws IOException;

	void readData(ExtendedPacketBuffer buffer) throws IOException;

	void processData(T message, MessageContext context);

}
