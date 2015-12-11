package reborncore.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class BucketHandler {

    public static BucketHandler INSTANCE = new BucketHandler();
    public Map<Block, Item> buckets = new HashMap<Block, Item>();

    private BucketHandler() {

    }

    //TODO 1.8
//    @SubscribeEvent
//    public void onBucketFill(FillBucketEvent event) {
//        ItemStack result = fillCustomBucket(event.world, event.target);
//
//        if (result == null)
//            return;
//
//        event.result = result;
//        event.setResult(Result.ALLOW);
//    }
//
//    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
//
//        Block block = world.getBlockState(pos.getBlockPos()).getBlock();
//
//        Item bucket = buckets.get(block);
//        if (bucket != null
//                && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) {
//            world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
//            return new ItemStack(bucket);
//        } else
//            return null;
//
//    }

}
