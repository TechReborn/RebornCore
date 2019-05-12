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

package reborncore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import reborncore.common.LootManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class RebornRegistry {
	public static LootManager.InnerPool lp = new LootManager.InnerPool();

	public static void registerBlock(Block block, Item.Settings builder, String name) {
		block.setRegistryName(name);
		GameData.register_impl(block);
		BlockItem itemBlock = new BlockItem(block, builder);
		itemBlock.setRegistryName(block.getRegistryName());
		GameData.register_impl(itemBlock);
	}

	public static void registerBlock(Block block, Item.Settings builder, Identifier name) {
		block.setRegistryName(name);
		BlockItem itemBlock = new BlockItem(block, builder);
		itemBlock.setRegistryName(block.getRegistryName());
		GameData.register_impl(itemBlock);
		GameData.register_impl(block);
	}

	public static void registerBlock(Block block, Class<? extends BlockItem> itemclass, String name) {
		GameData.register_impl(block);
		try {
			BlockItem itemBlock = itemclass.getConstructor(Block.class).newInstance(block);
			itemBlock.setRegistryName(name);
			GameData.register_impl(itemBlock);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void registerBlock(Block block, Class<? extends BlockItem> itemclass, Identifier name) {
		block.setRegistryName(name);
		GameData.register_impl(block);
		try {
			BlockItem itemBlock = itemclass.getConstructor(Block.class).newInstance(block);
			itemBlock.setRegistryName(name);
			GameData.register_impl(itemBlock);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void registerBlock(Block block, BlockItem itemBlock, String name) {
		GameData.register_impl(block);
		itemBlock.setRegistryName(name);
		GameData.register_impl(itemBlock);
	}

	public static void registerBlock(Block block, BlockItem itemBlock, Identifier name) {
		block.setRegistryName(name);
		GameData.register_impl(block);
		itemBlock.setRegistryName(name);
		GameData.register_impl(itemBlock);
	}

	public static void registerBlockNoItem(Block block, Identifier name) {
		block.setRegistryName(name);
		GameData.register_impl(block);
	}

	public static void registerBlockNoItem(Block block) {
		GameData.register_impl(block);
	}

	public static void registerBlock(Block block, Item.Settings builder) {
		GameData.register_impl(block);
		BlockItem itemBlock = new BlockItem(block, builder);
		itemBlock.setRegistryName(block.getRegistryName());
		GameData.register_impl(itemBlock);
	}

	public static void registerItem(Item item) {
		GameData.register_impl(item);
	}

	public static void registerItem(Item item, Identifier name) {
		item.setRegistryName(name);
		GameData.register_impl(item);
	}

	//eg: RebornRegistry.addLoot(Items.NETHER_STAR, 0.95, LootTableList.CHESTS_VILLAGE_BLACKSMITH);
	//eg: RebornRegistry.addLoot(Items.DIAMOND, 1.95, LootTableList.ENTITIES_COW);

	public static void addLoot(Item item, double chance, Identifier list) {
		lp.addItem(LootManager.createLootEntry(item, chance, list));
	}

	public static void addLoot(Item item, int minSize, int maxSize, double chance, Identifier list) {
		lp.addItem(LootManager.createLootEntry(item, minSize, maxSize, chance, list));
	}

	@Environment(EnvType.CLIENT)
	public static void registerItemModel(Item i, int meta) {
		Identifier loc = i.getRegistryName();
		//TODO waiting on 1.13 forge
		//ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "inventory"));
	}

	@Environment(EnvType.CLIENT)
	public static void registerItemModel(Block b, int meta) {
		registerItemModel(Item.getItemFromBlock(b), meta);
	}

	@Environment(EnvType.CLIENT)
	public static void registerItemModel(Item i, int meta, String variant) {
		Identifier loc = i.getRegistryName();
		//TODO waiting on 1.13 forge
		//ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "type=" + variant));
	}
}
