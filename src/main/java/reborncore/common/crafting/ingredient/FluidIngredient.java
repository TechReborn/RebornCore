package reborncore.common.crafting.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import reborncore.common.fluid.container.ItemFluidInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FluidIngredient extends RebornIngredient {

	private final Fluid fluid;
	private final Optional<List<Item>> holders;

	private final Lazy<List<ItemStack>> previewStacks;
	private final Lazy<Ingredient> previewIngredient;

	private FluidIngredient(Fluid fluid, Optional<List<Item>> holders) {
		this.fluid = fluid;
		this.holders = holders;

		previewStacks = new Lazy<>(() -> Registry.ITEM.stream()
			.filter(item -> item instanceof ItemFluidInfo)
			.filter(item -> !holders.isPresent() || holders.get().stream().anyMatch(i -> i == item))
			.map(item -> ((ItemFluidInfo)item).getFull(fluid))
			.collect(Collectors.toList()));

		previewIngredient = new Lazy<>(() -> Ingredient.ofStacks(previewStacks.get().toArray(new ItemStack[0])));
	}

	public static RebornIngredient deserialize(JsonObject json) {
		Identifier identifier = new Identifier(JsonHelper.getString(json, "fluid"));
		Fluid fluid = Registry.FLUID.get(identifier);
		if(fluid == Fluids.EMPTY){
			throw new JsonParseException("Fluid could not be found: " + JsonHelper.getString(json, "fluid"));
		}

		Optional<List<Item>> holders = Optional.empty();

		if(json.has("holder")){
			if(json.get("holder").isJsonPrimitive()){
				String ident = JsonHelper.getString(json, "holder");
				Item item = Registry.ITEM.get(new Identifier(ident));
				if(item == Items.AIR){
					throw new JsonParseException("could not find item:" + ident);
				}
				holders = Optional.of(Collections.singletonList(item));
			} else {
				JsonArray jsonArray = json.getAsJsonArray("holder");
				List<Item> itemList = new ArrayList<>();
				for (int i = 0; i < jsonArray.size(); i++) {
					String ident = jsonArray.get(i).getAsString();
					Item item = Registry.ITEM.get(new Identifier(ident));
					if(item == Items.AIR){
						throw new JsonParseException("could not find item:" + ident);
					}
					itemList.add(item);
				}
				holders = Optional.of(itemList);
			}
		}

		return new FluidIngredient(fluid, holders);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		if(holders.isPresent() && holders.get().stream().noneMatch(item -> itemStack.getItem() == item)){
			return false;
		}
		if(itemStack.getItem() instanceof ItemFluidInfo){
			return ((ItemFluidInfo) itemStack.getItem()).getFluid(itemStack) == fluid;
		}
		return false;
	}

	@Override
	public Ingredient getPreview() {
		return previewIngredient.get();
	}

	@Override
	public List<ItemStack> getPreviewStacks() {
		return previewStacks.get();
	}
}
