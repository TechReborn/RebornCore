package reborncore.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.util.ActionResult;
import reborncore.RebornCore;
import reborncore.fabric.events.ItemCraftCallback;
import reborncore.modloader.events.*;

public class RebornCoreFabric implements ModInitializer {

	public RebornCoreFabric() {
		RebornCore.hooks = new FabricHooks();
	}

	@Override
	public void onInitialize() {
		RebornCore.INSTANCE.onInitialize();

		//Proxy all the events over
		WorldTickCallback.EVENT.register(world -> WorldTickEvent.HANDLER.post(event -> event.tick(world)));
		CommandRegistry.INSTANCE.register(false, dispatcher -> CommandRegistryEvent.HANDLER.post(commandRegistryEvent -> commandRegistryEvent.register(dispatcher)));
		ItemCraftCallback.EVENT.register((stack, playerEntity) -> ItemCraftEvent.HANDLER.post(itemCraftEvent -> itemCraftEvent.onCraft(stack, playerEntity)));
		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			for(AttackBlockEvent handler : AttackBlockEvent.HANDLER.getHandlers()){
				ActionResult result = handler.interact(playerEntity, world, hand, blockPos, direction);
				if(result != ActionResult.PASS){
					return result;
				}
			}
			return ActionResult.PASS;
		});
		UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
			for(UseBlockEvent handler : UseBlockEvent.HANDLER.getHandlers()){
				ActionResult result = handler.interact(playerEntity, world, hand, blockHitResult);
				if(result != ActionResult.PASS){
					return result;
				}
			}
			return ActionResult.PASS;
		});
	}
}
