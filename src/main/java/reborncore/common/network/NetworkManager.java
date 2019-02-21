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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkManager {

	private static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
		.named(new ResourceLocation("reborncore", "network"))
		.clientAcceptedVersions(a -> true)
		.serverAcceptedVersions(a -> true)
		.networkProtocolVersion(() -> NetworkHooks.NETVERSION)
		.simpleChannel();

	private static Map<ResourceLocation, BiConsumer<ExtendedPacketBuffer, NetworkEvent.Context>> packetHandlers = new HashMap<>();

	static {
		channel.registerMessage(0, ForgeMessage.class, ForgeMessage::encode, ForgeMessage::decode, ForgeMessage::handle);
	}

	public static NetworkPacket createPacket(ResourceLocation resourceLocation, Consumer<ExtendedPacketBuffer> packetBufferConsumer) {
		return new ForgeMessage(resourceLocation, packetBufferConsumer);
	}

	public static void registerPacketHandler(ResourceLocation resourceLocation, BiConsumer<ExtendedPacketBuffer, NetworkEvent.Context> consumer) {
		if (packetHandlers.containsKey(resourceLocation)) {
			throw new RuntimeException("Packet handler already registered for " + resourceLocation);
		}
		packetHandlers.put(resourceLocation, consumer);
	}

	public static void send(PacketDistributor.PacketTarget packetTarget, NetworkPacket packet) {
		channel.send(packetTarget, packet);
	}

	public static void sendToServer(NetworkPacket packet) {
		send(PacketDistributor.SERVER.noArg(), packet);
	}

	public static void sendToAllAround(NetworkPacket packet, PacketDistributor.TargetPoint point) {
		send(PacketDistributor.NEAR.with(() -> point), packet);
	}

	public static void sendToAll(NetworkPacket packet) {
		send(PacketDistributor.ALL.noArg(), packet);

	}

	public static void sendToPlayer(NetworkPacket packet, EntityPlayerMP playerMP) {
		send(PacketDistributor.PLAYER.with(() -> playerMP), packet);
	}

	public static void sendToWorld(NetworkPacket packet, World world) {
		send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), packet);
	}

	public static void sendToTracking(NetworkPacket packet, Chunk chunk) {
		send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}

	public static void sendToTracking(NetworkPacket packet, World world, BlockPos pos) {
		sendToTracking(packet, world.getChunk(pos));
	}

	public static void sendToTracking(NetworkPacket packet, TileEntity tileEntity) {
		sendToTracking(packet, tileEntity.getWorld(), tileEntity.getPos());
	}

	private static class ForgeMessage extends NetworkPacket {

		ResourceLocation resourceLocation;
		Consumer<ExtendedPacketBuffer> encodeBufferConsumer;
		ExtendedPacketBuffer decodeBuffer;

		private ForgeMessage(ResourceLocation resourceLocation, Consumer<ExtendedPacketBuffer> encodeBufferConsumer) {
			this(resourceLocation);
			this.encodeBufferConsumer = encodeBufferConsumer;
		}

		private ForgeMessage(ResourceLocation resourceLocation, ExtendedPacketBuffer decodeBuffer) {
			this(resourceLocation);
			this.decodeBuffer = decodeBuffer;
		}

		private ForgeMessage(ResourceLocation resourceLocation) {
			this.resourceLocation = resourceLocation;
			if (!packetHandlers.containsKey(resourceLocation)) {
				throw new RuntimeException("No packet handler found for " + resourceLocation);
			}
		}

		private static void encode(ForgeMessage msg, PacketBuffer buf) {
			buf.writeInt(msg.resourceLocation.toString().length());
			buf.writeString(msg.resourceLocation.toString());
			msg.encodeBufferConsumer.accept(new ExtendedPacketBuffer(buf));
		}

		private static ForgeMessage decode(PacketBuffer buf) {
			//Clone this as im not sure what is done with the other one, do we need to also
			PacketBuffer clonedBuf = new PacketBuffer(buf.duplicate());
			ResourceLocation resourceLocation = new ResourceLocation(clonedBuf.readString(clonedBuf.readInt()));
			return new ForgeMessage(resourceLocation, new ExtendedPacketBuffer(clonedBuf));
		}

		private static void handle(ForgeMessage msg, Supplier<NetworkEvent.Context> ctx) {
			if (!packetHandlers.containsKey(msg.resourceLocation)) {
				throw new RuntimeException("No packet handler found for " + msg.resourceLocation);
			}
			BiConsumer<ExtendedPacketBuffer, NetworkEvent.Context> packetConsumer = packetHandlers.get(msg.resourceLocation);
			packetConsumer.accept(msg.decodeBuffer, ctx.get());
			msg.decodeBuffer.release();
		}
	}

}
