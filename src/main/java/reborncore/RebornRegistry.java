package reborncore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.common.util.CraftingHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class RebornRegistry
{
    public static void registerBlock(Block block, String name)
    {
        block.setRegistryName(name);
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block), block.getRegistryName());
    }

    public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass, String name)
    {
        block.setRegistryName(name);
        GameRegistry.register(block);
        try
        {
            ItemBlock itemBlock = itemclass.getConstructor(Block.class).newInstance(block);
            itemBlock.setRegistryName(name);
            GameRegistry.register(itemBlock);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    public static void registerItem(Item item, ResourceLocation name)
    {
        GameRegistry.register(item, name);
    }

    public static void addShapedOreRecipe(ItemStack output, Object parm)
    {
        CraftingHelper.addShapedOreRecipe(output, parm);
    }

    public static void addShapelessOreRecipe(ItemStack output, Object parm)
    {
        CraftingHelper.addShapelessOreRecipe(output, parm);
    }
}
