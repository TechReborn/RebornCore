package teamreborn.testmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import teamreborn.reborncore.api.registry.BlockRegistry;
import teamreborn.reborncore.api.registry.RebornRegistry;

@RebornRegistry(modID = "testmod")
public class TestBlock extends Block {

	@BlockRegistry
	public static TestBlock instance;

//	@RebornBlockRegistry(name = "test2")
//	public static TestBlock instance2;

	public TestBlock(String name) {
		super(Material.GROUND);
		setRegistryName(new ResourceLocation("testmod", name));
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabs.MISC);
	}

	public TestBlock() {
		this("test1");
	}
}
