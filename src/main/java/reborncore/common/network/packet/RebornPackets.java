package reborncore.common.network.packet;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.RebornCore;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.RegisterPacketEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by modmuss50 on 20/09/2016.
 */
public class RebornPackets {

	public static List<Pair<Side, Class<? extends INetworkPacket>>> packetList = new ArrayList<>();

	@SubscribeEvent
	public void loadPackets(RegisterPacketEvent event) {
		packetList.forEach(pair -> event.registerPacket(pair.getRight(), pair.getLeft()));
		event.registerPacket(CustomDescriptionPacket.class, Side.CLIENT);
		RebornCore.logHelper.info("Registered " + packetList.size() + " packet(s)");
	}


}
