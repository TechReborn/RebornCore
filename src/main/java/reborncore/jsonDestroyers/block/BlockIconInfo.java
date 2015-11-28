package reborncore.jsonDestroyers.block;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

public class BlockIconInfo {
    public Block block;
    public int meta;
    public EnumFacing side;

    public BlockIconInfo(Block block, int meta, EnumFacing side) {
        this.block = block;
        this.meta = meta;
        this.side = side;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public EnumFacing getSide() {
        return side;
    }

    public void setSide(EnumFacing side) {
        this.side = side;
    }
}
