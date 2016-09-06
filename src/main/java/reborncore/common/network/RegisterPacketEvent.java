package reborncore.common.network;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Constructor;

public class RegisterPacketEvent extends Event {

	SimpleNetworkWrapper wrapper;

	public RegisterPacketEvent(SimpleNetworkWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public void registerPacket(Class<? extends INetworkPacket> packet, Side processingSide) {
		int id = getNextID();
		if (packet.getName() == INetworkPacket.class.getName()) {
			throw new RuntimeException("Cannot register a INetworkPacket, please register a child of this");
		}
		boolean hasEmptyConstructor = false;
		for (Constructor constructor : packet.getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				hasEmptyConstructor = true;
			}
		}
		if (!hasEmptyConstructor) {
			throw new RuntimeException("The packet " + packet.getName() + " does not have an empty constructor");
		}
		NetworkManager.packetHashMap.put(id, packet);
		NetworkManager.packetHashMapReverse.put(packet, id);
		wrapper.registerMessage(PacketWrapper.PacketWrapperHandler.class, PacketWrapper.class, id, processingSide);
	}

	public static int getNextID() {
		return NetworkManager.packetHashMap.size() + 1;
	}
}
