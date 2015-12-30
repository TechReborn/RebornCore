package reborncore.api;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class TextureRegistry {

    public static ArrayList<Block> blocks = new ArrayList<Block>();

    public static void registerBlock(Block block) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            return;
        }
        if (!(block instanceof IBlockTextureProvider)) {
            return;
        }
        if (blocks.contains(block)) {
            return;
        }
        blocks.add(block);
    }

    public static ArrayList<Item> items = new ArrayList<Item>();

    public static void registerItem(Item item) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            return;
        }
        if (!(item instanceof IItemTexture)) {
            return;
        }
        if (items.contains(item)) {
            return;
        }
        items.add(item);
    }

    public static ArrayList<BlockFluidClassic> fluids = new ArrayList<BlockFluidClassic>();

    public static void registerFluid(BlockFluidClassic fluid) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            return;
        }
        if (fluids.contains(fluid)) {
            return;
        }
        fluids.add(fluid);
    }


    @SideOnly(Side.CLIENT)
    public static ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation((ResourceLocation) Block.blockRegistry.getNameForObject(state.getBlock()), (new DefaultStateMapper()).getPropertyString(state.getProperties()));
    }

    @SideOnly(Side.CLIENT)
    public static ModelResourceLocation getBlockinventoryResourceLocation(Block block) {
        return new ModelResourceLocation(Block.blockRegistry.getNameForObject(block), "inventory");
    }

    @SideOnly(Side.CLIENT)
    public static ModelResourceLocation getItemInventoryResourceLocation(Item block) {
        return new ModelResourceLocation(Item.itemRegistry.getNameForObject(block), "inventory");
    }

    @SideOnly(Side.CLIENT)
    public static ResourceLocation getBlockResourceLocation(Block block) {
        return Block.blockRegistry.getNameForObject(block);
    }

}
