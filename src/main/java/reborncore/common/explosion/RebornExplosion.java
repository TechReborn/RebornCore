package reborncore.common.explosion;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
//import techreborn.Core;

/**
 * Created by modmuss50 on 12/03/2016.
 */
public class RebornExplosion {

    BlockPos center;

    World world;

    int radius;

    public RebornExplosion(BlockPos center, World world, int radius) {
        this.center = center;
        this.world = world;
        this.radius = radius;
    }


    public void explode() {
        StopWatch watch = new StopWatch();
        watch.start();
        for (int tx = -radius; tx < radius + 1; tx++) {
            for (int ty = -radius; ty < radius + 1; ty++) {
                for (int tz = -radius; tz < radius + 1; tz++) {
                    if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
                        BlockPos pos = center.add(tx, ty, tz);
                        IBlockState state = world.getBlockState(pos);
                        if(state.getBlock()!= Blocks.bedrock && state.getBlock()!= Blocks.air){
                            world.setBlockState(pos, Blocks.air.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
        //Core.logHelper.info("The explosion took" + watch + " to explode");
    }
}
