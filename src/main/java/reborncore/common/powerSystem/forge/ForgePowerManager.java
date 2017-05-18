package reborncore.common.powerSystem.forge;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.common.powerSystem.TilePowerAcceptor;

import javax.annotation.Nullable;

public class ForgePowerManager implements IEnergyStorage {

	TilePowerAcceptor acceptor;
	@Nullable
	EnumFacing facing;

	public ForgePowerManager(TilePowerAcceptor acceptor, @Nullable EnumFacing facing) {
		this.acceptor = acceptor;
		this.facing = facing;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return acceptor.receiveEnergy(facing, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return acceptor.extractEnergy(facing, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return acceptor.getEnergyStored(facing);
	}

	@Override
	public int getMaxEnergyStored() {
		return acceptor.getMaxEnergyStored(facing);
	}

	@Override
	public boolean canExtract() {
		return acceptor.canProvideEnergy(facing);
	}

	@Override
	public boolean canReceive() {
		return acceptor.canAcceptEnergy(facing);
	}

	public void setFacing(@Nullable EnumFacing facing) {
		this.facing = facing;
	}
}
