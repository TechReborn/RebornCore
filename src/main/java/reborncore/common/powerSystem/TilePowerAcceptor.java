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

import net.minecraft.ChatFormat;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.math.Direction;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.tile.TileMachineBase;
import reborncore.common.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TilePowerAcceptor extends TileMachineBase implements
	IEnergyInterfaceTile, IListInfoProvider // TechReborn
{
	private EnumPowerTier tier;
	private double energy;

	public double extraPowerStoage;
	public double extraPowerInput;
	public int extraTier;
	public double powerChange;
	public double powerLastTick;
	public boolean checkOverfill = true; //Set to false to disable the overfill check.
	// Some external power systems (EU) support multiple energy packets per tick, this allows machines to possibly emit
	// multiple packets in a tick. Other power systems such as FE will ignore this option.
	public int maxPacketsPerTick = 1;

	private List<ExternalPowerHandler> powerManagers;

	public TilePowerAcceptor(BlockEntityType<?> tileEntityType) {
		super(tileEntityType);
		checkTier();
		setupManagers();
	}

	// don't manually set tiers
	@Deprecated
	public TilePowerAcceptor(BlockEntityType<?> tileEntityType, EnumPowerTier tier) {
		this(tileEntityType);
	}

	private void setupManagers() {
		final TilePowerAcceptor tile = this;
		powerManagers = ExternalPowerSystems.externalPowerHandlerList.stream()
			.map(externalPowerManager -> externalPowerManager.createPowerHandler(tile))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public void checkTier() {
		if (getBaseTier() == null) {
			if (this.getMaxInput() == 0) {
				tier = EnumPowerTier.getTier((int) this.getBaseMaxOutput());
			} else {
				tier = EnumPowerTier.getTier((int) this.getBaseMaxInput());
			}
		}
	}

	public void setExtraPowerStoage(double extraPowerStoage) {
		this.extraPowerStoage = extraPowerStoage;
	}

	public void setMaxPacketsPerTick(int maxPacketsPerTick) {
		this.maxPacketsPerTick = maxPacketsPerTick;
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
		if (world.isClient) {
			return;
		}

		double chargeEnergy = Math.min(getFreeSpace(), getMaxInput());
		if (chargeEnergy <= 0.0) {
			return;
		}
		if (!getInventoryForTile().isPresent()) {
			return;
		}
		ItemStack batteryStack = getInventoryForTile().get().getStackInSlot(slot);
		if (batteryStack.isEmpty()) {
			return;
		}

		if (ExternalPowerSystems.isPoweredItem(batteryStack)) {
			ExternalPowerSystems.dischargeItem(this, batteryStack);
		}

	}

	public int getEnergyScaled(int scale) {
		return (int) ((getEnergy() * scale / getMaxPower()));
	}

	public void readWithoutCoords(CompoundTag tag) {
		CompoundTag data = tag.getCompound("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT()) {
			this.setEnergy(data.getDouble("energy"));
		}
	}

	public CompoundTag writeWithoutCoords(CompoundTag tag) {
		CompoundTag data = new CompoundTag();
		data.putDouble("energy", energy);
		tag.put("TilePowerAcceptor", data);
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

	// TileMachineBase
	@Override
	public void tick() {
		super.tick();
		if (world.isClient) {
			return;
		}

		powerManagers.forEach(ExternalPowerHandler::tick);

		powerChange = getEnergy() - powerLastTick;
		powerLastTick = getEnergy();
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		CompoundTag data = tag.getCompound("TilePowerAcceptor");
		if (shouldHanldeEnergyNBT()) {
			this.setEnergy(data.getDouble("energy"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		CompoundTag data = new CompoundTag();
		data.putDouble("energy", getEnergy());
		tag.put("TilePowerAcceptor", data);
		return tag;
	}

	@Override
	public void resetUpgrades() {
		super.resetUpgrades();
		extraPowerStoage = 0;
		extraTier = 0;
		extraPowerInput = 0;
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
//		LazyOptional<T> externalCap = powerManagers.stream()
//			.filter(Objects::nonNull)
//			.map(externalPowerHandler -> externalPowerHandler.getCapability(capability, facing))
//			.filter(LazyOptional::isPresent)
//			.findFirst()
//			.orElse(null);
//
//		if (externalCap != null) {
//			return externalCap;
//		}
//
//		return super.getCapability(capability, facing);
//	}

	public abstract double getBaseMaxPower();

	public abstract double getBaseMaxOutput();

	public abstract double getBaseMaxInput();

	@Deprecated //Dont set the tier any where
	public EnumPowerTier getBaseTier() {
		return null;
	}

	// TileEntity
	//@Override
	//TODO 1.13 tile patches are gone?
	public void invalidate() {
		//super.invalidate();

		powerManagers.forEach(ExternalPowerHandler::invalidate);
	}

	//@Override
	//TODO 1.13 tile patches are gone?
	public void onChunkUnload() {
		//super.onChunkUnload();

		powerManagers.forEach(ExternalPowerHandler::unload);
	}

	// IEnergyInterfaceTile
	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(double energy) {
		if (!checkOverfill) {
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

	@Override
	public double getMaxPower() {
		return getBaseMaxPower() + extraPowerStoage;
	}

	@Override
	public double getMaxOutput() {
		double maxOutput = 0;
		if (this.extraTier > 0) {
			maxOutput = this.getTier().getMaxOutput();
		} else {
			maxOutput = getBaseMaxOutput();
		}
		return maxOutput;
	}

	@Override
	public double getMaxInput() {
		double maxInput = 0;
		if (this.extraTier > 0) {
			maxInput = this.getTier().getMaxInput();
		} else {
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
				checkTier();
			}
			baseTier = tier;
		}
		if (extraTier > 0) {
			for (EnumPowerTier tier : EnumPowerTier.values()) {
				if (tier.ordinal() == baseTier.ordinal() + extraTier) {
					return tier;
				}
			}
			return EnumPowerTier.INFINITE;
		}
		return baseTier;
	}

	// IListInfoProvider
	@Override
	public void addInfo(List<Component> info, boolean isRealTile, boolean hasData) {
		info.add(new TextComponent(ChatFormat.GRAY + StringUtils.t("reborncore.tooltip.energy.maxEnergy") + ": "
			+ ChatFormat.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxPower())));
		if (getMaxInput() != 0) {
			info.add(new TextComponent(ChatFormat.GRAY + StringUtils.t("reborncore.tooltip.energy.inputRate") + ": "
				+ ChatFormat.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxInput())));
		}
		if (getMaxOutput() != 0) {
			info.add(new TextComponent(ChatFormat.GRAY + StringUtils.t("reborncore.tooltip.energy.outputRate")
				+ ": " + ChatFormat.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxOutput())));
		}
		info.add(new TextComponent(ChatFormat.GRAY + StringUtils.t("reborncore.tooltip.energy.tier") + ": "
			+ ChatFormat.GOLD + StringUtils.toFirstCapitalAllLowercase(getTier().toString())));
		if (isRealTile) {
			info.add(new TextComponent(ChatFormat.GRAY + StringUtils.t("reborncore.tooltip.energy.change")
				+ ": " + ChatFormat.GOLD + PowerSystem.getLocaliszedPowerFormatted(getPowerChange()) + "/t"));
		}

		if (hasData) {
			info.add(new TextComponent(ChatFormat.GRAY + StringUtils.t("reborncore.tooltip.energy") + ": "
				+ ChatFormat.GOLD + PowerSystem.getLocaliszedPowerFormatted(energy)));
		}

		super.addInfo(info, isRealTile, hasData);
	}

	// Old cofh stuff, still used to implement Forge Energy, should be removed at somepoint
	@Deprecated
	public boolean canConnectEnergy(Direction from) {
		return canAcceptEnergy(from) || canProvideEnergy(from);
	}

	public int receiveEnergy(Direction from, int maxReceive, boolean simulate) {
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

	public int getEnergyStored(Direction from) {
		return ((int) getEnergy() * RebornCoreConfig.euPerFU);
	}

	public int getMaxEnergyStored(Direction from) {
		return ((int) getMaxPower() * RebornCoreConfig.euPerFU);
	}

	public int extractEnergy(Direction from, int maxExtract, boolean simulate) {
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
