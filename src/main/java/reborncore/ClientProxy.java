package reborncore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.client.hud.StackInfoHUD;
import reborncore.client.models.HolidayRenderEvent;
import reborncore.shields.client.RebornItemStackRenderer;
import reborncore.shields.client.ShieldTextureStore;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(HolidayRenderEvent.class);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new StackInfoHUD());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		TileEntityItemStackRenderer.instance = new RebornItemStackRenderer(TileEntityItemStackRenderer.instance);
	}

	@Override
	public void loadShieldTextures() {
		super.loadShieldTextures();
		ShieldTextureStore.load();
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}
}
