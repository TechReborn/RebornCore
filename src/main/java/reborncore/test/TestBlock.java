package reborncore.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;

public class TestBlock extends Block {

    public IBakedModel model;
    public IBakedModel invmodel;

    public TestBlock() {
        super(Material.cake);
        setUnlocalizedName("testBlock");
        setCreativeTab(CreativeTabs.tabBlock);
    }
}
