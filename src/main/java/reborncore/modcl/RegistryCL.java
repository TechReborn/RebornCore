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

package reborncore.modcl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.RebornRegistry;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Prospector
 */
public abstract class RegistryCL {

	public LinkedHashMap<String, ItemCL> itemRegistry = new LinkedHashMap<>();
	public LinkedHashMap<String, BlockCL> blockRegistry = new LinkedHashMap<>();
	public LinkedHashMap<String, BlockContainerCL> blockContainerRegistry = new LinkedHashMap<>();
	public HashMap<ItemStack, String> oreEntries = new HashMap<>();

	protected static void register(ItemCL item) {
		RebornRegistry.registerItem(item);
	}

	protected static void register(BlockCL block) {
		RebornRegistry.registerBlock(block);
	}

	protected static void register(BlockContainerCL block) {
		RebornRegistry.registerBlock(block);
		GameRegistry.registerTileEntity(block.tileEntity, "TilePackagerRD");
	}

	public abstract void init(ModCL mod);
}
