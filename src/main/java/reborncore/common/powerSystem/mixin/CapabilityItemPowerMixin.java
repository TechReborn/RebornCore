package reborncore.common.powerSystem.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import reborncore.common.powerSystem.PoweredItemContainerProvider;
import reborncore.mixin.api.Inject;
import reborncore.mixin.api.Mixin;

import javax.annotation.Nullable;

@Mixin(target = "")
public abstract class CapabilityItemPowerMixin extends Item {

	@Inject
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack,
	                                            @Nullable
		                                            NBTTagCompound nbt) {
		return new PoweredItemContainerProvider(stack);
	}

}
