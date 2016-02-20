package reborncore.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class BucketHandler {

    public static BucketHandler INSTANCE = new BucketHandler();
    public Map<IBlockState, Item> buckets = new HashMap<IBlockState, Item>();

    private BucketHandler() {

    }


    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        ItemStack result = fillCustomBucket(event.world, event.target);

        if (result == null)
            return;

        event.result = result;
        event.setResult(Event.Result.ALLOW);
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
        IBlockState state = world.getBlockState(pos.getBlockPos());

        Item bucket = buckets.get(state);

        if (bucket != null) {
            world.setBlockToAir(pos.getBlockPos());
            return new ItemStack(bucket);
        } else {
            return null;
        }
    }

}
