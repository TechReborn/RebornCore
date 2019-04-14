package reborncore.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import reborncore.common.util.NonNullListCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeUtils {

	public static <R extends Recipe> List<R> getRecipes(World world, RecipeType<R> type){
		List<R> recipes = new ArrayList<>();
		for(IRecipe recipe : world.getRecipeManager().getRecipes()){
			if(recipe instanceof Recipe && ((Recipe) recipe).getRecipeType().equals(type)){
				if(type.getRecipeClass() != recipe.getClass()){
					throw new RuntimeException("Invalid recipe in " + type.getName());
				}
				//noinspection unchecked
				recipes.add((R) recipe);
			}
		}
		return Collections.unmodifiableList(recipes);
	}

	public static NonNullList<ItemStack> deserializeItems(JsonObject jsonObject){
		if(jsonObject.isJsonArray()){
			return jsonObject.entrySet().stream().map(entry -> deserializeItem(entry.getValue().getAsJsonObject())).collect(NonNullListCollector.toList());
		} else {
			return NonNullList.from(deserializeItem(jsonObject));
		}
	}

	private static ItemStack deserializeItem(JsonObject jsonObject){
		ResourceLocation resourceLocation = new ResourceLocation(JsonUtils.getString(jsonObject, "item"));
		Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
		if(item == null){
			throw new IllegalStateException(resourceLocation + " did not exist");
		}
		int count = 1;
		if(jsonObject.has("count")){
			count = JsonUtils.getInt(jsonObject, "count");
		}
		//TODO support nbt
		return new ItemStack(item, count);
	}

}
