package reborncore.common.modules;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author Prospector on 28/05/16
 */
public class Module
{
	/* Module ID; format as so: modid:moduleName
	 * Example:
	 *	crystek.rawMaterials
	 */
	private String moduleID;

	/* Mod ids that must be present in order to load this module */
	private String[] modsRequired;

	/* Module ids that must be present in order to load this module */
	private String[] modulesRequired;

	public boolean isActive = true;

	public Module(String id)
	{
		moduleID = id;
	}

	public void preInit(FMLPreInitializationEvent event)
	{

	}

	public void init(FMLInitializationEvent event)
	{

	}

	public void postInit(FMLPostInitializationEvent event)
	{

	}

	public String getModuleID()
	{
		return moduleID;
	}

	public String[] getModsRequired()
	{
		return modsRequired;
	}

	public void setModsRequired(String... modIds)
	{
		modsRequired = modIds;
	}

	public String[] getModulesRequired()
	{
		return modulesRequired;
	}

	public void setModulesRequired(String... moduleIDs)
	{
		modulesRequired = moduleIDs;
	}
}
