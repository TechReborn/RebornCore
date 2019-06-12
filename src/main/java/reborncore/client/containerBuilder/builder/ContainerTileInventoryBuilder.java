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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.RebornCore;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.containerBuilder.builder.slot.FilteredSlot;
import reborncore.client.containerBuilder.builder.slot.UpgradeSlot;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;
import reborncore.client.gui.slots.SlotOutput;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;
import reborncore.common.util.Inventory;

import java.util.function.*;

public class ContainerTileInventoryBuilder {

	private final Inventory itemHandler;
	private final BlockEntity tile;
	private final ContainerBuilder parent;
	private final int rangeStart;

	ContainerTileInventoryBuilder(final ContainerBuilder parent, final BlockEntity tile) {
		this.itemHandler =  null; //TODO get the inv from the tile
		this.tile = tile;
		this.parent = parent;
		this.rangeStart = parent.slots.size();
		if (itemHandler instanceof IUpgradeable) {
			upgradeSlots((IUpgradeable) itemHandler);
		}
	}

	public ContainerTileInventoryBuilder slot(final int index, final int x, final int y) {
		this.parent.slots.add(new BaseSlot(this.itemHandler, index, x, y));
		return this;
	}

	public ContainerTileInventoryBuilder slot(final int index, final int x, final int y, Predicate<ItemStack> filter) {
		this.parent.slots.add(new BaseSlot(this.itemHandler, index, x, y, filter));
		return this;
	}

	public ContainerTileInventoryBuilder outputSlot(final int index, final int x, final int y) {
		this.parent.slots.add(new SlotOutput(this.itemHandler, index, x, y));
		return this;
	}

	public ContainerTileInventoryBuilder fakeSlot(final int index, final int x, final int y) {
		this.parent.slots.add(new SlotFake(this.itemHandler, index, x, y, false, false, Integer.MAX_VALUE));
		return this;
	}

	public ContainerTileInventoryBuilder filterSlot(final int index, final int x, final int y,
	                                                final Predicate<ItemStack> filter) {
		this.parent.slots.add(new FilteredSlot(this.itemHandler, index, x, y).setFilter(filter));
		return this;
	}

	public ContainerTileInventoryBuilder energySlot(final int index, final int x, final int y) {
		this.parent.slots.add(new FilteredSlot(this.itemHandler, index, x, y)
			.setFilter(ExternalPowerSystems::isPoweredItem));
		return this;
	}

	public ContainerTileInventoryBuilder fluidSlot(final int index, final int x, final int y) {
		this.parent.slots.add(new FilteredSlot(this.itemHandler, index, x, y).setFilter(
			stack -> true /* TODO fluid item stack  */));
		return this;
	}

	public ContainerTileInventoryBuilder fuelSlot(final int index, final int x, final int y) {
		throw new NotImplementedException("add a item handler version of furnace slots");
		//this.parent.slots.add(new SlotFurnaceFuel(this.itemHandler, index, x, y));
		//return this;
	}

	@Deprecated
	public ContainerTileInventoryBuilder upgradeSlot(final int index, final int x, final int y) {
		this.parent.slots.add(new FilteredSlot(this.itemHandler, index, x, y)
			.setFilter(stack -> stack.getItem() instanceof IUpgrade));
		return this;
	}

	private ContainerTileInventoryBuilder upgradeSlots(IUpgradeable upgradeable) {
		if (upgradeable.canBeUpgraded()) {
			for (int i = 0; i < upgradeable.getUpgradeSlotCount(); i++) {
				this.parent.slots.add(new UpgradeSlot(upgradeable.getUpgradeInvetory(), i, -18, i * 18 + 12));
			}
		}
		return this;
	}

	/**
	 * @param supplier The supplier must supply a variable holding inside a Short, it
	 * will be truncated by force.
	 * @param setter The setter to call when the variable has been updated.
	 * @return ContainerTileInventoryBuilder Inventory which will do the sync
	 */
	public ContainerTileInventoryBuilder syncShortValue(final IntSupplier supplier, final IntConsumer setter) {
		this.parent.shortValues.add(Pair.of(supplier, setter));
		return this;
	}

	/**
	 * @param supplier The supplier it can supply a variable holding in an Integer it
	 * will be split inside multiples shorts.
	 * @param setter The setter to call when the variable has been updated.
	 * @return ContainerTileInventoryBuilder Inventory which will do the sync
	 */
	public ContainerTileInventoryBuilder syncIntegerValue(final IntSupplier supplier, final IntConsumer setter) {
		this.parent.integerValues.add(Pair.of(supplier, setter));
		return this;
	}

	/**
	 * @param supplier The supplier it can supply a variable holding in an Object it
	 * will be synced with a custom packet
	 * @param setter The setter to call when the variable has been updated.
	 * @return ContainerTileInventoryBuilder Inventory which will do the sync
	 */
	public <T> ContainerTileInventoryBuilder sync(final Supplier<T> supplier, final Consumer<T> setter) {
		this.parent.objectValues.add(Pair.of(supplier, setter));
		return this;
	}

	public ContainerTileInventoryBuilder syncEnergyValue() {
		if (this.tile instanceof TilePowerAcceptor) {
			return this.syncIntegerValue(() -> (int) ((TilePowerAcceptor) this.tile).getEnergy(),
				((TilePowerAcceptor) this.tile)::setEnergy)
				.syncIntegerValue(() -> (int) ((TilePowerAcceptor) this.tile).extraPowerStoage,
					((TilePowerAcceptor) this.tile)::setExtraPowerStoage)
				.syncIntegerValue(() -> (int) ((TilePowerAcceptor) this.tile).getPowerChange(),
					((TilePowerAcceptor) this.tile)::setPowerChange);
		}
		RebornCore.LOGGER.error(this.itemHandler + " is not an instance of TilePowerAcceptor! Energy cannot be synced.");
		return this;
	}

	public ContainerTileInventoryBuilder syncCrafterValue() {
		if (this.tile instanceof IRecipeCrafterProvider) {
			return this
				.syncIntegerValue(() -> ((IRecipeCrafterProvider) this.tile).getRecipeCrafter().currentTickTime,
					(currentTickTime) -> ((IRecipeCrafterProvider) this.tile)
						.getRecipeCrafter().currentTickTime = currentTickTime)
				.syncIntegerValue(() -> ((IRecipeCrafterProvider) this.tile).getRecipeCrafter().currentNeededTicks,
					(currentNeededTicks) -> ((IRecipeCrafterProvider) this.tile)
						.getRecipeCrafter().currentNeededTicks = currentNeededTicks);
		}
		RebornCore.LOGGER
			.error(this.itemHandler + " is not an instance of IRecipeCrafterProvider! Craft progress cannot be synced.");
		return this;
	}

	public ContainerTileInventoryBuilder onCraft(final Consumer<CraftingInventory> onCraft) {
		this.parent.craftEvents.add(onCraft);
		return this;
	}

	public ContainerBuilder addInventory() {
		this.parent.tileInventoryRanges.add(Range.between(this.rangeStart, this.parent.slots.size() - 1));
		return this.parent;
	}
}
