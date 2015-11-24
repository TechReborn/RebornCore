package reborncore.api;


import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;

public class TextureRegistry {

    public static ArrayList<Block> blocks = new ArrayList<Block>();

    public static void registerBlock(Block block){
        blocks.add(block);
    }

    public static ArrayList<Item> items = new ArrayList<Item>();

    public static void registerItem(Item item){
        if(!(item instanceof IItemTexture)){
            return;
        }
        items.add(item);
    }


}
