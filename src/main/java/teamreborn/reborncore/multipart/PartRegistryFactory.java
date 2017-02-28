package teamreborn.reborncore.multipart;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Loader;
import teamreborn.reborncore.api.multipart.IPart;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RegistryTarget;
import teamreborn.reborncore.api.registry.impl.PartRegistry;
import teamreborn.reborncore.block.RebornBlockRegistry;
import teamreborn.reborncore.multipart.vanilla.BlockMultipart;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class PartRegistryFactory implements IRegistryFactory
{

	private static HashMap<String, IPart> partHashMap = new HashMap<>();

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return PartRegistry.class;
	}

	@Override
	public void handleClass(Class clazz)
	{
		try
		{
			IPart part = (IPart) clazz.newInstance();
			partHashMap.put(part.getIdenteifyer(), part);
			BlockMultipart blockPart = new BlockMultipart(part);
			blockPart.setRegistryName(Loader.instance().activeModContainer().getModId(), "part." + part.getIdenteifyer().toLowerCase());
			blockPart.setCreativeTab(CreativeTabs.MISC); //TODO debug code
			blockPart.setUnlocalizedName(blockPart.getRegistryName().toString());
			RebornBlockRegistry.registerBlock(blockPart);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

	}

	public static IPart getPartForName(String name)
	{
		return partHashMap.get(name);
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
