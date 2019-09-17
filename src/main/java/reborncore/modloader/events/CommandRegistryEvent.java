package reborncore.modloader.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;


public interface CommandRegistryEvent {

	EventHandler<CommandRegistryEvent> HANDLER = new EventHandler<>();

	void register(CommandDispatcher<ServerCommandSource> dispatcher);

}
