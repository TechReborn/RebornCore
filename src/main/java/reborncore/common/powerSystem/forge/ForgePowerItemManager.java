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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;

public class ForgePowerItemManager implements IEnergyStorage, IEnergyStorageModifiable {

	ItemStack stack;
	IEnergyItemInfo itemPowerInfo;

	public ForgePowerItemManager(ItemStack stack) {
		this.stack = stack;
		if (stack.getItem() instanceof IEnergyItemInfo) {
			this.itemPowerInfo = (IEnergyItemInfo) stack.getItem();
		}
		validateNBT();
	}

	public ForgePowerItemManager(ItemStack stack, IEnergyItemInfo itemPowerInfo) {
		this.stack = stack;
		this.itemPowerInfo = itemPowerInfo;
		validateNBT();
	}

	private int getEnergyInStack(){
		validateNBT();
		return stack.getTagCompound().getInteger("energy");
	}

	private void setEnergyInStack(int energy){
		validateNBT();
		stack.getTagCompound().setInteger("energy", energy);
	}

	//Checks to ensure that the item has a nbt tag, and upgrades old items to the new format
	private void validateNBT(){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("energy", 0);
		} else {
			if(stack.getTagCompound().hasKey("charge")){
				//Upgrades the item from the old format to the new format
				stack.getTagCompound().setInteger("energy", stack.getTagCompound().getInteger("charge") * RebornCoreConfig.euPerFU);
				stack.getTagCompound().removeTag("charge");
			}
		}
	}

	public void setEnergyStored(int value) {
		setEnergyInStack(value);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!itemPowerInfo.canAcceptEnergy(stack)) {
			return 0;
		}
		int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(),
				Math.min((int) itemPowerInfo.getMaxTransfer(stack), maxReceive));

		if (!simulate) {
			setEnergyInStack(getEnergyInStack() + energyReceived);
		}
		return energyReceived;
	} 

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!itemPowerInfo.canAcceptEnergy(stack)) {
			return 0;
		}
		int energyExtracted = Math.min(getEnergyStored(), maxExtract);
		if (!simulate) {
			setEnergyInStack(getEnergyInStack() - energyExtracted);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		return getEnergyInStack();
	}

	@Override
	public int getMaxEnergyStored() {
		return (int) itemPowerInfo.getMaxPower(stack);
	}

	@Override
	public boolean canExtract() {
		return itemPowerInfo.canProvideEnergy(stack);
	}

	@Override
	public boolean canReceive() {
		return itemPowerInfo.canAcceptEnergy(stack);
	}

	@Override
	public void setEnergy(int energy) {
		setEnergyInStack(energy);
	}
}
