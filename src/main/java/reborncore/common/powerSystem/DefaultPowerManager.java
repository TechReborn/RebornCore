package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.api.power.ItemPowerManager;
import team.reborn.energy.Energy;

public class DefaultPowerManager implements ExternalPowerManager {

	@Override
	public ExternalPowerHandler createPowerHandler(PowerAcceptorBlockEntity acceptor) {
		return new PowerHandler(acceptor);
	}

	@Override
	public boolean isPoweredItem(ItemStack stack) {
		return Energy.valid(stack);
	}

	@Override
	public boolean isPowered(BlockEntity blockEntity, Direction side) {
		return Energy.valid(blockEntity);
	}

	@Override
	public void dischargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {
		if (!Energy.valid(stack)) {
			return;
		}

		Energy.of(stack)
			.into(
				Energy
				      .of(blockEntityPowerAcceptor)
			)
			.move();

	}

	@Override
	public void chargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {
		if (!Energy.valid(stack)) {
			return;
		}

		Energy.of(blockEntityPowerAcceptor)
			.into(
				Energy
					.of(stack)
			)
			.move();
	}

	@Override
	public void chargeItem(ItemPowerManager sourcePowerItem, ItemStack targetStack) {
		if (!Energy.valid(targetStack)) {
			return;
		}
		Energy.of(sourcePowerItem.getStack())
			.into(
				Energy
					.of(targetStack)
			)
			.move();
	}
}
