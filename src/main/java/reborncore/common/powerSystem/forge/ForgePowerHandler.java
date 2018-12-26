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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ForgePowerHandler implements ExternalPowerHandler {
	TilePowerAcceptor powerAcceptor;
	ForgePowerManager powerManager;

	public ForgePowerHandler(TilePowerAcceptor powerAcceptor) {
		this.powerAcceptor = powerAcceptor;
		this.powerManager = new ForgePowerManager(powerAcceptor, null);
	}

	public void tick() {
		Map<EnumFacing, TileEntity> acceptors = new HashMap<EnumFacing, TileEntity>();
		if (powerAcceptor.getEnergy() > 0) { // Tesla or IC2 should handle this if enabled, so only do this without tesla
			for (EnumFacing side : EnumFacing.values()) {
				if (powerAcceptor.canProvideEnergy(side)) {
					TileEntity tile = powerAcceptor.getWorld().getTileEntity(powerAcceptor.getPos().offset(side));
					if (tile == null) {
						continue;
					} else if (ExternalPowerSystems.isPoweredTile(tile)) {
						// NB: This means we can't register ForgePowerHandler in the ExternalPowerSystems list
						// NB: Otherwise the check will not work.

						// Other power net will take care about this
						continue;
					} else if (tile instanceof IEnergyInterfaceTile) {
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
						if (eFace.canAcceptEnergy(side.getOpposite())) {
							acceptors.put(side, tile);
						}
					} else if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
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
				for (Map.Entry<EnumFacing, TileEntity> entry : acceptors.entrySet()) {
					EnumFacing side = entry.getKey();
					TileEntity tile = entry.getValue();
					if (tile instanceof IEnergyInterfaceTile) {
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
						if (RebornCoreConfig.smokeHighTeir && powerAcceptor.handleTierWithPower() && (eFace.getTier().ordinal() < powerAcceptor.getPushingTier().ordinal())) {

							World world = powerAcceptor.getWorld();
							BlockPos pos = powerAcceptor.getPos();

							for (int j = 0; j < 2; ++j) {
								double d3 = (double) pos.getX() + world.rand.nextDouble()
										+ (side.getXOffset() / 2);
								double d8 = (double) pos.getY() + world.rand.nextDouble() + 1;
								double d13 = (double) pos.getZ() + world.rand.nextDouble()
										+ (side.getZOffset() / 2);
								((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, false, d3, d8, d13, 2, 0.0D, 0.0D, 0.0D, 0.0D);
							}
						} else {
							double filled = eFace.addEnergy(Math.min(energyShare, remainingEnergy), false);
							remainingEnergy -= powerAcceptor.useEnergy(filled, false);
						}
					} else if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
						IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
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

	public void unload() {

	}

	public void invalidate() {

	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY && (powerAcceptor.canAcceptEnergy(facing) || powerAcceptor.canProvideEnergy(facing));
	}

	@Nullable @Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && (powerAcceptor.canAcceptEnergy(facing) || powerAcceptor.canProvideEnergy(facing))) {
			if (powerManager == null) {
				powerManager = new ForgePowerManager(powerAcceptor, facing);
			}
			powerManager.setFacing(facing);
			return CapabilityEnergy.ENERGY.cast(powerManager);
		}

		return null;
	}
}
