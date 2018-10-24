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

package reborncore.common.powerSystem;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import reborncore.RebornCore;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.power.IPowerConfig;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.forge.ForgePowerManager;
import reborncore.common.tile.TileLegacyMachineBase;
import reborncore.common.util.IC2ItemCharger;
import reborncore.common.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "ic2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2") })
public abstract class TilePowerAcceptor extends TileLegacyMachineBase implements
	IEnergyInterfaceTile, IListInfoProvider, // TechReborn
	IEnergyTile, IEnergySink, IEnergySource // Ic2
{
	private EnumPowerTier tier;
	protected boolean addedToEnet;
	ForgePowerManager forgePowerManager = new ForgePowerManager(this, null);
	private double energy;

	public double extraPowerStoage;
	public double extraPowerInput;
	public int extraTeir;
	public double powerChange;
	public double powerLastTick;
	public boolean checkOverfill = true; //Set to flase to disable the overfill check.

	public TilePowerAcceptor() {
		checkTeir();
	}

	public TilePowerAcceptor(EnumPowerTier tier) {
		checkTeir();
	}

	public void checkTeir() {
		if (getBaseTier() == null) {
			if (this.getMaxInput() == 0) {
				tier = EnumPowerTier.getTeir((int) this.getBaseMaxOutput());
			} else {
				tier = EnumPowerTier.getTeir((int) this.getBaseMaxInput());
			}
		}
	}
	
	public void setExtraPowerStoage(double extraPowerStoage) {
		this.extraPowerStoage = extraPowerStoage;
	}
	
	public double getFreeSpace() {
		return getMaxPower() - getEnergy();
	}

	/**
	 * Charge machine from battery placed inside inventory slot
	 * 
	 * @param slot int Slot ID for battery slot
	 */
	public void charge(int slot) {
		if (world.isRemote) {
			return;
		}
		double chargeEnergy = Math.min(getFreeSpace(), getMaxInput());
		if (chargeEnergy <= 0.0 ) {
			return;
		}
		ItemStack batteryStack = getStackInSlot(slot);
		if (batteryStack.isEmpty()) {
			return;
		}
		if (batteryStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage batteryEnergy = batteryStack.getCapability(CapabilityEnergy.ENERGY, null);
			if (batteryEnergy.getEnergyStored() > 0) {
				int extracted = batteryEnergy.extractEnergy((int) (chargeEnergy * RebornCoreConfig.euPerFU), false);
				addEnergy( extracted / RebornCoreConfig.euPerFU);
			}
		} else if (RebornCore.proxy.ic2Loaded) {
			IC2ItemCharger.dischargeIc2Item(this, batteryStack);
		}

	}

	public int getEnergyScaled(int scale) {
		return (int) ((getEnergy() * scale / getMaxPower()));
	}

	public IPowerConfig getPowerConfig() {
		return RebornCoreConfig.getRebornPower();
	}

	public void readFromNBTWithoutCoords(NBTTagCompound tag) {
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT())
			this.setEnergy(data.getDouble("energy"));
	}

	public NBTTagCompound writeToNBTWithoutCoords(NBTTagCompound tag) {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", energy);
		tag.setTag("TilePowerAcceptor", data);
		return tag;
	}

	public boolean shouldHanldeEnergyNBT() {
		return true;
	}
	
	public boolean handleTierWithPower() {
		return true;
	}

	public double getPowerChange() {
		return powerChange;
	}

	public void setPowerChange(double powerChange) {
		this.powerChange = powerChange;
	}
	
	@Optional.Method(modid = "ic2")
	public void onLoaded() {
		if(world.isRemote){
			return;
		}
		if (getPowerConfig().eu() && !addedToEnet && RebornCore.proxy.ic2Loaded) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
		}
	}

	// TileLegacyMachineBase
	@Override
	public void update() {
		super.update();
		if(world.isRemote){
			return;
		}
		
		if (RebornCoreConfig.isIC2Loaded && getPowerConfig().eu()) {
			onLoaded();
		}
		
		Map<EnumFacing, TileEntity> acceptors = new HashMap<EnumFacing, TileEntity>();
		if (getEnergy() > 0) { // Tesla or IC2 should handle this if enabled, so only do this without tesla
			for (EnumFacing side : EnumFacing.values()) {
				if (canProvideEnergy(side)) {
					TileEntity tile = world.getTileEntity(pos.offset(side));
					if (tile == null) {
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
			double drain = useEnergy(Math.min(getEnergy(), getMaxOutput()), true);
			double energyShare = drain / acceptors.size();
			double remainingEnergy = drain;

			if (energyShare > 0) {
				for (Map.Entry<EnumFacing, TileEntity> entry : acceptors.entrySet()) {
					EnumFacing side = entry.getKey();
					TileEntity tile = entry.getValue();
					if (tile instanceof IEnergyInterfaceTile) {
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
						if (handleTierWithPower() && (eFace.getTier().ordinal() < getPushingTier().ordinal())) {
							for (int j = 0; j < 2; ++j) {
								double d3 = (double) pos.getX() + world.rand.nextDouble()
										+ (side.getFrontOffsetX() / 2);
								double d8 = (double) pos.getY() + world.rand.nextDouble() + 1;
								double d13 = (double) pos.getZ() + world.rand.nextDouble()
										+ (side.getFrontOffsetZ() / 2);
								world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
							}
						} else {
							double filled = eFace.addEnergy(Math.min(energyShare, remainingEnergy), false);
							remainingEnergy -= useEnergy(filled, false);
						}
					} else if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
						IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
						if (forgePowerManager != null && energyStorage != null && energyStorage.canReceive()
								&& this.canProvideEnergy(side)) {
							int filled = energyStorage.receiveEnergy(
									(int) Math.min(energyShare, remainingEnergy) * RebornCoreConfig.euPerFU, false);
							remainingEnergy -= useEnergy(filled / RebornCoreConfig.euPerFU, false);
						}
					}
				}
			}
		}

		powerChange = getEnergy() - powerLastTick;
		powerLastTick = getEnergy();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT())
			this.setEnergy(data.getDouble("energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", getEnergy());
		tag.setTag("TilePowerAcceptor", data);
		return tag;
	}
	
	@Override
	public void resetUpgrades() {
		super.resetUpgrades();
		extraPowerStoage = 0;
		extraTeir = 0;
		extraPowerInput = 0;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (getPowerConfig().forge()) {
			if (capability == CapabilityEnergy.ENERGY && (canAcceptEnergy(facing) || canProvideEnergy(facing))) {
				return true;
			}
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (getPowerConfig().forge()) {
			if (capability == CapabilityEnergy.ENERGY && (canAcceptEnergy(facing) || canProvideEnergy(facing))) {
				if (forgePowerManager == null) {
					forgePowerManager = new ForgePowerManager(this, facing);
				}
				forgePowerManager.setFacing(facing);
				return CapabilityEnergy.ENERGY.cast(forgePowerManager);
			}
		}
		return super.getCapability(capability, facing);
	}
	
	public abstract double getBaseMaxPower();

	public abstract double getBaseMaxOutput();

	public abstract double getBaseMaxInput();

	@Deprecated //Dont set the tier any where
	public EnumPowerTier getBaseTier() {
		return null;
	}
	
	// TileEntity
	@Override
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}

	@Override
	@Optional.Method(modid = "ic2")
	public void onChunkUnload() {
		super.onChunkUnload();
		if (getPowerConfig().eu()) {
			if (addedToEnet && RebornCore.proxy.ic2Loaded) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				addedToEnet = false;
			}
		}
	}
	
	// IEnergyInterfaceTile
	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(double energy) {
		if(!checkOverfill){
			this.energy = energy;
			return;
		}
		this.energy = Math.max(Math.min(energy, getMaxPower()), 0);
	}

	@Override
	public double addEnergy(double energy) {
		return addEnergy(energy, false);
	}

	@Override
	public double addEnergy(double energy, boolean simulate) {
		double energyReceived = Math.min(getMaxInput(), Math.min(getFreeSpace(), energy));

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
			extract = energy;
		}
		if (!simulate) {
			setEnergy(energy - extract);
		}
		return extract;
	}

	@Override
	public boolean canAddEnergy(double energyIn) {
		return getEnergy() + energyIn <= getMaxPower();
	}
	
	@Override
	public double getMaxPower() {
		return getBaseMaxPower() + extraPowerStoage;
	}

	@Override
	public double getMaxOutput() {
		double maxOutput = 0;
		if (this.extraTeir > 0) {
			maxOutput = this.getTier().getMaxOutput();
		}
		else {
			maxOutput = getBaseMaxOutput();	
		}
		return maxOutput;
	}

	@Override
	public double getMaxInput() {
		double maxInput = 0;
		if (this.extraTeir > 0) {
			maxInput = this.getTier().getMaxInput();
		}
		else {
			maxInput = getBaseMaxInput();
		}
		return maxInput + extraPowerInput;
	}

	public EnumPowerTier getPushingTier() {
		return getTier();
	}

	@Override
	public EnumPowerTier getTier() {
		EnumPowerTier baseTier = getBaseTier();
		if (baseTier == null) {
			if (tier == null) {
				checkTeir();
			}
			baseTier = tier;
		}
		if (extraTeir > 0) {
			for (EnumPowerTier tier : EnumPowerTier.values()) {
				if (tier.getIC2Tier() == baseTier.getIC2Tier() + extraTeir) {
					return tier;
				}
			}
			return EnumPowerTier.INFINITE;
		}
		return baseTier;
	}
	
	// IListInfoProvider
	@Override
	public void addInfo(List<String> info, boolean isRealTile) {
		info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.maxEnergy") + ": "
				+ TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxPower()));
		if (getMaxInput() != 0) {
			info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.inputRate") + ": "
					+ TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxInput()));
		}
		if (getMaxOutput() != 0) {
			info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.outputRate")
					+ ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxOutput()));
		}
		info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.tier") + ": "
				+ TextFormatting.GOLD + StringUtils.toFirstCapitalAllLowercase(getTier().toString()));
		if(isRealTile){
			info.add(TextFormatting.GRAY + StringUtils.t("reborncore.tooltip.energy.change")
				+ ": " + TextFormatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getPowerChange()) + "/t");
		}
	}
	
	// IEnergySink
	@Override
	@Optional.Method(modid = "ic2")
	public double getDemandedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return getMaxPower() - getEnergy();
	}

	@Override
	@Optional.Method(modid = "ic2")
	public int getSinkTier() {
		return getTier().getIC2Tier();
	}

	@Override
	@Optional.Method(modid = "ic2")
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		double used = addEnergy(amount);
		return (amount - used);
	}

	// IEnergyAcceptor
	@Override
	@Optional.Method(modid = "ic2")
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canAcceptEnergy(enumFacing);
	}
	
	// IEnergySource
	@Override
	@Optional.Method(modid = "ic2")
	public double getOfferedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return Math.min(getEnergy(), getMaxOutput());
	}

	@Override
	@Optional.Method(modid = "ic2")
	public void drawEnergy(double amount) {
		useEnergy((int) amount);
	}

	@Override
	@Optional.Method(modid = "ic2")
	public int getSourceTier() {
		return getTier().getIC2Tier();
	}

	// IEnergyEmitter
	@Override
	@Optional.Method(modid = "ic2")
	public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canProvideEnergy(enumFacing);
	}

	// Old cofh stuff, still used to implement Forge Energy, should be removed at somepoint
	@Deprecated
	public boolean canConnectEnergy(EnumFacing from) {
		return canAcceptEnergy(from) || canProvideEnergy(from);
	}

	@Deprecated
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!canAcceptEnergy(from)) {
			return 0;
		}
		int feReceived = (int) Math.min(getMaxEnergyStored(from) - getEnergyStored(from), Math.min(getMaxInput() * RebornCoreConfig.euPerFU, maxReceive));
		int euReceived = feReceived / RebornCoreConfig.euPerFU;
		feReceived = euReceived * RebornCoreConfig.euPerFU;

		if (!simulate) {
			setEnergy(getEnergy() + euReceived);
		}
		return feReceived;
	}

	@Deprecated
	public int getEnergyStored(EnumFacing from) {
		return ((int) getEnergy() * RebornCoreConfig.euPerFU);
	}

	@Deprecated
	public int getMaxEnergyStored(EnumFacing from) {
		return ((int) getMaxPower() * RebornCoreConfig.euPerFU);
	}

	@Deprecated
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (!canProvideEnergy(from)) {
			return 0;
		}
		int euExtracted = Math.min(getEnergyStored(null), maxExtract / RebornCoreConfig.euPerFU);
		int feExtracted = euExtracted * RebornCoreConfig.euPerFU;

		if (!simulate) {
			setEnergy(getEnergy() - euExtracted);
		}
		return feExtracted;
	}
}
