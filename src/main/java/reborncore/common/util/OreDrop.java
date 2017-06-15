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
	public OreDrop(ItemStack drop, Double modifyer) {
		this.drop = drop;
		this.count = drop.getCount();
		this.baseChance = 100;
		this.modifyer = modifyer;
	}

	public OreDrop(ItemStack drop, double baseChance, Double modifyer) {
		this.drop = drop;
		this.count = drop.getCount();
		this.baseChance = (int) (baseChance * 100);
		this.modifyer = modifyer;
	}

	public ItemStack getDrops(int fortuneLevel, Random random) {
		int count;
		if (baseChance == 100) // This always drops. Use vanilla fortune rules.
		{
			count = calculateFortuneMulti(fortuneLevel, random);
		} else if (calculateFortuneSingle(fortuneLevel, random)) // This has a
		// chance to
		// drop.
		// Increase
		// that
		// chance
		// with
		// fortune.
		{
			count = this.count;
		} else {
			count = 0;
		}
		return new ItemStack(drop.getItem(), count, drop.getItemDamage());
	}

	// Refer to http://minecraft.gamepedia.com/Enchanting#Fortune
	private int calculateFortuneMulti(int level, Random random) {
		int chanceOfEachBonus = 100 / (level + 2);
		int roll = random.nextInt(100);

		if (roll < chanceOfEachBonus * level) // If level = 0, this is always
		// false
		{
			return count * ((roll / chanceOfEachBonus) + 2);
		} else {
			return count;
		}
	}

	// Each fortune level increases probability by 50% (configurable) of base,
	// up to a limit of 100%, obviously.
	// So, if base is 5% and we have Fortune III, chance is 5% + (3 * 2.5%) =
	// 12.5%
	private boolean calculateFortuneSingle(int level, Random random) {
		double modifier = modifyer * level;
		double total = baseChance + (baseChance * modifier);
		int roll = random.nextInt(100);
		return roll <= total;
	}

	public ItemStack drop;
	public Integer baseChance;
	public Integer count;
	public Double modifyer;
}
