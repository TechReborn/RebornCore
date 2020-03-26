/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.common.powerSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import reborncore.api.IListInfoProvider;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blockentity.RedstoneConfiguration;
import reborncore.common.util.StringUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

import javax.annotation.Nullable;
import java.util.List;

public abstract class PowerAcceptorBlockEntity extends MachineBaseBlockEntity implements EnergyStorage, IListInfoProvider // TechReborn
{
	private EnergyTier blockEntityPowerTier;
	private double energy;

	public double extraPowerStorage;
	public double extraPowerInput;
	public int extraTier;
	public double powerChange;
	public double powerLastTick;
	public boolean checkOverfill = true; // Set to false to disable the overfill check.
	// Some external power systems (EU) support multiple energy packets per tick,
	// this allows machines to possibly emit
	// multiple packets in a tick. Other power systems such as FE will ignore this
	// option.
	public int maxPacketsPerTick = 1;

	public PowerAcceptorBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
		checkTier();
	}

	public void checkTier() {
		if (this.getMaxInput(EnergySide.UNKNOWN) == 0) {
			blockEntityPowerTier = getTier((int) this.getBaseMaxOutput());
		} else {
			blockEntityPowerTier = getTier((int) this.getBaseMaxInput());
		}

	}

	public static EnergyTier getTier(int power) {
		for (EnergyTier tier : EnergyTier.values()) {
			if (tier.getMaxInput() >= power) {
				return tier;
			}
		}
		return EnergyTier.INFINITE;
	}

	public void setExtraPowerStorage(double extraPowerStorage) {
		this.extraPowerStorage = extraPowerStorage;
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

		double chargeEnergy = Math.min(getFreeSpace(), getMaxInput(EnergySide.UNKNOWN));
		if (chargeEnergy <= 0.0) {
			return;
		}
		if (!getOptionalInventory().isPresent()) {
			return;
		}
		ItemStack batteryStack = getOptionalInventory().get().getInvStack(slot);
		if (batteryStack.isEmpty()) {
			return;
		}

		if (Energy.valid(batteryStack)) {
			Energy.of(batteryStack)
				.into(
					Energy
						.of(this)
				)
				.move();
		}

	}

	/**
	 * Charge battery from machine placed inside inventory slot
	 *
	 * @param slot int Slot ID for battery slot
	 */
	public void discharge(int slot) {
		if (world.isClient) {
			return;
		}

		ItemStack batteryStack = getOptionalInventory().get().getInvStack(slot);
		if(batteryStack.isEmpty()){
			return;
		}

		if(Energy.valid(batteryStack)){
			Energy.of(this).into(Energy.of(batteryStack)).move();
		}
	}

	public int getEnergyScaled(int scale) {
		return (int) ((getEnergy() * scale / getMaxPower()));
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

		if (getEnergy() > 0 && isActive(RedstoneConfiguration.POWER_IO)) { // Tesla or IC2 should handle this if enabled, so only do this without tesla
			for (Direction side : Direction.values()) {
				BlockEntity blockEntity = getWorld().getBlockEntity(getPos().offset(side));
				if(blockEntity == null || !Energy.valid(blockEntity)){
					continue;
				}
				Energy.of(this)
					.side(side)
					.into(
						Energy.of(blockEntity).side(side.getOpposite())
					)
					.move();
			}
		}

		powerChange = getEnergy() - powerLastTick;
		powerLastTick = getEnergy();
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		CompoundTag data = tag.getCompound("PowerAcceptor");
		if (shouldHanldeEnergyNBT()) {
			this.setEnergy(data.getDouble("energy"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		CompoundTag data = new CompoundTag();
		data.putDouble("energy", getEnergy());
		tag.put("PowerAcceptor", data);
		return tag;
	}

	@Override
	public void resetUpgrades() {
		super.resetUpgrades();
		extraPowerStorage = 0;
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

	public double getEnergy() {
		return getStored(EnergySide.UNKNOWN);
	}

	@Override
	public double getStored(EnergySide face) {
		return energy;
	}

	@Override
	public void setStored(double amount) {
		this.energy = amount;
		if(checkOverfill){
			this.energy = Math.max(Math.min(energy, getMaxPower()), 0);
		}
		markDirty();
	}

	public void setEnergy(double energy) {
		setStored(energy);
	}

	public double addEnergy(double energy) {
		return addEnergy(energy, false);
	}

	public double addEnergy(double energy, boolean simulate) {
		double energyReceived = Math.min(getMaxPower(), Math.min(getFreeSpace(), energy));

		if (!simulate) {
			setEnergy(getEnergy() + energyReceived);
		}
		return energyReceived;
	}

	public boolean canUseEnergy(double input) {
		return input <= energy;
	}

	public double useEnergy(double energy) {
		return useEnergy(energy, false);
	}

	public double useEnergy(double extract, boolean simulate) {
		if (extract > energy) {
			extract = energy;
		}
		if (!simulate) {
			setEnergy(energy - extract);
		}
		return extract;
	}

	public boolean canAddEnergy(double energyIn) {
		return getEnergy() + energyIn <= getMaxPower();
	}

	public double getMaxPower() {
		return getBaseMaxPower() + extraPowerStorage;
	}

	@Override
	public double getMaxStoredPower() {
		return getMaxPower();
	}

	public boolean canAcceptEnergy(Direction direction) {
		return true;
	}

	public boolean canProvideEnergy(Direction direction) {
		return true;
	}

	@Override
	public double getMaxOutput(EnergySide face) {
		if (!isActive(RedstoneConfiguration.POWER_IO)) {
			return 0;
		}
		if(!canProvideEnergy(fromSide(face))) {
			return 0;
		}
		double maxOutput = 0;
		if (this.extraTier > 0) {
			maxOutput = this.getTier().getMaxOutput();
		} else {
			maxOutput = getBaseMaxOutput();
		}
		return maxOutput;
	}

	@Override
	public double getMaxInput(EnergySide face) {
		if (!isActive(RedstoneConfiguration.POWER_IO)) {
			return 0;
		}
		if(!canAcceptEnergy(fromSide(face))) {
			return 0;
		}
		double maxInput = 0;
		if (this.extraTier > 0) {
			maxInput = this.getTier().getMaxInput();
		} else {
			maxInput = getBaseMaxInput();
		}
		return maxInput + extraPowerInput;
	}

	public static Direction fromSide(EnergySide side){
		if(side == EnergySide.UNKNOWN){
			return null;
		}
		return Direction.values()[side.ordinal()];
	}

	public EnergyTier getPushingTier() {
		return getTier();
	}

	@Override
	public EnergyTier getTier() {
		if (blockEntityPowerTier == null) {
			checkTier();
		}

		if (extraTier > 0) {
			for (EnergyTier enumTier : EnergyTier.values()) {
				if (enumTier.ordinal() == blockEntityPowerTier.ordinal() + extraTier) {
					return blockEntityPowerTier;
				}
			}
			return EnergyTier.INFINITE;
		}
		return blockEntityPowerTier;
	}

	// IListInfoProvider
	@Override
	public void addInfo(List<Text> info, boolean isReal, boolean hasData) {
		info.add(new LiteralText(Formatting.GRAY + StringUtils.t("reborncore.tooltip.energy.maxEnergy") + ": "
				+ Formatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxPower())));
		if (getMaxInput(EnergySide.UNKNOWN) != 0) {
			info.add(new LiteralText(Formatting.GRAY + StringUtils.t("reborncore.tooltip.energy.inputRate") + ": "
					+ Formatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxInput(EnergySide.UNKNOWN))));
		}
		if (getMaxOutput(EnergySide.UNKNOWN) <= 0) {
			info.add(new LiteralText(Formatting.GRAY + StringUtils.t("reborncore.tooltip.energy.outputRate") + ": "
					+ Formatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getMaxOutput(EnergySide.UNKNOWN))));
		}
		info.add(new LiteralText(Formatting.GRAY + StringUtils.t("reborncore.tooltip.energy.tier") + ": "
				+ Formatting.GOLD + StringUtils.toFirstCapitalAllLowercase(getTier().toString())));
		if (isReal) {
			info.add(new LiteralText(Formatting.GRAY + StringUtils.t("reborncore.tooltip.energy.change") + ": "
					+ Formatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(getPowerChange()) + "/t"));
		}

		if (hasData) {
			info.add(new LiteralText(Formatting.GRAY + StringUtils.t("reborncore.tooltip.energy") + ": "
					+ Formatting.GOLD + PowerSystem.getLocaliszedPowerFormatted(energy)));
		}

		super.addInfo(info, isReal, hasData);
	}

	/**
	 * Calculates the comparator output of a powered BE with the formula
	 * {@code ceil(blockEntity.getEnergy() * 15.0 / storage.getMaxPower())}.
	 *
	 * @param blockEntity the powered BE
	 * @return the calculated comparator output or 0 if {@code blockEntity} is not a {@code PowerAcceptorBlockEntity}
	 */
	public static int calculateComparatorOutputFromEnergy(@Nullable BlockEntity blockEntity) {
		if (blockEntity instanceof PowerAcceptorBlockEntity) {
			PowerAcceptorBlockEntity storage = (PowerAcceptorBlockEntity) blockEntity;
			return MathHelper.ceil(storage.getEnergy() * 15.0 / storage.getMaxPower());
		} else {
			return 0;
		}
	}

}
