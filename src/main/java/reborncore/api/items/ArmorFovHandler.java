package reborncore.api.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ArmorFovHandler {

	float changeFov(float old, ItemStack stack, PlayerEntity playerEntity);
}
