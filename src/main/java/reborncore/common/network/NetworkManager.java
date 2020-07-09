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

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NetworkManager {


	public static Packet<ServerPlayPacketListener> createServerBoundPacket(Identifier identifier, Consumer<ExtendedPacketBuffer> packetBufferConsumer) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		packetBufferConsumer.accept(new ExtendedPacketBuffer(buf));
		return new CustomPayloadC2SPacket(identifier, buf);
	}

	public static void registerServerBoundHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer) {
		ServerSidePacketRegistry.INSTANCE.register(identifier, (packetContext, packetByteBuf) -> consumer.accept(new ExtendedPacketBuffer(packetByteBuf), packetContext));
	}

	public static Packet<ClientPlayPacketListener> createClientBoundPacket(Identifier identifier, Consumer<ExtendedPacketBuffer> packetBufferConsumer) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		packetBufferConsumer.accept(new ExtendedPacketBuffer(buf));
		return new CustomPayloadS2CPacket(identifier, buf);
	}

	public static void registerClientBoundHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer) {
		ClientSidePacketRegistry.INSTANCE.register(identifier, (packetContext, packetByteBuf) -> consumer.accept(new ExtendedPacketBuffer(packetByteBuf), packetContext));
	}


	public static void sendToServer(Packet<?> packet) {
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
	}

	public static void sendToAll(Packet<?> packet, MinecraftServer server) {
		server.getPlayerManager().sendToAll(packet);
	}

	public static void sendToPlayer(Packet<?> packet, ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.networkHandler.sendPacket(packet);
	}

	public static void sendToWorld(Packet<?> packet, ServerWorld world) {
		world.getPlayers().forEach(serverPlayerEntity -> sendToPlayer(packet, serverPlayerEntity));
	}


	public static void sendToTracking(Packet<?> packet, ServerWorld world, BlockPos pos) {
		//TODO fix this to be better
		sendToWorld(packet, world);

	}

	public static void sendToTracking(Packet<?> packet, BlockEntity blockEntity) {
		sendToTracking(packet, (ServerWorld) blockEntity.getWorld(), blockEntity.getPos());
	}


}
