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

package reborncore.client.models;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.state.property.Property;

public class ModelCompound {
	private String inventoryVariant = "inventory";
	private String fileName = "modelregistration.undefinedfilename";
	private String blockstatePath;
	private Property[] ignoreProperties = null;
	private String modid;
	private Block block = null;
	private Item item = null;
	private int meta;

	public ModelCompound(String modid, Block block, int meta, String blockstatePath, Property<?>... ignoreProperties) {
		this.modid = modid;
		this.block = block;
		this.blockstatePath = blockstatePath;
		this.ignoreProperties = ignoreProperties;
		this.meta = meta;
	}

	public ModelCompound(String modid, Block block, int meta, Property<?>... ignoreProperties) {
		this(modid, block, meta, "", ignoreProperties);
	}

	public ModelCompound(String modid, Block block, Property<?>... ignoreProperties) {
		this(modid, block, 0, "", ignoreProperties);
	}

	public ModelCompound(String modid, Block block, String blockstatePath, Property<?>... ignoreProperties) {
		this(modid, block, 0, blockstatePath, ignoreProperties);
	}

	public ModelCompound(String modid, Item item, int meta, String blockstatePath) {
		this.modid = modid;
		this.item = item;
		this.blockstatePath = blockstatePath;
		this.meta = meta;
	}

	public ModelCompound(String modid, Item item, int meta) {
		this(modid, item, meta, "");
	}

	public ModelCompound(String modid, Item item) {
		this(modid, item, 0, "");
	}

	public ModelCompound(String modid, Item item, String blockstatePath) {
		this(modid, item, 0, blockstatePath);
	}

	public String getInventoryVariant() {
		return inventoryVariant;
	}

	public String getFileName() {
		return fileName;
	}

	public ModelCompound setFileName(String name) {
		fileName = name;
		return this;
	}

	public ModelCompound setInvVariant(String variant) {
		inventoryVariant = variant;
		return this;
	}

	public void setBlockStatePath(String blockstatePath) {
		this.blockstatePath = blockstatePath;
	}

	public String getModid() {
		return modid;
	}

	public Block getBlock() {
		return block;
	}

	public Item getItem() {
		if (isBlock()) {
			return Item.getItemFromBlock(block);
		}
		return item;
	}

	public boolean isBlock() {
		if (block != null) {
			return true;
		}
		return false;
	}

	public boolean isItem() {
		if (item != null) {
			return true;
		}
		return false;
	}

	public String getBlockStatePath() {
		return blockstatePath;
	}

	public Property[] getIgnoreProperties() {
		return ignoreProperties;
	}

	public int getMeta() {
		return meta;
	}
}