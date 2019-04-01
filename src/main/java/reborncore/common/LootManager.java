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

package reborncore.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import reborncore.RebornCore;
import reborncore.RebornRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class LootManager {
	public static LootManager INSTANCE = new LootManager();

	private LootManager() {}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent evt) {
		InnerPool lp = RebornRegistry.lp;
		for (LootItem item : lp.items) {
			if (item != null) {
				if (item.getLootTableList() == evt.getName()) {
					LootPool main = evt.getTable().getPool("main");
					main.addEntry(new LootEntryItem(item.item.getItem(), (int) item.chance, 1, count(0, item.maxSize), new LootCondition[0], RebornCore.MOD_NAME));
				}
			}
		}
	}

	private static LootFunction[] count(float min, float max) {
		return new LootFunction[] { new SetCount(new LootCondition[0], new RandomValueRange(min, max)) };
	}

	public static LootItem createLootEntry(Item item, double chance, ResourceLocation loottablelist) {
		return new LootItem(new ItemStack(item), chance, 1, 1, loottablelist);
	}

	public static LootItem createLootEntry(Item item, int minStackSize, int maxStackSize, double chance, ResourceLocation loottablelist) {
		return new LootItem(new ItemStack(item, 1), chance, minStackSize, maxStackSize, loottablelist);
	}

	public static class InnerPool extends LootPool {
		public final List<LootItem> items = new ArrayList<LootItem>();

		public InnerPool() {
			super(new LootEntry[0], new LootCondition[0], new RandomValueRange(0, 0), new RandomValueRange(0, 0), RebornCore.MOD_ID);
		}

		public boolean isEmpty() {
			return items.isEmpty();
		}

		public void addItem(LootItem entry) {
			if (entry != null) {
				items.add(entry);
			}
		}

		@Override
		public void generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
			for (LootItem entry : items) {
				if (rand.nextDouble() < entry.chance) {
					ItemStack stack = entry.createStack(rand);
					if (stack != null) {
						//                       System.out.println("LootManager.InnerPool.generateLoot: Added " + stack.getDisplayName() + " " + stack);
						stacks.add(stack);
					}
				}
			}
		}
	}
}
