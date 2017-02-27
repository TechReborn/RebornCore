package teamreborn.testmod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import teamreborn.reborncore.api.registry.RebornRegistry;
import teamreborn.reborncore.api.registry.impl.ItemRegistry;

@RebornRegistry (modID = "testmod")
public class TestItem extends Item
{

	@ItemRegistry
	public static TestItem testItem;

	public TestItem()
	{
		setRegistryName(new ResourceLocation("testmod", "testItem"));
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabs.MISC);
	}
}
