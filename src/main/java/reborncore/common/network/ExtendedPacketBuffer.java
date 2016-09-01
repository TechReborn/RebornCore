package reborncore.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;


public class ExtendedPacketBuffer extends PacketBuffer {
	public ExtendedPacketBuffer(ByteBuf wrapped) {
		super(wrapped);
	}
}
