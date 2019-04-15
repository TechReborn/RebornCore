package reborncore.common.crafting;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.function.Predicate;

//Mainly a stack size aware wrapper for Ingredient
public class RebornIngredient implements Predicate<ItemStack> {

	public static final int NO_SIZE = -1;

	private final Ingredient base;

	private final int size;

	private RebornIngredient(Ingredient base, int size) {
		this.base = base;
		this.size = size;
	}

	private RebornIngredient(Ingredient base) {
		this(base, NO_SIZE);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return base.test(itemStack) && (size == NO_SIZE || itemStack.getCount() >= size);
	}

	public Ingredient getBase() {
		return base;
	}

	public int getSize() {
		if(size == NO_SIZE){
			return 1;
		}
		return size;
	}

	public static RebornIngredient deserialize(@Nullable JsonElement json) {
		Validate.notNull(json, "item cannot be null");
		Ingredient base = Ingredient.deserialize(json);
		int size = NO_SIZE;
		if(json.isJsonObject()){
			if(json.getAsJsonObject().has("size")){
				size = JsonUtils.getInt(json.getAsJsonObject(), "size");
			}
		} else if (json.isJsonArray()){
			//TODO not really supported? might be best to ensure all sizes are the same? or find a nice way to allow mutli sizes, could be possible if required
		}
		return new RebornIngredient(base, size);
	}

}
