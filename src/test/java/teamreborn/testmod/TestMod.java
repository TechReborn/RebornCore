package teamreborn.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "testmod")
public class TestMod {

	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		System.out.println(TestBlock.instance2.getRegistryName());
	}

}
