package reborncore;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import reborncore.common.packets.PacketHandler;

@Mod(modid = RebornCore.MOD_ID, name = RebornCore.MOD_NAME, version = RebornCore.MOD_VERSION)
public class RebornCore {
    public static final String MOD_NAME = "RebornCore";
    public static final String MOD_ID = "reborncore";
    public static final String MOD_VERSION = "@MODVERSION@";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // packets
        PacketHandler.setChannels(NetworkRegistry.INSTANCE.newChannel(
                MOD_ID + "_packets", new PacketHandler()));
    }


}
