package reborncore.common.explosion;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.commons.lang3.time.StopWatch;
import reborncore.RebornCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by modmuss50 on 12/03/2016.
 */
public class RebornExplosion extends Explosion {

    @Nonnull
    BlockPos center;

    @Nonnull
    World world;

    @Nonnull
    int radius;

    @Nullable
    EntityLivingBase livingBase;

    public RebornExplosion(@Nonnull BlockPos center, @Nonnull World world, @Nonnull int radius) {
        super(world, null, center.getX(), center.getY(), center.getZ(), radius, false, true);
        this.center = center;
        this.world = world;
        this.radius = radius;
    }

    public void setLivingBase(@Nullable EntityLivingBase livingBase) {
        this.livingBase = livingBase;
    }

    public
    @Nullable
    EntityLivingBase getLivingBase() {
        return livingBase;
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
                        Block block = state.getBlock();
                        if (block != Blocks.bedrock && block != Blocks.air) {
                            block.onBlockDestroyedByExplosion(world, pos, this);
                            world.setBlockState(pos, Blocks.air.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
        RebornCore.logHelper.info("The explosion took" + watch + " to explode");
    }

    @Override
    public void doExplosionA() {
        explode();
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        explode();
    }

    @Override
    public
    @Nullable
    EntityLivingBase getExplosivePlacedBy() {
        return livingBase;
    }

    @Override
    public List<BlockPos> getAffectedBlockPositions() {
        List<BlockPos> poses = new ArrayList<>();
        for (int tx = -radius; tx < radius + 1; tx++) {
            for (int ty = -radius; ty < radius + 1; ty++) {
                for (int tz = -radius; tz < radius + 1; tz++) {
                    if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
                        BlockPos pos = center.add(tx, ty, tz);
                        IBlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (block != Blocks.bedrock && block != Blocks.air) {
                            poses.add(pos);
                        }
                    }
                }
            }
        }
        return poses;
    }
}
