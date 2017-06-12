package reborncore.api.tile;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.tile.TileLegacyMachineBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Added to an item to say that it is a valid upgrade
 */
public interface IUpgrade {

	public void process(
		@Nonnull
			TileLegacyMachineBase machineBase,
		@Nullable
			RecipeCrafter crafter,
		@Nonnull
			ItemStack stack);

	@SideOnly(Side.CLIENT)
	public void handleRightClick(TileEntity tile, ItemStack stack, Container container, int slotID);

}
