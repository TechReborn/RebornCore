package reborncore.ic2;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.*;
import ic2.api.info.ILocatable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.common.powerSystem.TilePowerAcceptor;

public class IC2EnergyBase implements IEnergyTile, IEnergySink, IEnergySource, ExternalPowerHandler, ILocatable {

	TilePowerAcceptor powerAcceptor;
	protected boolean addedToEnet;

	public IC2EnergyBase(TilePowerAcceptor powerAcceptor) {
		this.powerAcceptor = powerAcceptor;
	}

	@Override
	public double getDemandedEnergy() {
		return powerAcceptor.getMaxPower() - powerAcceptor.getEnergy();
	}

	@Override
	public int getSinkTier() {
		return powerAcceptor.getTier().getIC2Tier();
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		double used = powerAcceptor.addEnergy(amount);
		return (amount - used);
	}

	// IEnergyAcceptor
	@Override
	@Optional.Method(modid = "ic2")
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return powerAcceptor.canAcceptEnergy(enumFacing);
	}

	// IEnergySource
	@Override
	public double getOfferedEnergy() {
		return Math.min(powerAcceptor.getEnergy(), powerAcceptor.getMaxOutput());
	}

	@Override
	public void drawEnergy(double amount) {
		powerAcceptor.useEnergy((int) amount);
	}

	@Override
	public int getSourceTier() {
		return powerAcceptor.getTier().getIC2Tier();
	}

	// IEnergyEmitter
	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
		return powerAcceptor.canProvideEnergy(enumFacing);
	}

	@Override
	public void tick() {
		if(powerAcceptor.getWorld().isRemote){
			return;
		}
		if (!addedToEnet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
		}
	}

	@Override
	public void unload() {
		if (addedToEnet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void invalidate() {
		unload();
	}

	@Override
	public BlockPos getPosition() {
		return powerAcceptor.getPos();
	}

	@Override
	public World getWorldObj() {
		return powerAcceptor.getWorld();
	}
	// END IC2
}
