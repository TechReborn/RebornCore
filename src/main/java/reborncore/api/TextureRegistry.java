package reborncore.api;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;

import java.util.ArrayList;

public class TextureRegistry {

    public static ArrayList<Block> blocks = new ArrayList<Block>();

    public static void registerBlock(Block block){
        if(!(block instanceof IBlockTextureProvider)){
            return;
        }
        if(blocks.contains(block)){
            return;
        }
        blocks.add(block);
    }

    public static ArrayList<Item> items = new ArrayList<Item>();

    public static void registerItem(Item item){
        if(!(item instanceof IItemTexture)){
            return;
        }
        if(items.contains(item)){
            return;
        }
        items.add(item);
    }

    public static ArrayList<BlockFluidClassic> fluids = new ArrayList<BlockFluidClassic>();

    public static void registerFluid(BlockFluidClassic fluid){
        if(fluids.contains(fluid)){
            return;
        }
        fluids.add(fluid);
    }


    public static ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation((ResourceLocation) Block.blockRegistry.getNameForObject(state.getBlock()), (new DefaultStateMapper()).getPropertyString(state.getProperties()));
    }

}
