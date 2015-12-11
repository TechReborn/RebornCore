package reborncore.test;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;

public class ItemBlockTest extends ItemMultiTexture {
    public ItemBlockTest(Block block) {
        super(block, block, TestBlock.types);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int par1) {
        return par1;
    }

}
