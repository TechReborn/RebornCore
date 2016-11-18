package reborncore.common.network.packet;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.common.network.RegisterPacketEvent;

/**
 * Created by modmuss50 on 20/09/2016.
 */
public class RebornPackets {

	@SubscribeEvent
	public void loadPackets(RegisterPacketEvent event) {
		event.registerPacket(CustomDescriptionPacket.class, Side.CLIENT);
	}

}
