package reborncore.common.fluid.container;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import reborncore.common.fluid.FluidValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*

	* Based of Slilk's API but with some breaking changes

	* Direction has been replaced with a generic type, this allows for things like ItemStack to be easily passed along
	* Some methods such as getCapacity have been tweaked to also provide the type
	* Some methods have got default implementations making it a lot easier to implement without the worry for bugs
	* The multiple fluids thing has gone, it is still possible to one fluid per side if wanted as the type is passed around everywhere
	* A lot of the "helper" methods have been removed, these should really go in boilerplate classes and not the in raw api
	* removed the docs as cba to write them

 */
public interface GenericFluidContainer<T> {

	@Nullable
	static GenericFluidContainer<ItemStack> fromStack(@Nonnull ItemStack itemStack){
		if(itemStack.getItem() instanceof GenericFluidContainer){
			//noinspection unchecked
			return (GenericFluidContainer<ItemStack>) itemStack.getItem();
		}
		return null;
	}

	void setFluid(T type, @Nonnull FluidInstance instance);

	@Nonnull
	FluidInstance getFluidInstance(T type);

	FluidValue getCapacity(T type);

	default boolean canHold(T type, Fluid fluid){
		return true;
	}

	default FluidValue getCurrentFluidAmount(T type) {
		return getFluidInstance(type).getAmount();
	}

	default boolean canInsertFluid(T type, @Nonnull Fluid fluid, FluidValue amount){
		if(!canHold(type, fluid)){
			return false;
		}
		FluidInstance currentFluid = getFluidInstance(type);
		return currentFluid.isEmpty() || currentFluid.getFluid() == fluid && currentFluid.getAmount().add(amount).lessThan(getCapacity(type));
	}

	default boolean canExtractFluid(T type, @Nonnull Fluid fluid, FluidValue amount){
		return getFluidInstance(type).getFluid() == fluid && amount.lessThanOrEqual(getFluidInstance(type).getAmount());
	}

	default void insertFluid(T type, @Nonnull Fluid fluid, FluidValue amount){
		if(canInsertFluid(type, fluid, amount)){
			setFluid(type, getFluidInstance(type).addAmount(amount));
		}
	}

	default void extractFluid(T type, @Nonnull Fluid fluid, FluidValue amount){
		if(canExtractFluid(type, fluid, amount)){
			setFluid(type, getFluidInstance(type).subtractAmount(amount));
		}
	}

}
