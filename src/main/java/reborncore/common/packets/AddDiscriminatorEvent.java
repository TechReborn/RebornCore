package reborncore.common.packets;

import net.minecraftforge.fml.common.eventhandler.Event;

@Deprecated
public class AddDiscriminatorEvent extends Event
{

	public PacketHandler packetHandler;

	public AddDiscriminatorEvent(PacketHandler packetHandler)
	{
		this.packetHandler = packetHandler;
	}

	public PacketHandler getPacketHandler()
	{
		return packetHandler;
	}

	@Override
	public boolean isCancelable()
	{
		return false;
	}
}
