package reborncore.common.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StackIngredient extends RebornIngredient {

	private final List<ItemStack> stacks;

	private final Optional<Integer> count;

	private StackIngredient(List<ItemStack> stacks, Optional<Integer> count) {
		this.stacks = stacks;
		this.count = count;
	}

	public static RebornIngredient deserialize(JsonObject json) {
		Identifier identifier = new Identifier(JsonHelper.getString(json, "item"));
		Item item = Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + identifier + "'"));

		Optional<Integer> stackSize = Optional.empty();
		if(json.has("size")){
			stackSize = Optional.of(JsonHelper.getInt(json, "size"));
		}

		return new StackIngredient(Collections.singletonList(new ItemStack(item)), stackSize);
	}


	@Override
	public boolean test(ItemStack itemStack) {
		if(itemStack.isEmpty()){
			return false;
		}
		if(stacks.stream().noneMatch(recipeStack -> recipeStack.getItem() == itemStack.getItem())){
			return false;
		}
		if(count.isPresent() && count.get() > itemStack.getCount()){
			return false;
		}
		return true;
	}

	@Override
	public Ingredient getPreview() {
		return Ingredient.ofStacks(stacks.toArray(new ItemStack[0]));
	}

	@Override
	public List<ItemStack> getPreviewStacks() {
		return Collections.unmodifiableList(count.map(stackSize -> stacks.stream().map(ItemStack::copy).peek(itemStack -> itemStack.setCount(stackSize)).collect(Collectors.toList())).orElse(stacks));
	}

	public int getCount(){
		return count.orElse(1);
	}
}
