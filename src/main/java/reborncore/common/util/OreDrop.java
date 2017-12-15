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

package reborncore.common.util;

import net.minecraft.item.ItemStack;

import java.util.Random;

public class OreDrop {

	public ItemStack drop;
	public Integer baseChance;
	public Integer minQuantity;
	public int maxQuantity;

	/**
	 * @param drop ItemStack to drop. Increase stack size to always drop more than one item
	 * @param maxQuantity int Drop modifier, if drop ItemStack size could be more than 1
	 */
	public OreDrop(ItemStack drop, int maxQuantity) {
		this.drop = drop;
		this.minQuantity = drop.getCount();
		if (this.minQuantity == 0) {
			this.minQuantity = 1;
		}
		this.baseChance = 100;
		this.maxQuantity = maxQuantity;
	}

	/**
	 * @param drop ItemStack to drop
	 * @param baseChance double Chance to drop that item
	 * @param maxQuantity int Drop modifier, if drop ItemStack size could be more than 1
	 */
	public OreDrop(ItemStack drop, double baseChance, int maxQuantity) {
		this.drop = drop;
		this.minQuantity = drop.getCount();
		if (this.minQuantity == 0) {
			this.minQuantity = 1;
		}
		this.baseChance = (int) (baseChance * 100);
		this.maxQuantity = maxQuantity;
	}

	public ItemStack getDrops(int fortuneLevel, Random random) {
		int count;
		
		// This always drops. Increase drop amount with fortune
		if (baseChance == 100) {
			count = calculateFortuneMulti(fortuneLevel, random);
		} 
		// This has a chance to drop. Increase that chance with fortune.
		else if (calculateFortuneSingle(fortuneLevel, random)) {
			count = this.calculateBaseQuantity(random);
		} else {
			count = 0;
		}
		return new ItemStack(drop.getItem(), count, drop.getItemDamage());
	}

	// Refer to http://minecraft.gamepedia.com/Enchanting#Fortune
	// Fortune I has 33% chance to multiply drop by 2
	// Fortune II has 25% probability to multiply drop by 2 and 25% probability to multiply drop by 3
	// Fortune III has 20% probability to multiply drop by 2 or 3 or 4
	private int calculateFortuneMulti(int fortune, Random random) {
		int chanceOfEachBonus = 100 / (fortune + 2);
		int roll = random.nextInt(100);

		// If level = 0, this is always false
		if (roll < chanceOfEachBonus * fortune) {
			return this.calculateBaseQuantity(random) * ((roll / chanceOfEachBonus) + 2);
		} else {
			return this.calculateBaseQuantity(random);
		}
	}

	// Each fortune level increases probability by 50% (configurable) of base,
	// up to a limit of 100%, obviously.
	// So, if base is 5% and we have Fortune III, chance is 5% + (3 * 2.5%) =
	// 12.5%
	private boolean calculateFortuneSingle(int fortune, Random random) {
		double total = baseChance + (fortune * baseChance / 2);
		int roll = random.nextInt(100);
		return roll <= total;
	}
	
	private int calculateBaseQuantity(Random random) {
		return this.minQuantity + random.nextInt(this.maxQuantity - this.minQuantity + 1);
	}

}
