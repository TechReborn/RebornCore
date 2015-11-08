package reborncore.common.packets;

import cpw.mods.fml.common.eventhandler.Event;

public class AddDiscriminatorEvent extends Event {

    public PacketHandler packetHandler;

    public AddDiscriminatorEvent(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
