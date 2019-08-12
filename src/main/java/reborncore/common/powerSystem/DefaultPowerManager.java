package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.api.power.EnergyBlockEntity;
import reborncore.api.power.ItemPowerManager;

public class DefaultPowerManager implements ExternalPowerManager {

	@Override
	public ExternalPowerHandler createPowerHandler(PowerAcceptorBlockEntity acceptor) {
		return new PowerHandler(acceptor);
	}

	@Override
	public boolean isPoweredItem(ItemStack stack) {
		return stack.getItem() instanceof IEnergyItemInfo;
	}

	@Override
	public boolean isPowered(BlockEntity blockEntity, Direction side) {
		return blockEntity instanceof EnergyBlockEntity;
	}

	@Override
	public void dischargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {
		if (! (stack.getItem() instanceof IEnergyItemInfo)) {
			return;
		}
		double chargeEnergy = Math.min(blockEntityPowerAcceptor.getFreeSpace(), blockEntityPowerAcceptor.getMaxInput());
		if (chargeEnergy <= 0.0) {
			return;
		}
		ItemPowerManager poweredItem = new ItemPowerManager(stack);
		if (poweredItem.getEnergyStored() > 0) {
			int extracted = poweredItem.extractEnergy((int) chargeEnergy, false);
			blockEntityPowerAcceptor.addEnergy(extracted);
		}
	}

	@Override
	public void chargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {
		if (! (stack.getItem() instanceof IEnergyItemInfo)) {
			return;
		}
		
		int chargeEnergy = (int) Math.min(blockEntityPowerAcceptor.getEnergy(), blockEntityPowerAcceptor.getMaxOutput());
		ItemPowerManager poweredItem = new ItemPowerManager(stack);
		int energyReceived = poweredItem.receiveEnergy(chargeEnergy, false);
		if (energyReceived > 0) {
			blockEntityPowerAcceptor.useEnergy((double) energyReceived);
		}
	}

	@Override
	public void chargeItem(ItemPowerManager powerAcceptor, ItemStack stack) {

	}
}
