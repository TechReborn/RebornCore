package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.EnergyBlockEntity;
import reborncore.common.RebornCoreConfig;

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
				if (powerAcceptor.canProvideEnergy(side)) {
					BlockEntity blockEntity = powerAcceptor.getWorld().getBlockEntity(powerAcceptor.getPos().offset(side));
					if (blockEntity == null) {
						continue;
					} else if (blockEntity instanceof EnergyBlockEntity) {
						EnergyBlockEntity eFace = (EnergyBlockEntity) blockEntity;
						if (eFace.canAcceptEnergy(side.getOpposite())) {
							acceptors.put(side, blockEntity);
						}
					}
				}
			}
		}

		if (acceptors.size() > 0) {
			double drain = powerAcceptor.useEnergy(Math.min(powerAcceptor.getEnergy(), powerAcceptor.getMaxOutput()), true);
			double energyShare = drain / acceptors.size();
			double remainingEnergy = drain;

			if (energyShare > 0) {
				for (Map.Entry<Direction, BlockEntity> entry : acceptors.entrySet()) {
					Direction side = entry.getKey();
					BlockEntity blockEntity = entry.getValue();
					if (blockEntity instanceof EnergyBlockEntity) {
						EnergyBlockEntity eFace = (EnergyBlockEntity) blockEntity;
						if (RebornCoreConfig.smokeHighTeir && powerAcceptor.handleTierWithPower() && (eFace.getTier().ordinal() < powerAcceptor.getPushingTier().ordinal())) {

							World world = powerAcceptor.getWorld();
							BlockPos pos = powerAcceptor.getPos();

							for (int j = 0; j < 2; ++j) {
								double d3 = (double) pos.getX() + world.random.nextDouble()
										+ (side.getOffsetX() / 2);
								double d8 = (double) pos.getY() + world.random.nextDouble() + 1;
								double d13 = (double) pos.getZ() + world.random.nextDouble()
										+ (side.getOffsetZ() / 2);
								((ServerWorld) world).spawnParticles(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 2, 0.0D, 0.0D, 0.0D, 0.0D);
							}
						} else {
							double filled = eFace.addEnergy(Math.min(energyShare, remainingEnergy), false);
							remainingEnergy -= powerAcceptor.useEnergy(filled, false);
						}
					}
				}
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