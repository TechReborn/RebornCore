package reborncore.common.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.HashMap;

public class NetworkManager {

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("rebornCore");

	public static HashMap<Integer, Class<? extends INetworkPacket>> packetHashMap = new HashMap<>();
	public static HashMap<Class<? extends INetworkPacket>, Integer> packetHashMapReverse = new HashMap<>();

	public static void load(){
		MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent(NETWORK_WRAPPER));
	}


	public static void sendToServer(INetworkPacket packet){
		if(!packetHashMap.containsValue(packet.getClass())){
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		}
		NETWORK_WRAPPER.sendToServer(new PacketWrapper(packet));
	}
}
