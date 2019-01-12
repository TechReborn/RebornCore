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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import reborncore.Distribution;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkManager {

	private static final SimpleChannel INTERNAL_HANDLER = NetworkRegistry.ChannelBuilder
		.named(new ResourceLocation("reborncore", "networking"))
		.simpleChannel();

	public static void load() {
		MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent());

		INTERNAL_HANDLER.registerMessage(0, PacketWrapper.class, new BiConsumer<PacketWrapper, PacketBuffer>() {
			@Override
			public void accept(PacketWrapper packetWrapper, PacketBuffer buffer) {

			}
		}, new Function<PacketBuffer, PacketWrapper>() {
			@Override
			public PacketWrapper apply(PacketBuffer buffer) {
				return null;
			}
		}, new BiConsumer<PacketWrapper, Supplier<NetworkEvent.Context>>() {
			@Override
			public void accept(PacketWrapper packetWrapper, Supplier<NetworkEvent.Context> contextSupplier) {

			}
		});

	}

	public static ArrayList<INetworkPacket> packetList = new ArrayList<>();

	public static void sendToServer(INetworkPacket packet) {
		INTERNAL_HANDLER.sendToServer(new PacketWrapper(packet));
	}

//	public static void sendToAllAround(INetworkPacket packet, NetworkRegistry.TargetPoint point) {
//	//	INTERNAL_HANDLER.sendToAllAround(new PacketWrapper(packet), point);
//	}

	public static void sendToAll(INetworkPacket packet) {
	//INTERNAL_HANDLER.sendToAll(new PacketWrapper(packet));
	}

	public static void sendToPlayer(INetworkPacket packet, EntityPlayerMP playerMP) {
		//INTERNAL_HANDLER.sendTo(new PacketWrapper(packet), playerMP);
	}

	public static void sendToWorld(INetworkPacket packet, World world) {
		//INTERNAL_HANDLER.sendToDimension(new PacketWrapper(packet), world.provider.getDimension());
	}


	public static void registerPacket(Class<? extends INetworkPacket> packetClass, Distribution side){
		throw new UnsupportedOperationException("Not working just yet");
	}


}
