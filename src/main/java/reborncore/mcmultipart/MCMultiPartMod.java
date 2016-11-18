package reborncore.mcmultipart;

import reborncore.mcmultipart.block.BlockMultipartContainer;
import reborncore.mcmultipart.block.TileCoverable;
import reborncore.mcmultipart.block.TileMultipartContainer;
import reborncore.mcmultipart.network.MultipartNetworkHandler;
import reborncore.mcmultipart.util.MCMPEventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = MCMultiPartMod.MODID, name = MCMultiPartMod.NAME, version = MCMultiPartMod.VERSION, acceptedMinecraftVersions = "[1.11]")
public class MCMultiPartMod {

    public static final String MODID = "reborncore-mcmultipart", NAME = "reborncore-MCMultiPart", VERSION = "@MODVERSION@";

    @SidedProxy(serverSide = "reborncore.mcmultipart.MCMPCommonProxy", clientSide = "reborncore.mcmultipart.client.MCMPClientProxy")
    public static MCMPCommonProxy proxy;

    public static BlockMultipartContainer multipart;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // Register the multipart container Block and TileEntity
        GameRegistry.register(multipart = new BlockMultipartContainer(), new ResourceLocation(MCMultiPartMod.MODID, "multipart"));
        GameRegistry.registerTileEntity(TileMultipartContainer.Ticking.class, "reborncore.mcmultipart:multipart.ticking");
        GameRegistry.registerTileEntity(TileMultipartContainer.class, "reborncore.mcmultipart:multipart.nonticking");
        // Register the default coverable tile for use with blocks that want to host covers, but don't require a TE
        GameRegistry.registerTileEntity(TileCoverable.class, "reborncore.mcmultipart:coverable");

        proxy.preInit();

        MinecraftForge.EVENT_BUS.register(new MCMPEventHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        MultipartNetworkHandler.init();

        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

}
