package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.power.ItemPowerManager;
import reborncore.common.registration.RebornRegister;

@RebornRegister("reborncore")
public class PowerManager implements ExternalPowerManager {

	@Override
	public ExternalPowerHandler createPowerHandler(TilePowerAcceptor acceptor) {
		return new PowerHandler(acceptor);
	}

	@Override
	public boolean isPoweredItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isPoweredTile(BlockEntity tileEntity, Direction side) {
		return tileEntity instanceof IEnergyInterfaceTile;
	}

	@Override
	public void dischargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack) {

	}

	@Override
	public void chargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack) {

	}

	@Override
	public void chargeItem(ItemPowerManager powerAcceptor, ItemStack stack) {

	}
}
