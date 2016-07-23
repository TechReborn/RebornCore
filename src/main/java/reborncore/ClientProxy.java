package reborncore;

import reborncore.corelib.client.render.FluidRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.shields.client.RebornItemStackRenderer;
import reborncore.shields.client.ShieldTextureStore;

public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		MinecraftForge.EVENT_BUS.register(FluidRenderer.INSTANCE);
		TileEntityItemStackRenderer.instance = new RebornItemStackRenderer(TileEntityItemStackRenderer.instance);
	}

	@Override
	public void loadShieldTextures() {
		super.loadShieldTextures();
		ShieldTextureStore.load();
	}
}
