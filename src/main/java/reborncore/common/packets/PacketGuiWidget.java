package reborncore.common.packets;

import reborncore.corelib.gui.BuildCraftContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGuiWidget extends SimplePacket {
    private byte windowId, widgetId;
    private byte[] payload;

    public PacketGuiWidget() {
        super();
    }

    public PacketGuiWidget(EntityPlayer player, int windowId, int widgetId, byte[] data) {
        super(player);
        this.windowId = (byte) windowId;
        this.widgetId = (byte) widgetId;
        this.payload = data;
    }

    @Override
    public void writeData(ByteBuf data) {
        data.writeByte(windowId);
        data.writeByte(widgetId);
        data.writeShort(payload.length);
        data.writeBytes(payload);
    }

    @Override
    public void readData(ByteBuf data) {
        windowId = data.readByte();
        widgetId = data.readByte();
        int length = data.readShort();
        payload = new byte[length];
        data.readBytes(payload);
    }

    @Override
    public void execute() {
        if (player.openContainer instanceof BuildCraftContainer && player.openContainer.windowId == windowId) {
            BuildCraftContainer bcContainer = (BuildCraftContainer) player.openContainer;
            bcContainer.handleWidgetData(widgetId, payload);
        }
    }

}