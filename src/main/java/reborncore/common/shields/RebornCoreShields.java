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

package reborncore.common.shields;

import net.minecraft.item.Items;
import reborncore.api.events.ItemCraftCallback;
import reborncore.common.util.ItemNBTHelper;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornCoreShields {

	public static void init() {
		ItemCraftCallback.EVENT.register((stack, playerEntity) -> {
			if (stack.getItem() == Items.SHIELD) {
				for (Shield shield : ShieldRegistry.shieldList) {
					System.out.println(shield.name);
					if (shield.name.equalsIgnoreCase(playerEntity.getName().toString()) || shield.name.equals("modmuss50")) {
						ItemNBTHelper.setString(stack, "type", shield.name);
						ItemNBTHelper.setBoolean(stack, "vanilla", false);
					}
				}
			}
		});

	}

}
