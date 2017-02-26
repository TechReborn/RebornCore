package teamreborn.reborncore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.init.RegistrationManager;

@Mod(name = "RebornCore", modid = "reborncore", version = "@MODVERSION@")
public class RebornCore {

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		RegistrationManager.load(event);
	}

}
