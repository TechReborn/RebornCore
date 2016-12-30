package reborncore.mixins;

import net.minecraft.util.EnumFacing;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.asm.mixin.Inject;
import reborncore.asm.mixin.Mixin;

@Mixin(target = "net.minecraft.tileentity.TileEntityChest")
public class MixinChestTile implements IEnergyInterfaceTile {
	@Override
	@Inject
	public double getEnergy() {
		return 0;
	}

	@Override
	@Inject
	public void setEnergy(double energy) {

	}

	@Override
	@Inject
	public double getMaxPower() {
		return 0;
	}

	@Override
	@Inject
	public boolean canAddEnergy(double energy) {
		return false;
	}

	@Override
	@Inject
	public double addEnergy(double energy) {
		return 0;
	}

	@Override
	@Inject
	public double addEnergy(double energy, boolean simulate) {
		return 0;
	}

	@Override
	@Inject
	public boolean canUseEnergy(double energy) {
		return false;
	}

	@Override
	@Inject
	public double useEnergy(double energy) {
		return 0;
	}

	@Override
	@Inject
	public double useEnergy(double energy, boolean simulate) {
		return 0;
	}

	@Override
	@Inject
	public boolean canAcceptEnergy(EnumFacing direction) {
		return true;
	}

	@Override
	@Inject
	public boolean canProvideEnergy(EnumFacing direction) {
		return true;
	}

	@Override
	@Inject
	public double getMaxOutput() {
		return 64;
	}

	@Override
	@Inject
	public double getMaxInput() {
		return 32;
	}

	@Override
	@Inject
	public EnumPowerTier getTier() {
		return EnumPowerTier.EXTREME;
	}
}
