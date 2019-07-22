package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.api.power.EnergyBlockEntity;
import reborncore.api.power.ItemPowerManager;
import reborncore.common.registration.RebornRegister;

@RebornRegister("reborncore")
public class PowerManager implements ExternalPowerManager {

	@Override
	public ExternalPowerHandler createPowerHandler(PowerAcceptorBlockEntity acceptor) {
		return new PowerHandler(acceptor);
	}

	@Override
	public boolean isPoweredItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isPowered(BlockEntity blockEntity, Direction side) {
		return blockEntity instanceof EnergyBlockEntity;
	}

	@Override
	public void dischargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {

	}

	@Override
	public void chargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {

	}

	@Override
	public void chargeItem(ItemPowerManager powerAcceptor, ItemStack stack) {

	}
}
