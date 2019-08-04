package reborncore.common.crafting;

import com.google.gson.JsonObject;
import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import reborncore.common.util.Tank;

import javax.annotation.Nonnull;

public abstract class RebornFluidRecipe extends RebornRecipe {

	@Nonnull
	private FluidInstance fluidInstance = FluidInstance.EMPTY;

	public RebornFluidRecipe(RebornRecipeType<?> type, Identifier name) {
		super(type, name);
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		super.deserialize(jsonObject);
		if(jsonObject.has("tank")){
			JsonObject tank = jsonObject.get("tank").getAsJsonObject();

			Identifier identifier = new Identifier(JsonHelper.getString(tank, "fluid"));
			Fluid fluid = Registry.FLUID.get(identifier);

			int amount = 1000;
			if(tank.has("amount")){
				amount = JsonHelper.getInt(tank, "amount");
			}

			fluidInstance = new FluidInstance(fluid, amount);
		}
	}

	public abstract Tank getTank(BlockEntity be);

	@Override
	public boolean canCraft(BlockEntity be) {
		final FluidInstance recipeFluid = fluidInstance;
		final FluidInstance tankFluid = getTank(be).getFluidInstance();
		if (fluidInstance.isEmpty()) {
			return true;
		}
		if (tankFluid.isEmpty()) {
			return false;
		}
		if (tankFluid.getFluid().equals(recipeFluid.getFluid())) {
			if (tankFluid.getAmount() >= recipeFluid.getAmount()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCraft(BlockEntity be) {
		final FluidInstance recipeFluid = fluidInstance;
		final FluidInstance tankFluid = getTank(be).getFluidInstance();
		if (fluidInstance.isEmpty()) {
			return true;
		}
		if (tankFluid.isEmpty()) {
			return false;
		}
		if (tankFluid.getFluid().equals(recipeFluid.getFluid())) {
			if (tankFluid.getAmount() >= recipeFluid.getAmount()) {
				tankFluid.subtractAmount(recipeFluid.getAmount());
				return true;
			}
		}
		return false;
	}

}
