package reborncore.common.recipe.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import reborncore.api.newRecipe.IMachine;
import reborncore.common.recipe.registry.IngredientRegistry;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.util.ItemUtils;

@RebornRegistry
@IngredientRegistry
public class ItemStackIngredient extends BaseIngredient{

	ItemStack stack;
	boolean matchDamage, matchNBT, useOreDict = true;

	public ItemStackIngredient() {
	}

	public ItemStackIngredient(ItemStack stack) {
		this.stack = stack;
	}

	public ItemStackIngredient(ItemStack stack, boolean useOreDict) {
		this.stack = stack;
		this.useOreDict = useOreDict;
	}

	public ItemStackIngredient(ItemStack stack, boolean matchNBT, boolean useOreDict) {
		this.stack = stack;
		this.matchNBT = matchNBT;
		this.useOreDict = useOreDict;
	}

	public ItemStackIngredient(ItemStack stack, boolean matchDamage, boolean useOreDict, boolean matchNBT) {
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

	@Override
	public ResourceLocation getType() {
		return new ResourceLocation("reborncore:itemstack");
	}

	public static ItemStackIngredient fromJson(JsonObject jsonObject){
		//Adds the data value, if its not set, as there is not reason that it should be required
		if(!jsonObject.has("data")){
			jsonObject.addProperty("data", 0);
		}
		ItemStack stack = CraftingHelper.getItemStack(jsonObject, new JsonContext("reborncore"));
		boolean matchDamage = true;
		if(jsonObject.has("matchDamage")){
			matchDamage = jsonObject.getAsJsonPrimitive("matchDamage").getAsBoolean();
		}
		boolean matchNBT = true;
		if(jsonObject.has("matchNBT")){
			matchNBT = jsonObject.getAsJsonPrimitive("matchNBT").getAsBoolean();
		}
		boolean useOreDict = true;
		if(jsonObject.has("oreDict")){
			useOreDict = jsonObject.getAsJsonPrimitive("oreDict").getAsBoolean();
		}
		return new ItemStackIngredient(stack, matchDamage, useOreDict, matchNBT);
	}
}
