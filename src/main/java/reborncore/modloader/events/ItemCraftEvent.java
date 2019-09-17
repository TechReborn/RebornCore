package reborncore.modloader.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ItemCraftEvent {

	EventHandler<ItemCraftEvent> HANDLER = new EventHandler<>();

	void onCraft(ItemStack stack, PlayerEntity playerEntity);

}
