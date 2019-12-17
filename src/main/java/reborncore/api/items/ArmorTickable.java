package reborncore.api.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ArmorTickable {

	void tickArmor(ItemStack stack, PlayerEntity playerEntity);
}
