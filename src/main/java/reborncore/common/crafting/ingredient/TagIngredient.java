package reborncore.common.crafting.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.stream.Collectors;

public class TagIngredient extends RebornIngredient {

	private final Identifier tagIdentifier;
	private final Tag<Item> tag;

	public TagIngredient(Identifier tagIdentifier, Tag<Item> tag) {
		this.tagIdentifier = tagIdentifier;
		this.tag = tag;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return itemStack.getItem().isIn(tag);
	}

	@Override
	public Ingredient getPreview() {
		return Ingredient.fromTag(tag);
	}

	@Override
	public List<ItemStack> getPreviewStacks() {
		return tag.values().stream().map(ItemStack::new).collect(Collectors.toList());
	}

	public static RebornIngredient deserialize(JsonObject json) {
		Identifier identifier = new Identifier(JsonHelper.getString(json, "tag"));
		Tag<Item> tag = ItemTags.getContainer().get(identifier);
		if (tag == null) {
			throw new JsonSyntaxException("Unknown item tag '" + identifier + "'");
		} else {
			return new TagIngredient(identifier, tag);
		}
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("tag", tagIdentifier.toString());
		return jsonObject;
	}
}
