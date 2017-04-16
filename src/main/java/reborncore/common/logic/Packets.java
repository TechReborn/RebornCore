package reborncore.common.logic;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.common.network.RegisterPacketEvent;

/**
 * Created by Gigabit101 on 16/04/2017.
 */
//TODO move this
public class Packets
{
    @SubscribeEvent
    public static void loadPackets(RegisterPacketEvent event)
    {
        event.registerPacket(PacketButtonID.class, Side.SERVER);
    }
}
