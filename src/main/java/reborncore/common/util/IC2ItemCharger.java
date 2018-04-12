package reborncore.common.util;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import reborncore.common.powerSystem.TilePowerAcceptor;

public class IC2ItemCharger {

	public static void chargeIc2Item(TilePowerAcceptor tilePowerAcceptor, ItemStack stack){
		if(stack.isEmpty()){
			return;
		}
		if(stack.getItem() instanceof IElectricItem){
			tilePowerAcceptor.useEnergy(ElectricItem.manager.charge(stack, tilePowerAcceptor.getEnergy(), 4, false, false));
		}
	}

	public static void dischargeIc2Item(TilePowerAcceptor tilePowerAcceptor, ItemStack stack){
		if(stack.isEmpty()){
			return;
		}
		if(stack.getItem() instanceof IElectricItem){
			tilePowerAcceptor.addEnergy(ElectricItem.manager.discharge(stack, tilePowerAcceptor.getFreeSpace(), 4, false, true,  false));
		}
	}

	public static boolean isIC2PoweredItem(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}
		return stack.getItem() instanceof IElectricItem;
	}

}
