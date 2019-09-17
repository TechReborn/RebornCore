package reborncore.modloader.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface UseBlockEvent {

	EventHandler<UseBlockEvent> HANDLER = new EventHandler<>();

	ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult);

}
