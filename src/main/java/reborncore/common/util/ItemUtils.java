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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import reborncore.common.recipes.IRecipeInput;
import team.reborn.energy.Energy;

import java.util.List;

/**
 * Created by mark on 12/04/15.
 */
public class ItemUtils {

	public static boolean isItemEqual(final ItemStack a, final ItemStack b,
	                                  final boolean matchNBT) {
		if (a.isEmpty() || b.isEmpty()) {
			return false;
		}
		if (a.getItem() != b.getItem()) {
			return false;
		}
		return !matchNBT || ItemStack.areTagsEqual(a, b);
	}

	public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchNBT,
	                                  boolean useTags) {
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		if (isItemEqual(a, b, matchNBT)) {
			return true;
		}
		if (a.isEmpty() || b.isEmpty()) {
			return false;
		}
		if (useTags) {

			//TODO tags
		}
		return false;
	}

	//TODO tags
	public static boolean isInputEqual(Object input, ItemStack other, boolean matchNBT,
	                                   boolean useTags) {
		if (input instanceof ItemStack) {
			return isItemEqual((ItemStack) input, other, matchNBT, useTags);
		} else if (input instanceof String) {
			//TODO tags
		} else if (input instanceof IRecipeInput) {
			List<ItemStack> inputs = ((IRecipeInput) input).getAllStacks();
			for (ItemStack stack : inputs) {
				if (isItemEqual(stack, other, matchNBT, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void writeItemToNBT(ItemStack stack, CompoundTag data) {
		if (stack.isEmpty() || stack.getCount() <= 0) {
			return;
		}
		if (stack.getCount() > 127) {
			stack.setCount(127);
		}
		stack.toTag(data);
	}

	public static ItemStack readItemFromNBT(CompoundTag data) {
		return ItemStack.fromTag(data);
	}

	public static double getPowerForDurabilityBar(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0.0;
		}

		if (!Energy.valid(stack)) {
			return 0.0;
		}

		return Energy.of(stack).getEnergy() / Energy.of(stack).getMaxStored();
	}

	/**
	 * Checks if powered item is active
	 *
	 * @param stack ItemStack ItemStack to check
	 * @return True if powered item is active
	 */
	public static boolean isActive(ItemStack stack) {
		return !stack.isEmpty() && stack.getTag() != null && stack.getTag().getBoolean("isActive");
	}

	/**
	 * Check if powered item has enough energy to continue being in active state
	 *
	 * @param stack ItemStack ItemStack to check
	 * @param cost int Cost of operation performed by tool
	 * @param isClient boolean Client side
	 * @param messageId int MessageID for sending no spam message
	 */
	public static void checkActive(ItemStack stack, int cost, boolean isClient, int messageId) {
		if (!ItemUtils.isActive(stack)) {
			return;
		}
		if (Energy.of(stack).getEnergy() >= cost) {
			return;
		}
		if (isClient) {
			ChatUtils.sendNoSpamMessages(messageId, new LiteralText(
					Formatting.GRAY + StringUtils.t("reborncore.message.energyError") + " "
							+ Formatting.GOLD + StringUtils.t("reborncore.message.deactivating")));
		}
		stack.getOrCreateTag().putBoolean("isActive", false);
	}

	/**
	 * Switch active\inactive state for powered item
	 *
	 * @param stack ItemStack ItemStack to work on
	 * @param cost int Cost of operation performed by tool
	 * @param isClient boolean Are we on client side
	 * @param messageId MessageID for sending no spam message
	 */
	public static void switchActive(ItemStack stack, int cost, boolean isClient, int messageId){
		ItemUtils.checkActive(stack, cost, isClient, messageId);

		if (!ItemUtils.isActive(stack)) {
			stack.getOrCreateTag().putBoolean("isActive", true);
			if (isClient) {
				ChatUtils.sendNoSpamMessages(messageId, new LiteralText(
						Formatting.GRAY + StringUtils.t("reborncore.message.setTo") + " "
								+ Formatting.GOLD + StringUtils.t("reborncore.message.active")));
			}
		} else {
			stack.getOrCreateTag().putBoolean("isActive", false);
			if (isClient) {
				ChatUtils.sendNoSpamMessages(messageId, new LiteralText(
						Formatting.GRAY + StringUtils.t("reborncore.message.setTo") + " "
								+ Formatting.GOLD + StringUtils.t("reborncore.message.inactive")));
			}
		}
	}

	/**
	 * Adds active\inactive state to powered item tooltip
	 *
	 * @param stack ItemStack ItemStack to check
	 * @param tooltip List Tooltip strings
	 */
	public static void buildActiveTooltip(ItemStack stack, List<Text> tooltip){
		if (!ItemUtils.isActive(stack)) {
			tooltip.add(new TranslatableText("reborncore.message.inactive").formatted(Formatting.RED));
		} else {
			tooltip.add(new TranslatableText("reborncore.message.active").formatted(Formatting.GREEN));
		}
	}
}
