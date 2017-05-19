package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.mixin.MixinManager;
import reborncore.mixin.json.MixinTargetData;

@Deprecated
public abstract class PoweredItem {

	public static void registerPoweredItem(String itemclass, boolean ic2) {
		MixinManager.registerMixin(new MixinTargetData("reborncore.common.powerSystem.mixin.BasePowerMixin", itemclass));
		MixinManager.registerMixin(new MixinTargetData("reborncore.common.powerSystem.mixin.CapabilityItemPowerMixin", itemclass));
		if (ic2 && Loader.isModLoaded("ic2")) {
			MixinManager.registerMixin(new MixinTargetData("reborncore.common.powerSystem.mixin.EUItemPowerTrait", itemclass));
		}
	}

	public static void registerPoweredItem(String itemclass) {
		registerPoweredItem(itemclass, true);
	}

	public static boolean canUseEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).canUseEnergy(energy, stack);
		} else {
			return false;
		}
	}

	public static double useEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).useEnergy(energy, stack);
		} else {
			return 0;
		}
	}

	public static void setEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			((IEnergyInterfaceItem) stack.getItem()).setEnergy(energy, stack);
		}
	}

	public static double getEnergy(ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).getEnergy(stack);
		} else {
			return 0;
		}
	}

	public static double addEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).addEnergy(energy, stack);
		}
		return 0;
	}

	public static double getMaxPower(ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).getMaxPower(stack);
		}
		return 0;
	}

}
