package reborncore.common.powerSystem;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.*;
import ic2.api.info.Info;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import reborncore.RebornCore;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.api.power.IPowerConfig;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.forge.ForgePowerManager;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.common.tile.TileLegacyMachineBase;
import reborncore.common.util.StringUtils;

import java.util.List;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2") })
public abstract class TilePowerAcceptor extends RFProviderTile implements IEnergyReceiver, IEnergyProvider, // Cofh
	IEnergyInterfaceTile, IListInfoProvider, // TechReborn
	IEnergyTile, IEnergySink, IEnergySource // Ic2
{
	public int tier;
	protected boolean addedToEnet;
	ForgePowerManager forgePowerManager;
	private double energy;

	public TilePowerAcceptor(int tier) {
		this.tier = tier;
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.created(this);
		}
	}


	@Override
	public void update() {
		super.update();
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.update(this);
		} else if(!getPowerConfig().eu() && !RebornCore.isIC2Loaded && getEnergy() > 0) { //Tesla or IC2 should handle this if enabled, so only do this without tesla
			for(EnumFacing side : EnumFacing.values()){
				if(canProvideEnergy(side)){
					TileEntity tile = world.getTileEntity(pos.offset(side));
					if(tile instanceof IEnergyInterfaceTile){
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
						if(eFace.getTier().ordinal() < getTier().ordinal()){
							for (int j = 0; j < 2; ++j) {
								double d3 = (double)pos.getX() + world.rand.nextDouble() + (side.getFrontOffsetX() / 2);
								double d8 = (double)pos.getY() + world.rand.nextDouble() + 1;
								double d13 = (double)pos.getZ() + world.rand.nextDouble()+ (side.getFrontOffsetZ() / 2);
								world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
							}
						} else {
							if(eFace.canAcceptEnergy(side.getOpposite()) && eFace.canAddEnergy(Math.min(getEnergy(), getMaxOutput()))){
								eFace.addEnergy(this.useEnergy(Math.min(getEnergy(), getMaxOutput())));
							}
						}
					}
				}
			}
		}

		if(RebornCore.isIC2Loaded){
			onLoaded();
		}
	}

	@Optional.Method(modid = "IC2")
	public void onLoaded() {
		if (getPowerConfig().eu() && !addedToEnet &&
			!FMLCommonHandler.instance().getEffectiveSide().isClient() &&
			Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));

			addedToEnet = true;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void onChunkUnload() {
		super.onChunkUnload();
		if (getPowerConfig().eu()) {
			if (addedToEnet && Info.isIc2Available()) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));

				addedToEnet = false;
			}
		}
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double getDemandedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return Math.min(getMaxPower() - getEnergy(), getMaxInput());
	}

	@Override
	@Optional.Method(modid = "IC2")
	public int getSinkTier() {
		return tier;
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		setEnergy(getEnergy() + amount);
		return 0;
	}

	@Override
	@Optional.Method(modid = "IC2")
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canAcceptEnergy(enumFacing);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canProvideEnergy(enumFacing);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double getOfferedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return Math.min(getEnergy(), getMaxOutput());
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void drawEnergy(double amount) {
		useEnergy((int) amount);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public int getSourceTier() {
		return tier;
	}
	// END IC2

	// COFH
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if (!getPowerConfig().rf())
			return false;
		return canAcceptEnergy(from) || canProvideEnergy(from);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!getPowerConfig().rf())
			return 0;
		if (!canAcceptEnergy(from)) {
			return 0;
		}
		maxReceive *= RebornCoreConfig.euPerRF;
		int energyReceived = Math.min(getMaxEnergyStored(null) - getEnergyStored(null),
			Math.min((int) this.getMaxInput() * RebornCoreConfig.euPerRF, maxReceive));

		if (!simulate) {
			setEnergy(getEnergy() + energyReceived);
		}
		return energyReceived / RebornCoreConfig.euPerRF;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		if (!getPowerConfig().rf())
			return 0;
		return ((int) getEnergy() * RebornCoreConfig.euPerRF);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if (!getPowerConfig().rf())
			return 0;
		return ((int) getMaxPower() * RebornCoreConfig.euPerRF);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (!getPowerConfig().rf())
			return 0;
		if (!canProvideEnergy(from)) {
			return 0;
		}
		maxExtract *= RebornCoreConfig.euPerRF;
		int energyExtracted = Math.min(getEnergyStored(null), Math.min(maxExtract, maxExtract));

		if (!simulate) {
			setEnergy(energy - energyExtracted);
		}
		return energyExtracted * RebornCoreConfig.euPerRF;
	}
	// END COFH

	// TechReborn

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(double energy) {
		this.energy = energy;

		if (this.getEnergy() > getMaxPower()) {
			this.setEnergy(getMaxPower());
		} else if (this.energy < 0) {
			this.setEnergy(0);
		}
	}

	@Override
	public double addEnergy(double energy) {
		return addEnergy(energy, false);
	}

	@Override
	public double addEnergy(double energy, boolean simulate) {
		double energyReceived = Math.min(getMaxPower(), Math.min(this.getMaxPower(), energy));

		if (!simulate) {
			setEnergy(getEnergy() + energyReceived);
		}
		return energyReceived;
	}

	@Override
	public boolean canUseEnergy(double input) {
		return input <= energy;
	}

	@Override
	public double useEnergy(double energy) {
		return useEnergy(energy, false);
	}

	@Override
	public double useEnergy(double extract, boolean simulate) {
		if (extract > energy) {
			double tempEnergy = energy;
			setEnergy(0);
			return tempEnergy;
		}
		if (!simulate) {
			setEnergy(energy - extract);
		}
		return extract;
	}

	@Override
	public boolean canAddEnergy(double energy) {
		return this.energy + energy <= getMaxPower();
	}
	// TechReborn END

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		energy = data.getDouble("energy");
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.readFromNBT(tag, this);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", energy);
		tag.setTag("TilePowerAcceptor", data);
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.writeToNBT(tag, this);
		}
		return tag;
	}

	public void readFromNBTWithoutCoords(NBTTagCompound tag) {
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		energy = data.getDouble("energy");
	}

	public NBTTagCompound writeToNBTWithoutCoords(NBTTagCompound tag) {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", energy);
		tag.setTag("TilePowerAcceptor", data);
		return tag;
	}

	@Override
	public void addInfo(List<String> info, boolean isRealTile) {
		info.add(TextFormatting.GRAY + "Max Energy: " + TextFormatting.GOLD
			+ PowerSystem.getLocaliszedPowerFormatted(getMaxPower()));
		if (getMaxInput() != 0) {
			info.add(TextFormatting.GRAY + "Input Rate: " + TextFormatting.GOLD
				+ PowerSystem.getLocaliszedPowerFormatted(getMaxInput()));
		}
		if (getMaxOutput() != 0) {
			info.add(TextFormatting.GRAY + "Output Rate: " + TextFormatting.GOLD
				+ PowerSystem.getLocaliszedPowerFormatted(getMaxOutput()));
		}
		info.add(TextFormatting.GRAY + "Tier: " + TextFormatting.GOLD + StringUtils.toFirstCapitalAllLowercase(getTier().toString()));
		// if(isRealTile){ //TODO sync to client
		// info.add(TextFormatting.LIGHT_PURPLE + "Stored energy " +
		// TextFormatting.GREEN + getEUString(energy));
		// }
	}

	public double getFreeSpace() {
		return getMaxPower() - energy;
	}

	public void charge(int slot) {
		if (getStackInSlot(slot) != null) {
			if (getStackInSlot(slot).getItem() instanceof IEnergyItemInfo) {
				if (getEnergy() != 0) {
					ItemStack stack = getStackInSlot(slot);
					double maxPower = PoweredItem.getMaxPower(stack);
					double energy = PoweredItem.getEnergy(stack);
					IEnergyItemInfo iEnergyItemInfo = (IEnergyItemInfo) stack.getItem();
					if (energy < maxPower) {
						double transfer =  Math.min(Math.min(iEnergyItemInfo.getMaxTransfer(stack), getEnergy()), maxPower - energy);
						if(PoweredItem.canUseEnergy(transfer, stack)){
							PoweredItem.useEnergy(transfer, stack);
							addEnergy(transfer);
						}

					}
				}
			}
		}
	}

	public int getEnergyScaled(int scale) {
		return (int) ((energy * scale / getMaxPower()));
	}

	public IPowerConfig getPowerConfig() {
		return RebornCoreConfig.getRebornPower();
	}

	//Tesla

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			if (TeslaManager.manager.hasCapability(capability, facing, this)) {
				return true;
			}
		}
		if (getPowerConfig().forge()) {
			if (capability == CapabilityEnergy.ENERGY) {
				return true;
			}
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			T teslaCap = TeslaManager.manager.getCapability(capability, facing, this);
			if (capability != null) {
				return teslaCap;
			}
		}
		if (getPowerConfig().forge()) {
			if (capability == CapabilityEnergy.ENERGY) {
				if (forgePowerManager == null) {
					forgePowerManager = new ForgePowerManager(this, facing);
				}
				forgePowerManager.setFacing(facing);
				return (T) forgePowerManager;
			}
		}
		return super.getCapability(capability, facing);
	}

	//End Tesla
}
