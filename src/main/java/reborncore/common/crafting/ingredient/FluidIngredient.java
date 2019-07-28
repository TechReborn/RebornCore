package reborncore.common.crafting.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import reborncore.common.fluid.container.ItemFluidInfo;

import java.util.List;
import java.util.stream.Collectors;

public class FluidIngredient extends RebornIngredient {

	private final Fluid fluid;

	private final Lazy<List<ItemStack>> previewStacks;
	private final Lazy<Ingredient> previewIngredient;

	private FluidIngredient(Fluid fluid) {
		this.fluid = fluid;

		previewStacks = new Lazy<>(() -> Registry.ITEM.stream()
			.filter(item -> item instanceof ItemFluidInfo)
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
		return new FluidIngredient(fluid);
	}

	@Override
	public boolean test(ItemStack itemStack) {
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
