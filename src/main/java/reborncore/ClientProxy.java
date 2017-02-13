package reborncore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.client.RenderEvent;
import reborncore.client.hud.StackInfoHUD;
import reborncore.client.models.HolidayRenderEvent;
import reborncore.common.multiblock.MultiblockClientTickHandler;
import reborncore.shields.client.RebornItemStackRenderer;
import reborncore.shields.client.ShieldTextureStore;

import java.util.List;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(HolidayRenderEvent.class);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(RenderEvent.class);
		MinecraftForge.EVENT_BUS.register(new StackInfoHUD());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		TileEntityItemStackRenderer.instance = new RebornItemStackRenderer(TileEntityItemStackRenderer.instance);
		MinecraftForge.EVENT_BUS.register(new MultiblockClientTickHandler());
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

	@Override
	public void getCrashData(List<String> list) {
		super.getCrashData(list);
		list.add("RenderEngine: " + (FMLClientHandler.instance().hasOptifine() ? "1" : "0"));
	}
}
