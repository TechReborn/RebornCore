/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package reborncore.client.multiblock.component;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.multiblock.CoordTriplet;

/**
 * A component of a multiblock, the normal one
 * is just a block.
 */
public class MultiblockComponent {

    public CoordTriplet relPos;
    public final Block block;
    public final int meta;

    public MultiblockComponent(CoordTriplet relPos, Block block, int meta) {
        this.relPos = relPos;
        this.block = block;
        this.meta = meta;
    }

    public CoordTriplet getRelativePosition() {
        return relPos;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public boolean matches(World world, int x, int y, int z) {
        return world.getBlockState(new BlockPos(x, y, z)).getBlock() == getBlock();
    }

    public ItemStack[] getMaterials() {
        return new ItemStack[]{new ItemStack(block, 1, meta)};
    }

    public void rotate(double angle) {
        double x = relPos.x;
        double z = relPos.z;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double xn = x * cos - z * sin;
        double zn = x * sin + z * cos;
        relPos = new CoordTriplet((int) Math.round(xn), relPos.y, (int) Math.round(zn));
    }

    public MultiblockComponent copy() {
        return new MultiblockComponent(relPos, block, meta);
    }
}
