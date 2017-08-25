/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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
import ic2.api.info.Info;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.api.power.IPowerConfig;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;
import reborncore.common.powerSystem.forge.ForgePowerManager;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.common.tile.TileLegacyMachineBase;
import reborncore.common.util.StringUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
	public int extraTeir;

	public void setExtraPowerStoage(double extraPowerStoage) {
		this.extraPowerStoage = extraPowerStoage;
	}

	public TilePowerAcceptor() {
		checkTeir();
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.created(this);
		}
	}

	public TilePowerAcceptor(EnumPowerTier tier) {
		checkTeir();
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.created(this);
		}
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

	@Override
	public void update() {
		super.update();

		if (getEnergy() > 0 && !world.isRemote) { //Tesla or IC2 should handle this if enabled, so only do this without tesla
			for (EnumFacing side : EnumFacing.values()) {
				if (canProvideEnergy(side)) {
					TileEntity tile = world.getTileEntity(pos.offset(side));
					if (tile == null) {
						continue;
					}
					if (tile instanceof IEnergyInterfaceTile) {
						IEnergyInterfaceTile eFace = (IEnergyInterfaceTile) tile;
						if (handleTierWithPower() && eFace.getTier().ordinal() < getPushingTier().ordinal()) {
							for (int j = 0; j < 2; ++j) {
								double d3 = (double) pos.getX() + world.rand.nextDouble() + (side.getFrontOffsetX() / 2);
								double d8 = (double) pos.getY() + world.rand.nextDouble() + 1;
								double d13 = (double) pos.getZ() + world.rand.nextDouble() + (side.getFrontOffsetZ() / 2);
								world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
							}
						} else {
							double drain = useEnergy(Math.min(getEnergy(), getMaxOutput()), true);
							if (drain > 0) {
								double filled = eFace.addEnergy(drain, false);
								useEnergy(filled, false);
							}
						}
					} else if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
						IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
						if (forgePowerManager != null && energyStorage != null && energyStorage.canReceive() && this.canProvideEnergy(side)) {
							int drain = forgePowerManager.extractEnergy(Math.min(forgePowerManager.getEnergyStored(), (int) getMaxOutput() * RebornCoreConfig.euPerFU), true);
							if (drain > 0) {
								int filled = energyStorage.receiveEnergy(drain, false);
								forgePowerManager.extractEnergy(filled, false);
							}
						}
					} else if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
						TeslaManager.manager.update(this);
					}
				}
			}
		}

		if (RebornCoreConfig.isIC2Loaded && getPowerConfig().eu() && Info.isIc2Available()) {
			onLoaded();
		}
	}

	@Optional.Method(modid = "ic2")
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
	@Optional.Method(modid = "ic2")
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
	@Optional.Method(modid = "ic2")
	public double getDemandedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return Math.min(getMaxPower() - getEnergy(), getMaxInput());
	}

	@Override
	@Optional.Method(modid = "ic2")
	public int getSinkTier() {
		return getTier().getIC2Tier();
	}

	@Override
	@Optional.Method(modid = "ic2")
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		setEnergy(getEnergy() + amount);
		return 0;
	}

	@Override
	@Optional.Method(modid = "ic2")
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canAcceptEnergy(enumFacing);
	}

	@Override
	@Optional.Method(modid = "ic2")
	public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canProvideEnergy(enumFacing);
	}

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
	// END IC2

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
	// END COFH

	// TechReborn

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(double energy) {
		this.energy = Math.max(Math.min(energy, getMaxPower()), 0);
	}

	@Override
	public double addEnergy(double energy) {
		return addEnergy(energy, false);
	}

	@Override
	public double addEnergy(double energy, boolean simulate) {
		double energyReceived = Math.min(getMaxPower(), Math.min(getFreeSpace(), energy));

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
	// TechReborn END

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound data = tag.getCompoundTag("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT())
			this.setEnergy(data.getDouble("energy"));
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.readFromNBT(tag, this);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("energy", getEnergy());
		tag.setTag("TilePowerAcceptor", data);
		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
			TeslaManager.manager.writeToNBT(tag, this);
		}
		return tag;
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

	@Override
	public void addInfo(List<String> info, boolean isRealTile) {
		info.add(TextFormatting.GRAY + "Max Energy: " + TextFormatting.GOLD
			+ getLocaliszedPowerFormatted((int) getMaxPower()));
		if (getMaxInput() != 0) {
			info.add(TextFormatting.GRAY + "Input Rate: " + TextFormatting.GOLD
				+ getLocaliszedPowerFormatted((int) getMaxInput()));
		}
		if (getMaxOutput() != 0) {
			info.add(TextFormatting.GRAY + "Output Rate: " + TextFormatting.GOLD
				+ getLocaliszedPowerFormatted((int) getMaxOutput()));
		}
		info.add(TextFormatting.GRAY + "Tier: " + TextFormatting.GOLD + StringUtils.toFirstCapitalAllLowercase(getTier().toString()));
	}

	public double getFreeSpace() {
		return getMaxPower() - getEnergy();
	}

	public void charge(int slot) {
		if (getStackInSlot(slot) != ItemStack.EMPTY) {
			if (getStackInSlot(slot).getItem() instanceof IEnergyItemInfo) {
				if (getEnergy() != 0) {
					ItemStack stack = getStackInSlot(slot);
					double maxPower = PoweredItem.getMaxPower(stack);
					double energy = PoweredItem.getEnergy(stack);
					IEnergyItemInfo iEnergyItemInfo = (IEnergyItemInfo) stack.getItem();
					if (energy < maxPower) {
						double transfer = Math.min(Math.min(iEnergyItemInfo.getMaxTransfer(stack), getEnergy()), maxPower - energy);
						if (PoweredItem.canUseEnergy(transfer, stack)) {
							PoweredItem.useEnergy(transfer, stack);
							addEnergy(transfer);
						}

					}
				}
			}
		}
	}

	public int getEnergyScaled(int scale) {
		return (int) ((getEnergy() * scale / getMaxPower()));
	}

	public IPowerConfig getPowerConfig() {
		return RebornCoreConfig.getRebornPower();
	}

	public String getLocaliszedPowerFormatted(final int eu) {
		switch (PowerSystem.getDisplayPower()) {
			case EU:
				return NumberFormat.getIntegerInstance(Locale.forLanguageTag("en_US")).format(eu) + " "
					+ EnergySystem.EU.abbreviation;
			case TESLA:
				return NumberFormat.getIntegerInstance(Locale.forLanguageTag("en_US"))
					.format(eu * RebornCoreConfig.euPerFU) + " " + EnergySystem.TESLA.abbreviation;
			default:
				return NumberFormat.getIntegerInstance(Locale.forLanguageTag("en_US"))
					.format(eu * RebornCoreConfig.euPerFU) + " " + EnergySystem.FE.abbreviation;
		}
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
			if (TeslaManager.manager.isTeslaCapability(capability)) {
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

	public abstract double getBaseMaxPower();

	public abstract double getBaseMaxOutput();

	public abstract double getBaseMaxInput();

	@Deprecated //Dont set the tier any where
	public EnumPowerTier getBaseTier() {
		return null;
	}

	@Override
	public double getMaxPower() {
		return getBaseMaxPower() + extraPowerStoage;
	}

	@Override
	public double getMaxOutput() {
		return getBaseMaxOutput();
	}

	@Override
	public double getMaxInput() {
		return getBaseMaxInput();
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
			return EnumPowerTier.INSANE;
		}
		return baseTier;
	}

	@Override
	public void resetUpgrades() {
		super.resetUpgrades();
		extraPowerStoage = 0;
		extraTeir = 0;
		if (getEnergy() > getMaxPower()) { //Makes sure we do not have too much power, and voids it
			setEnergy(getMaxPower());
		}
	}

	public boolean handleTierWithPower() {
		return true;
	}
}
