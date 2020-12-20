package reborncore.common.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class IdentifiedPacket {

	private final Identifier channel;
	private final PacketByteBuf packetByteBuf;

	public IdentifiedPacket(Identifier channel, PacketByteBuf packetByteBuf) {
		this.channel = channel;
		this.packetByteBuf = packetByteBuf;
	}

	public Identifier getChannel() {
		return channel;
	}

	public PacketByteBuf getPacketByteBuf() {
		return packetByteBuf;
	}
}
