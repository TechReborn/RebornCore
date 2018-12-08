package reborncore.ic2;

import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.common.powerSystem.TilePowerAcceptor;
import reborncore.common.registration.RebornRegistry;

@RebornRegistry(modOnly = "ic2")
public class RebronCoreIC2 implements ExternalPowerManager {
	@Override
	public ExternalPowerHandler createPowerHandler(TilePowerAcceptor acceptor) {
		return new IC2EnergyBase(acceptor);
	}

	@Override
	public boolean isPoweredTile(TileEntity tileEntity) {
		return tileEntity instanceof IEnergyTile;
	}

	@Override
	public boolean isPoweredItem(ItemStack stack) {
		return IC2ItemCharger.isIC2PoweredItem(stack);
	}

	@Override
	public void dischargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack) {
		IC2ItemCharger.dischargeIc2Item(tilePowerAcceptor, stack);
	}

	@Override
	public void chargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack) {
		IC2ItemCharger.chargeIc2Item(tilePowerAcceptor, stack);
	}
}
