package reborncore.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;


public interface ItemCraftCallback {

	Event<ItemCraftCallback> EVENT = EventFactory.createArrayBacked(ItemCraftCallback.class, (listeners) -> (stack, playerEntity) -> {
		for(ItemCraftCallback callback : listeners){
			callback.onCraft(stack, playerEntity);
		}
	});

	void onCraft(ItemStack stack, PlayerEntity playerEntity);

}
