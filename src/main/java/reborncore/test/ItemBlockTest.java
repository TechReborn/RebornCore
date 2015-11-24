package reborncore.test;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;

import java.util.List;

public class ItemBlockTest extends ItemMultiTexture {
    public ItemBlockTest(Block block) {
        super(block, block, TestBlock.types);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int par1) {
        return par1;
    }

}
