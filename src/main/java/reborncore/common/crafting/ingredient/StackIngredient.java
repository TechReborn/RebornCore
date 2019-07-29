package reborncore.common.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
	private final Optional<CompoundTag> tag;

	private StackIngredient(List<ItemStack> stacks, Optional<Integer> count, Optional<CompoundTag> tag) {
		this.stacks = stacks;
		this.count = count;
		this.tag = tag;
	}

	public static RebornIngredient deserialize(JsonObject json) {
		Identifier identifier = new Identifier(JsonHelper.getString(json, "item"));
		Item item = Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + identifier + "'"));

		Optional<Integer> stackSize = Optional.empty();
		if(json.has("count")){
			stackSize = Optional.of(JsonHelper.getInt(json, "count"));
		}

		Optional<CompoundTag> tag = Optional.empty();
		if(json.has("tag")){
			tag = Optional.of((CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, json.get("tag")));
		}

		return new StackIngredient(Collections.singletonList(new ItemStack(item)), stackSize, tag);
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
		if(tag.isPresent()){
			if(!itemStack.hasTag()){
				return false;
			}

			//Bit of a meme here, as DataFixer likes to use the most basic primative type over using an int.
			//So we have to go to json and back on the incoming stack to be sure its using types that match our input.

			CompoundTag compoundTag = itemStack.getTag();
			JsonElement jsonElement = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, compoundTag);
			compoundTag = (CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, jsonElement);

			if(!tag.get().equals(compoundTag)){
				return false;
			}
		}
		return true;
	}

	@Override
	public Ingredient getPreview() {
		return Ingredient.ofStacks(stacks.toArray(new ItemStack[0]));
	}

	@Override
	public List<ItemStack> getPreviewStacks() {
		return Collections.unmodifiableList(
			stacks.stream()
				.map(ItemStack::copy)
				.peek(itemStack -> itemStack.setCount(count.orElse(1)))
				.peek(itemStack -> itemStack.setTag(tag.orElse(null)))
				.collect(Collectors.toList()));
	}

	public int getCount(){
		return count.orElse(1);
	}
}
