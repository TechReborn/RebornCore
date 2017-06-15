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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.HashMap;

public class NetworkManager {

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("rebornCore");

	public static HashMap<Integer, Class<? extends INetworkPacket>> packetHashMap = new HashMap<>();
	public static HashMap<Class<? extends INetworkPacket>, Integer> packetHashMapReverse = new HashMap<>();

	public static void load() {
		MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent(NETWORK_WRAPPER));
	}

	public static void sendToServer(INetworkPacket packet) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		}
		NETWORK_WRAPPER.sendToServer(new PacketWrapper(packet));
	}

	public static void sendToAllAround(INetworkPacket packet, NetworkRegistry.TargetPoint point) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		}
		NETWORK_WRAPPER.sendToAllAround(new PacketWrapper(packet), point);
	}

	public static void sendToAll(INetworkPacket packet) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		} else {
			NETWORK_WRAPPER.sendToAll(new PacketWrapper(packet));
		}
	}

	public static void sendToPlayer(INetworkPacket packet, EntityPlayerMP playerMP) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		} else {
			NETWORK_WRAPPER.sendTo(new PacketWrapper(packet), playerMP);
		}
	}

	public static void sendToWorld(INetworkPacket packet, World world) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		} else {
			NETWORK_WRAPPER.sendToDimension(new PacketWrapper(packet), world.provider.getDimension());
		}
	}
}
