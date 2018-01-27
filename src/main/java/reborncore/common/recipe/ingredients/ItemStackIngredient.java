package reborncore.common.recipe.ingredients;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import reborncore.common.util.ItemUtils;

public class ItemStackIngredient extends BaseIngredient<ItemStack>{

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
	public ItemStack get() {
		return stack.copy();
	}

	@Override
	public boolean matches(Object obj) {
		//TODO shall we check the object or not? We might only want to check item stacks
		return ItemUtils.isInputEqual(obj, stack, matchDamage, matchNBT, useOreDict);
	}

	@Override
	public Class getHeldClass() {
		return stack.getClass();
	}
}
