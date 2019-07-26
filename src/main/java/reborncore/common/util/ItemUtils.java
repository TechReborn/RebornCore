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

package reborncore.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import reborncore.common.recipes.IRecipeInput;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mark, estebes
 */
public class ItemUtils {

	public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage,
	                                  final boolean matchNBT) {
		if (a.isEmpty() || b.isEmpty())
			return false;
		if (a.getItem() != b.getItem())
			return false;
		if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b))
			return false;
		if (matchDamage && a.getHasSubtypes()) {
			if (isWildcard(a) || isWildcard(b))
				return true;
			if (a.getItemDamage() != b.getItemDamage())
				return false;
		}
		return true;
	}

	public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchDamage, boolean matchNBT,
	                                  boolean useOreDic) {
		if (isItemEqual(a, b, matchDamage, matchNBT)) {
			return true;
		}
		if (a.isEmpty() || b.isEmpty())
			return false;
		if (useOreDic) {
			for (int inta : OreDictionary.getOreIDs(a)) {
				for (int intb : OreDictionary.getOreIDs(b)) {
					if (inta == intb) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isInputEqual(Object input, ItemStack other, boolean matchDamage, boolean matchNBT,
	                                   boolean useOreDic) {
		if (input instanceof ItemStack) {
			return isItemEqual((ItemStack) input, other, matchDamage, matchNBT, useOreDic);
		} else if (input instanceof String) {
			NonNullList<ItemStack> ores = OreDictionary.getOres((String) input);
			for (ItemStack stack : ores) {
				if (isItemEqual(stack, other, matchDamage, matchNBT, false)) {
					return true;
				}
			}
		} else if (input instanceof IRecipeInput){
			List<ItemStack> inputs = ((IRecipeInput) input).getAllStacks();
			for (ItemStack stack : inputs) {
				if (isItemEqual(stack, other, matchDamage, matchNBT, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isWildcard(ItemStack stack) {
		return isWildcard(stack.getItemDamage());
	}

	public static boolean isWildcard(int damage) {
		return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
	}

	public static void writeInvToNBT(IInventory inv, String tag, NBTTagCompound data) {
		NBTTagList list = new NBTTagList();
		for (byte slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (!stack.isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", slot);
				writeItemToNBT(stack, itemTag);
				list.appendTag(itemTag);
			}
		}
		data.setTag(tag, list);
	}

	public static void readInvFromNBT(IInventory inv, String tag, NBTTagCompound data) {
		NBTTagList list = data.getTagList(tag, 10);
		for (byte entry = 0; entry < list.tagCount(); entry++) {
			NBTTagCompound itemTag = list.getCompoundTagAt(entry);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSizeInventory()) {
				ItemStack stack = readItemFromNBT(itemTag);
				inv.setInventorySlotContents(slot, stack);
			}
		}
	}

	public static List<ItemStack> getStackWithAllOre(ItemStack stack) {
		if (stack.isEmpty()) {
			return new ArrayList<ItemStack>();
		}
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (int oreID : OreDictionary.getOreIDs(stack)) {
			for (ItemStack ore : OreDictionary.getOres(OreDictionary.getOreName(oreID))) {
				ItemStack newOre = ore.copy();
				newOre.setCount(stack.getCount());
				list.add(newOre);
			}
		}
		if (list.isEmpty()) {
			list.add(stack);
		}
		return list;
	}

	/**
	 * Write an {@link ItemStack} from {@link NBTTagCompound}
	 *
	 * @param data the NBT data
	 * @return the {@link ItemStack} represented by the NBT data
	 */
	public static ItemStack readItemFromNBT(NBTTagCompound data) {
		return new ItemStack(data);
	}

	/**
	 * Write an {@link ItemStack} to {@link NBTTagCompound}
	 *
	 * @param stack the itemstack
	 * @param data the {@link NBTTagCompound} representing the stack
	 */
	public static void writeItemToNBT(ItemStack stack, NBTTagCompound data) {
		if (isEmpty(stack)) return;

		if (stack.getCount() > 127) stack.setCount(127);

		stack.writeToNBT(data);
	}
	/**
	 * Get a value between 0 and 1 that represent the current power to be shown as durability.
	 *
	 * @param stack the itemstack
	 * @return the current power to be shown as durability
	 */
	public static double getPowerForDurabilityBar(ItemStack stack) {
		if (isEmpty(stack)) return 0.0;

		if (!(stack.getItem() instanceof IEnergyItemInfo) ) return 0.0;

		IEnergyStorage capEnergy = new ForgePowerItemManager(stack);

		return (double) capEnergy.getEnergyStored() / (double) capEnergy.getMaxEnergyStored();
	}

	/**
	 * Check if a stack is has the active modifier is active
	 *
	 * @param stack the itemstack
	 * @return the stack active modifier status
	 */
	public static boolean isActive(ItemStack stack) {
		if (!isEmpty(stack)) {
			NBTTagCompound nbt = getStackNbtData(stack);
			if (nbt.hasKey("isActive")) return nbt.getBoolean("isActive");
		}

		return false;
	}

	/**
	 * Get or create an ItemStack NBT data
	 *
	 * @param stack the itemstack
	 * @return the existing NBT or a new one if it does not exist
	 */
	public static NBTTagCompound getStackNbtData(ItemStack stack) {
		NBTTagCompound ret = stack.getTagCompound();

		if (ret == null) { // does not exist so we need to create it
			ret = new NBTTagCompound();
			stack.setTagCompound(ret);
		}

		return ret;
	}

	/**
	 * Check if a stack is empty
	 *
	 * @param stack the itemstack
	 * @return true if the stack is empty or false otherwise
	 */
	public static boolean isEmpty(ItemStack stack) {
		return stack == ItemStack.EMPTY || stack == null || stack.getCount() <= 0;
	}

	/**
	 * Get the size of a stack
	 *
	 * @param stack the itemstack
	 * @return the size of the stack
	 */
	public static int getSize(ItemStack stack) {
		return isEmpty(stack) ? 0 : stack.getCount();
	}

	/**
	 * Set the size of a stack
	 *
	 * @param stack the itemstack
	 * @param size the new size
	 * @return the resulting stack
	 */
	public static ItemStack setSize(ItemStack stack, int size) {
		if (size <= 0) return ItemStack.EMPTY;

		stack.setCount(size);

		return stack;
	}

	/**
	 * Increase the size of a stack
	 *
	 * @param stack the itemstack
	 * @param amount amount to be increased by
	 * @return the resulting stack
	 */
	public static ItemStack increaseSize(ItemStack stack, int amount) {
		return setSize(stack, getSize(stack) + amount);
	}

	/**
	 * Increase the size of a stack
	 *
	 * @param stack the itemstack
	 * @return the resulting stack
	 */
	public static ItemStack increaseSize(ItemStack stack) {
		return increaseSize(stack, 1);
	}

	/**
	 * Decrease the size of a stack
	 *
	 * @param stack the itemstack
	 * @param amount amount to be decreased by
	 * @return the resulting stack
	 */
	public static ItemStack decreaseSize(ItemStack stack, int amount) {
		return setSize(stack, getSize(stack) - amount);
	}

	/**
	 * Decrease the size of a stack
	 *
	 * @param stack the itemstack
	 * @return the resulting stack
	 */
	public static ItemStack decreaseSize(ItemStack stack) {
		return decreaseSize(stack, 1);
	}

	/**
	 * Drop a stack to the world
	 *
	 * @param world the world
	 * @param pos the position
	 * @param stack the itemstack
	 */
	public static void dropToWorld(World world, BlockPos pos, ItemStack stack) {
		if (isEmpty(stack)) return;

		double f = 0.7;
		double dx = world.rand.nextFloat() * f + (1 - f) * 0.5;
		double dy = world.rand.nextFloat() * f + (1 - f) * 0.5;
		double dz = world.rand.nextFloat() * f + (1 - f) * 0.5;

		EntityItem entityItem = new EntityItem(world, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, stack.copy());
		entityItem.setDefaultPickupDelay();
		world.spawnEntity(entityItem);
	}

	/**
	 * Simple util to decently represent an ItemStack with a string
	 *
	 * @param itemStack the ItemStack to be represent
	 * @return a formatted string representing the ItemStack
	 */
	public static String toStringSafe(ItemStack itemStack) {
		return (itemStack == null) ? "(null)" : (itemStack.getItem() == null) ?
		                                    getSize(itemStack)+"x(null)@(unknown)" : itemStack.toString();
	}
}
