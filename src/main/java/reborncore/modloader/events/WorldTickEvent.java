package reborncore.modloader.events;

import net.minecraft.world.World;

public interface WorldTickEvent {

	EventHandler<WorldTickEvent> HANDLER = new EventHandler<>();

	void tick(World world);

}
