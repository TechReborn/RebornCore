package reborncore.modloader.networking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ThreadExecutor;
import reborncore.modloader.Side;

public interface PacketContext {

	Side getSide();

	PlayerEntity getPlayer();

	ThreadExecutor getTaskQueue();
}
