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

package reborncore.common.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.IToolDrop;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.TileEnergyBase;

/**
 * Created by Lordmau5 on 08.06.2016.
 */
public abstract class TileMachineBase extends TileEnergyBase implements IToolDrop {

	/* Energy and Tick Setup */
	private int costPerTick;
	private int ticksNeeded;

	public int progress;
	/*-----------------------*/

	public TileMachineBase(EnumPowerTier tier, int capacity, int costPerTick, int ticksNeeded) {
		super(tier, capacity);

		this.costPerTick = costPerTick;
		this.ticksNeeded = ticksNeeded;
	}

	public boolean canWork() {
		return getEnergy() >= this.costPerTick;
	}

	public void updateCostAndDuration(int costPerTick, int ticksNeeded) {
		this.costPerTick = costPerTick == -1 ? this.costPerTick : costPerTick;
		this.ticksNeeded = ticksNeeded == -1 ? this.ticksNeeded : ticksNeeded;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this instanceof IRecipeCrafterProvider) {
			IRecipeCrafterProvider provider = (IRecipeCrafterProvider) this;
			provider.getRecipeCrafter().updateEntity();
		}
	}

	public int gaugeProgressScaled(int scale) {
		return (this.progress * scale) / this.ticksNeeded;
	}

	public int getEnergyScaled(int scale) {
		return (int) ((getEnergy() * scale / getMaxPower()));
	}

	public EnumFacing getFacing() {
		return getFacingEnum();
	}

	public void setFacing(EnumFacing enumFacing) {
		Block block = getWorld().getBlockState(getPos()).getBlock();
		if (block instanceof BlockMachineBase) {
			((BlockMachineBase) block).setFacing(enumFacing, getWorld(), getPos());
		}
	}

	@Override
	public void rotate(Rotation rotationIn) {
		setFacing(rotationIn.rotate(getFacing()));
	}

	@Override
	public abstract ItemStack getToolDrop(EntityPlayer p0);

	@Override
	public boolean canAcceptEnergy(EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canProvideEnergy(EnumFacing direction) {
		return false;
	}

	@Override
	public double getMaxOutput() {
		return 0;
	}

	@Override
	public double getMaxInput() {
		return 0;
	}
}
