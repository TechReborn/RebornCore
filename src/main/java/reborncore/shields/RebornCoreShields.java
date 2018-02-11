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

package reborncore.shields;

import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import reborncore.common.util.ItemNBTHelper;
import reborncore.shields.api.Shield;
import reborncore.shields.api.ShieldRegistry;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornCoreShields {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new RebornCoreShields());
	}

	@SubscribeEvent
	public void craft(PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting.getItem() == Items.SHIELD) {
			for (Shield shield : ShieldRegistry.shieldList) {
				if (shield.name.equalsIgnoreCase(event.player.getName())) {
					ItemNBTHelper.setString(event.crafting, "type", shield.name);
					ItemNBTHelper.setBoolean(event.crafting, "vanilla", false);
				}
			}
		}
	}

}
