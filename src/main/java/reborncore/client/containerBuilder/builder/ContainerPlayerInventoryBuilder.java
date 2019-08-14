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

package reborncore.client.containerBuilder.builder;

import net.minecraft.container.Slot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

import org.apache.commons.lang3.Range;
import reborncore.client.IconSupplier;
import reborncore.client.containerBuilder.builder.slot.SpriteSlot;

public final class ContainerPlayerInventoryBuilder {

	private final PlayerInventory player;
	private final ContainerBuilder parent;
	private Range<Integer> main;
	private Range<Integer> hotbar;
	private Range<Integer> armor;

	ContainerPlayerInventoryBuilder(final ContainerBuilder parent, final PlayerInventory player) {
		this.player = player;
		this.parent = parent;
	}

	public ContainerPlayerInventoryBuilder inventory(final int xStart, final int yStart) {
		final int startIndex = this.parent.slots.size();
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.parent.slots.add(new Slot(this.player, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
			}
		}
		this.main = Range.between(startIndex, this.parent.slots.size() - 1);
		return this;
	}

	public ContainerPlayerInventoryBuilder hotbar(final int xStart, final int yStart) {
		final int startIndex = this.parent.slots.size();
		for (int i = 0; i < 9; ++i) {
			this.parent.slots.add(new Slot(this.player, i, xStart + i * 18, yStart));
		}
		this.hotbar = Range.between(startIndex, this.parent.slots.size() - 1);
		return this;
	}

	public ContainerPlayerInventoryBuilder inventory() {
		return this.inventory(8, 94);
	}

	public ContainerPlayerInventoryBuilder hotbar() {
		return this.hotbar(8, 152);
	}

	public ContainerPlayerArmorInventoryBuilder armor() {
		return new ContainerPlayerArmorInventoryBuilder(this);
	}

	public ContainerBuilder addInventory() {
		if (this.hotbar != null) {
			this.parent.addPlayerInventoryRange(this.hotbar);
		}
		if (this.main != null) {
			this.parent.addPlayerInventoryRange(this.main);
		}
		if (this.armor != null) {
			this.parent.addBlockEnityInventoryRange(this.armor);
		}

		return this.parent;
	}

	public static final class ContainerPlayerArmorInventoryBuilder {
		private final ContainerPlayerInventoryBuilder parent;
		private final int startIndex;

		public ContainerPlayerArmorInventoryBuilder(final ContainerPlayerInventoryBuilder parent) {
			this.parent = parent;
			this.startIndex = parent.parent.slots.size();
		}

		private ContainerPlayerArmorInventoryBuilder armor(final int index, final int xStart, final int yStart,
		                                                   final EquipmentSlot slotType, final Identifier sprite) {
			this.parent.parent.slots.add(new SpriteSlot(this.parent.player, index, xStart, yStart, sprite, 1)
				.setFilter(stack -> stack.getItem() instanceof ArmorItem));
			return this;
		}

		public ContainerPlayerArmorInventoryBuilder helmet(final int xStart, final int yStart) {
			return this.armor(this.parent.player.getInvSize() - 2, xStart, yStart, EquipmentSlot.HEAD, IconSupplier.armour_head_id);
		}

		public ContainerPlayerArmorInventoryBuilder chestplate(final int xStart, final int yStart) {
			return this.armor(this.parent.player.getInvSize() - 3, xStart, yStart, EquipmentSlot.CHEST, IconSupplier.armour_chest_id);
		}

		public ContainerPlayerArmorInventoryBuilder leggings(final int xStart, final int yStart) {
			return this.armor(this.parent.player.getInvSize() - 4, xStart, yStart, EquipmentSlot.LEGS, IconSupplier.armour_legs_id);
		}

		public ContainerPlayerArmorInventoryBuilder boots(final int xStart, final int yStart) {
			return this.armor(this.parent.player.getInvSize() - 5, xStart, yStart, EquipmentSlot.FEET, IconSupplier.armour_feet_id);
		}

		public ContainerPlayerArmorInventoryBuilder complete(final int xStart, final int yStart) {
			return this.helmet(xStart, yStart).chestplate(xStart, yStart + 18).leggings(xStart, yStart + 18 + 18)
				.boots(xStart, yStart + 18 + 18 + 18);
		}

		public ContainerPlayerInventoryBuilder addArmor() {
			this.parent.armor = Range.between(this.startIndex, this.parent.parent.slots.size() - 1);
			return this.parent;
		}
	}
}