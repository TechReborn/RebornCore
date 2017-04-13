package reborncore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.mixin.implementations.forge.MixinForgeLoadingCore;

import java.util.ArrayList;
import java.util.List;

public class CommonProxy {

	public List<String> invalidFingerprints = new ArrayList<>();

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public void loadShieldTextures() {

	}

	public World getClientWorld() {
		return null;
	}

	public EntityPlayer getPlayer() {
		return null;
	}

	public void getCrashData(List<String> list) {
		list.add("Plugin Engine: " + (Loader.isModLoaded("sponge") ? "1" : "0"));
		list.add("RebornCore Version: " + RebornCore.MOD_VERSION);
		list.add("Mixin Status: " + (MixinForgeLoadingCore.mixinsLoaded ? "1" : "0"));
		list.add("Runtime Debofucsation " + (FMLForgePlugin.RUNTIME_DEOBF ? "1" : "0"));
		list.addAll(invalidFingerprints);
	}
}
