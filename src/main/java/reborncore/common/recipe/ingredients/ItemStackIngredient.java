package reborncore.common.recipe.ingredients;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IMachine;
import reborncore.common.recipe.registry.IngredientRegistry;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.util.ItemUtils;

@RebornRegistry
@IngredientRegistry
public class ItemStackIngredient extends BaseIngredient{

	ItemStack stack;
	boolean matchDamage, matchNBT, useOreDict = true;

	public ItemStackIngredient(ResourceLocation type, ItemStack stack) {
		super(type);
		this.stack = stack;
	}

	public ItemStackIngredient(ResourceLocation type, ItemStack stack, boolean useOreDict) {
		super(type);
		this.stack = stack;
		this.useOreDict = useOreDict;
	}

	public ItemStackIngredient(ResourceLocation type, ItemStack stack, boolean matchNBT, boolean useOreDict) {
		super(type);
		this.stack = stack;
		this.matchNBT = matchNBT;
		this.useOreDict = useOreDict;
	}

	public ItemStackIngredient(ResourceLocation type, ItemStack stack, boolean matchDamage, boolean useOreDict, boolean matchNBT) {
		super(type);
		this.stack = stack;
		this.matchDamage = matchDamage;
		this.matchNBT = matchNBT;
		this.useOreDict = useOreDict;
	}

	@Override
	public boolean canCraft(IMachine machine) {
		//TODO make this work
		return ItemUtils.isInputEqual(machine, stack, matchDamage, matchNBT, useOreDict);
	}
}
