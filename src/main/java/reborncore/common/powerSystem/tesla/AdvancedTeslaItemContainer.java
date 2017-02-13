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
