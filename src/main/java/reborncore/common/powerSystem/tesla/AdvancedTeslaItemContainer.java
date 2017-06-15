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

package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PoweredItem;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class AdvancedTeslaItemContainer implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

	ItemStack stack;
	IEnergyItemInfo itemPowerInfo;

	public AdvancedTeslaItemContainer(ItemStack stack) {
		this.stack = stack;
		if (stack.getItem() instanceof IEnergyItemInfo) {
			itemPowerInfo = (IEnergyItemInfo) stack.getItem();
		}
	}

	public long getStoredPower() {
		return (long) PoweredItem.getEnergy(stack) * RebornCoreConfig.euPerFU;
	}

	//Receive
	public long givePower(long tesla, boolean simulated) {
		return (long) PoweredItem.addEnergy(tesla * RebornCoreConfig.euPerFU, stack);
	}

	//Take power out
	public long takePower(long tesla, boolean simulated) {
		return (int) PoweredItem.useEnergy(tesla * RebornCoreConfig.euPerFU, stack);
	}

	public long getCapacity() {
		return (long) PoweredItem.getMaxPower(stack) * RebornCoreConfig.euPerFU;
	}

	public long getInputRate() {
		return (long) itemPowerInfo.getMaxTransfer(stack) * RebornCoreConfig.euPerFU;
	}

	public long getOutputRate() {
		return (long) itemPowerInfo.getMaxTransfer(stack) * RebornCoreConfig.euPerFU;
	}

}
