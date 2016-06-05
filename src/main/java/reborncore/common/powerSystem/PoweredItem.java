package reborncore.common.powerSystem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.powerSystem.traits.BasePowerTrait;
import reborncore.common.powerSystem.traits.EUItemPowerTrait;
import reborncore.common.powerSystem.traits.RFItemPowerTrait;
import reborncore.jtraits.MixinFactory;

public abstract class PoweredItem
{

	public static Item createItem(Class itemClass) throws IllegalAccessException, InstantiationException
	{
		if(Loader.isModLoaded("IC2")){
			return (Item) MixinFactory
					.mixin(itemClass, BasePowerTrait.class, RFItemPowerTrait.class, EUItemPowerTrait.class).newInstance();
		}
		return (Item) MixinFactory
				.mixin(itemClass, BasePowerTrait.class, RFItemPowerTrait.class).newInstance();
	}

	public static boolean canUseEnergy(double energy, ItemStack stack)
	{
		if (stack.getItem() instanceof IEnergyInterfaceItem)
		{
			return ((IEnergyInterfaceItem) stack.getItem()).canUseEnergy(energy, stack);
		} else
		{
			return false;
		}
	}

	public static double useEnergy(double energy, ItemStack stack)
	{
		if (stack.getItem() instanceof IEnergyInterfaceItem)
		{
			return ((IEnergyInterfaceItem) stack.getItem()).useEnergy(energy, stack);
		} else
		{
			return 0;
		}
	}

	public static void setEnergy(double energy, ItemStack stack)
	{
		if (stack.getItem() instanceof IEnergyInterfaceItem)
		{
			((IEnergyInterfaceItem) stack.getItem()).setEnergy(energy, stack);
		}
	}

	public static double getEnergy(ItemStack stack)
	{
		if (stack.getItem() instanceof IEnergyInterfaceItem)
		{
			return ((IEnergyInterfaceItem) stack.getItem()).getEnergy(stack);
		} else
		{
			return 0;
		}
	}

	public static double addEnergy(double energy, ItemStack stack)
	{
		if (stack.getItem() instanceof IEnergyInterfaceItem)
		{
			return ((IEnergyInterfaceItem) stack.getItem()).addEnergy(energy, stack);
		}
		return 0;
	}

	public static double getMaxPower(ItemStack stack)
	{
		if (stack.getItem() instanceof IEnergyInterfaceItem)
		{
			return ((IEnergyInterfaceItem) stack.getItem()).getMaxPower(stack);
		}
		return 0;
	}

}
