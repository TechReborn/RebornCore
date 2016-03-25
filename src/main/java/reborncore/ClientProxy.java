package reborncore;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.shields.client.RebornItemStackRenderer;
import reborncore.shields.client.ShieldTextureLoader;

public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ShieldTextureLoader.instance = new ShieldTextureLoader(event.getModConfigurationDirectory());
		MinecraftForge.EVENT_BUS.register(ShieldTextureLoader.instance);
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
		TileEntityItemStackRenderer.instance = new RebornItemStackRenderer();
	}
}
