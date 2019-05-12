/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.powerSystem.forge;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ForgePowerHandler implements ExternalPowerHandler {
	TilePowerAcceptor powerAcceptor;
	ForgeEnergyStorage powerManager;

	public ForgePowerHandler(TilePowerAcceptor powerAcceptor) {
		this.powerAcceptor = powerAcceptor;
		this.powerManager = new ForgeEnergyStorage(powerAcceptor, null);
	}

	@Override
	public void tick() {
		Map<Direction, BlockEntity> acceptors = new HashMap<>();
		if (powerAcceptor.getEnergy() > 0) { // Tesla or IC2 should handle this if enabled, so only do this without tesla
			for (Direction side : Direction.values()) {
				if (powerAcceptor.canProvideEnergy(side)) {
					BlockEntity tile = powerAcceptor.getWorld().getBlockEntity(powerAcceptor.getPos().offset(side));
					if (tile == null) {
						continue;
					} else if (isOtherPoweredTile(tile, side.getOpposite())) {
						// Other power net will take care about this

						continue;
					} else if (tile instanceof IEnergyInterfaceTile) {
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
						if (eFace.canAcceptEnergy(side.getOpposite())) {
							acceptors.put(side, tile);
						}
					} else if (tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).isPresent()) {
						acceptors.put(side, tile);
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
					BlockEntity tile = entry.getValue();
					if (tile instanceof IEnergyInterfaceTile) {
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
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
					} else if (tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).isPresent()) {
						IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).orElseGet(null);
						if (powerManager != null && energyStorage != null && energyStorage.canReceive()
							&& powerAcceptor.canProvideEnergy(side)) {
							int filled = energyStorage.receiveEnergy(
								(int) Math.min(energyShare, remainingEnergy) * RebornCoreConfig.euPerFU, false);
							remainingEnergy -= powerAcceptor.useEnergy(filled / RebornCoreConfig.euPerFU, false);
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

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(
		@Nonnull
			Capability<T> cap, Direction facing) {
		if (cap == CapabilityEnergy.ENERGY && (powerAcceptor.canAcceptEnergy(facing) || powerAcceptor.canProvideEnergy(facing))) {
			if (powerManager == null) {
				powerManager = new ForgeEnergyStorage(powerAcceptor, facing);
			}
			return LazyOptional.of(new NonNullSupplier<T>() {
				@Nonnull
				@Override
				public T get() {
					return (T) powerManager;
				}
			});
		}
		return LazyOptional.empty();
	}

	/**
	 * Checks whether the provided tile is considered a powered tile by other power systems already
	 */
	private static boolean isOtherPoweredTile(BlockEntity tileEntity, Direction facing) {
		return ExternalPowerSystems.externalPowerHandlerList.stream()
			.filter(externalPowerManager -> !(externalPowerManager instanceof ForgePowerManager))
			.anyMatch(externalPowerManager -> externalPowerManager.isPoweredTile(tileEntity, facing));
	}
}
