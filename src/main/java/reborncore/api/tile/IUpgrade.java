package reborncore.api.tile;

import net.minecraft.item.ItemStack;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.tile.TileLegacyMachineBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Added to an item to say that it is a valid upgrade
 */
public interface IUpgrade {

	public void process(@Nonnull TileLegacyMachineBase machineBase, @Nullable
		RecipeCrafter crafter, @Nonnull ItemStack stack);
}
