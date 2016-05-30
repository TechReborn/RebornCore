package reborncore.common.modules;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;

/**
 * @author Prospector on 28/05/16
 */
public class ModuleRegistry
{
	public HashMap<Module, String> modules = new HashMap<Module, String>();

	public void addModules()
	{
		checkRequirements();
	}

	protected void checkRequirements(){
		for (Module i : modules.keySet())
			hasRequirements(i);
	}

	public void preInit(FMLPreInitializationEvent event)
	{
		addModules();
		for (Module i : modules.keySet())
		{
			if (i.isActive)
				i.preInit(event);
		}
	}

	public void init(FMLInitializationEvent event)
	{
		for (Module i : modules.keySet())
		{
			if (i.isActive)
				i.init(event);
		}
	}

	public void postInit(FMLPostInitializationEvent event)
	{
		for (Module i : modules.keySet())
		{
			if (i.isActive)
				i.postInit(event);
		}
	}

	public boolean hasRequirements(Module module)
	{
		String[] reqMods = module.getModsRequired();
		String[] reqModules = module.getModulesRequired();

		if (reqMods != null)
			for (String i : reqMods)
			{
				if (!Loader.isModLoaded(i))
				{
					module.isActive = false;
					return false;
				}
			}

		if (reqModules != null)
			for (String i : reqModules)
			{
				if (!modules.values().contains(i))
				{
					module.isActive = false;
					return false;
				}
			}

		return true;
	}
}
