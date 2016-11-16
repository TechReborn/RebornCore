package reborncore.asm.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.asm.IItemStackCompatibility;

/**
 * Created by modmuss50 on 16/11/16.
 */
@Mixin(value = ItemStack.class)
public abstract class MixinItemStack implements IItemStackCompatibility {

	@Shadow
	public int stackSize;

	@Shadow
	private Item item;

	//Get StackSize
	@Override
	public int func_190916_E() {
		return this.stackSize;
	}

	//SetStackSize
	@Override
	public void func_190920_e(int stackSize) {
		this.stackSize = stackSize;
	}

	//IncreaseStackSize
	@Override
	public void func_190917_f(int amount) {
		this.func_190920_e(this.stackSize + amount);
	}

	//DecreaseStackSize
	@Override
	public void func_190918_g(int amount) {
		this.func_190917_f(-amount);
	}

}
