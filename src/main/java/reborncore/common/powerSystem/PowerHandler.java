package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;
import reborncore.api.power.ExternalPowerHandler;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergySide;

import java.util.HashMap;
import java.util.Map;

public class PowerHandler implements ExternalPowerHandler {
	PowerAcceptorBlockEntity powerAcceptor;
	
	public PowerHandler(PowerAcceptorBlockEntity powerAcceptor) {
		this.powerAcceptor = powerAcceptor;
	}

	@Override
	public void tick() {
		Map<Direction, BlockEntity> acceptors = new HashMap<>();
		if (powerAcceptor.getEnergy() > 0) { // Tesla or IC2 should handle this if enabled, so only do this without tesla
			for (Direction side : Direction.values()) {
				BlockEntity blockEntity = powerAcceptor.getWorld().getBlockEntity(powerAcceptor.getPos().offset(side));
				if (blockEntity == null) {
					continue;
				}
				Energy.of(powerAcceptor)
					.side(EnergySide.fromMinecraft(side))
					.into(
						Energy.of(blockEntity).side(EnergySide.fromMinecraft(side.getOpposite()))
					)
					.move();

			}
		}
	}

	@Override
	public void unload() {

	}

	@Override
	public void invalidate() {

	}

	/**
	 * Checks whether the provided blockEntity is considered a powered blockEntity by other power systems already
	 */
	private static boolean isOtherPowered(BlockEntity BlockEntity, Direction facing) {
		return ExternalPowerSystems.externalPowerHandlerList.stream()
				.filter(externalPowerManager -> !(externalPowerManager instanceof PowerHandler))
				.anyMatch(externalPowerManager -> externalPowerManager.isPowered(BlockEntity, facing));
	}
}