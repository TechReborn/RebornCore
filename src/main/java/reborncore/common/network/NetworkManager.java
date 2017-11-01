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
import net.minecraftforge.fml.relauncher.Side;
import reborncore.RebornCore;

import java.util.HashMap;
import java.util.zip.CRC32;

public class NetworkManager {

	public static HashMap<Class<? extends INetworkPacket>, SimpleNetworkWrapper> packetWrapperMap = new HashMap<>();
	public static HashMap<String, SimpleNetworkWrapper> packageWrapperMap = new HashMap<>();
	private static HashMap<SimpleNetworkWrapper, IntStore> wrapperIdList = new HashMap<>();


	public static HashMap<Integer, Class<? extends INetworkPacket>> packetHashMap = new HashMap<>();
	public static HashMap<Class<? extends INetworkPacket>, Integer> packetHashMapReverse = new HashMap<>();

	public static void load() {
		MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent());
	}

	public static void sendToServer(INetworkPacket packet) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		}
		getWrapperForPacket(packet.getClass()).sendToServer(new PacketWrapper(packet));
	}

	public static void sendToAllAround(INetworkPacket packet, NetworkRegistry.TargetPoint point) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		}
		getWrapperForPacket(packet.getClass()).sendToAllAround(new PacketWrapper(packet), point);
	}

	public static void sendToAll(INetworkPacket packet) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		} else {
			getWrapperForPacket(packet.getClass()).sendToAll(new PacketWrapper(packet));
		}
	}

	public static void sendToPlayer(INetworkPacket packet, EntityPlayerMP playerMP) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		} else {
			getWrapperForPacket(packet.getClass()).sendTo(new PacketWrapper(packet), playerMP);
		}
	}

	public static void sendToWorld(INetworkPacket packet, World world) {
		if (!packetHashMap.containsValue(packet.getClass())) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		} else {
			getWrapperForPacket(packet.getClass()).sendToDimension(new PacketWrapper(packet), world.provider.getDimension());
		}
	}

	public static SimpleNetworkWrapper getWrapperForPacket(Class<? extends INetworkPacket> packetClass){
		return packetWrapperMap.get(packetClass);
	}

	public static SimpleNetworkWrapper createOrGetNetworkWrapper(Class<? extends INetworkPacket> packetClass){
		String wrapperName = getWrapperName(packetClass);
		if(packageWrapperMap.containsKey(wrapperName)){
			return packageWrapperMap.get(wrapperName);
		} else {
			SimpleNetworkWrapper newNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(wrapperName);
			RebornCore.logHelper.info("Created new network wrapper " + wrapperName);
			packageWrapperMap.put(wrapperName, newNetworkWrapper);
			return newNetworkWrapper;
		}
	}

	public static String getWrapperName(Class<? extends INetworkPacket> packetClass){
		String packageName = packetClass.getCanonicalName().substring(0, packetClass.getCanonicalName().lastIndexOf("."));
		CRC32 crc = new CRC32();
		crc.update(packageName.getBytes());
		//Packet network names have a max size of 20
		//3 chars on the rc bit, 11 on the package name, 1 to the & and the last 5 on the hash
		return "rc&" + packageName.substring(0, 11) + "&" + Long.toString(crc.getValue()).substring(0, 5);
	}

	public static void registerPacket(Class<? extends INetworkPacket> packetClass, Side side){
		SimpleNetworkWrapper wrapper = createOrGetNetworkWrapper(packetClass);
		int id = getNextIDForWrapper(wrapper);
		wrapper.registerMessage(PacketWrapper.PacketWrapperHandler.class, PacketWrapper.class, id, side);
		packetWrapperMap.put(packetClass, wrapper);
		RebornCore.logHelper.info("Registed packet to " + getWrapperName(packetClass) + " side: " + side + " id:" + id);
	}

	public static int getNextIDForWrapper(SimpleNetworkWrapper networkWrapper){
		if(wrapperIdList.containsKey(networkWrapper)){
			wrapperIdList.get(networkWrapper).id++;
			return wrapperIdList.get(networkWrapper).id;
		} else {
			wrapperIdList.put(networkWrapper, new IntStore());
			return 0;
		}
	}

	private static class IntStore {
		int id = 0;
	}

}
