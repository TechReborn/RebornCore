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

package reborncore.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import reborncore.RebornCore;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.util.Inventory;
import reborncore.common.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Use this in your tile entity to craft things
 */
public class RecipeCrafter implements IUpgradeHandler {

	/**
	 * This is the recipe type to use
	 */
	public String recipeName;

	/**
	 * This is the parent tile
	 */
	public TileEntity parentTile;

	/**
	 * This is the place to use the power from
	 */
	public IEnergyInterfaceTile energy;

	public Optional<IUpgradeHandler> parentUpgradeHandler = Optional.empty();

	/**
	 * This is the amount of inputs that the setRecipe has
	 */
	public int inputs;

	/**
	 * This is the amount of outputs that the recipe has
	 */
	public int outputs;

	/**
	 * This is the inventory to use for the crafting
	 */
	public Inventory inventory;

	/**
	 * This is the list of the slots that the crafting logic should look for the
	 * input item stacks.
	 */
	public int[] inputSlots;

	/**
	 * This is the list for the slots that the crafting logic should look fot
	 * the output item stacks.
	 */
	public int[] outputSlots;
	public IBaseRecipeType currentRecipe;
	public int currentTickTime = 0;
	public int currentNeededTicks = 1;// Set to 1 to stop rare crashes
	double lastEnergy;

	int ticksSinceLastChange;

	@Nullable
	public static ICrafterSoundHanlder soundHanlder = null;

	public RecipeCrafter(String recipeName, TileEntity parentTile, int inputs, int outputs, Inventory inventory,
	                     int[] inputSlots, int[] outputSlots) {
		this.recipeName = recipeName;
		this.parentTile = parentTile;
		if (parentTile instanceof IEnergyInterfaceTile) {
			energy = (IEnergyInterfaceTile) parentTile;
		}
		if (parentTile instanceof IUpgradeHandler) {
			parentUpgradeHandler = Optional.of((IUpgradeHandler) parentTile);
		}
		this.inputs = inputs;
		this.outputs = outputs;
		this.inventory = inventory;
		this.inputSlots = inputSlots;
		this.outputSlots = outputSlots;
		if (!(parentTile instanceof IRecipeCrafterProvider)) {
			RebornCore.LOGGER.error(parentTile.getClass().getName() + " does not use IRecipeCrafterProvider report this to the issue tracker!");
		}
	}

	/**
	 * Call this on the tile tick
	 */
	public void updateEntity() {
		if (parentTile.getWorld().isRemote) {
			return;
		}
		ticksSinceLastChange++;
		// Force a has chanced every second
		if (ticksSinceLastChange == 20) {
			setInvDirty(true);
			ticksSinceLastChange = 0;
		}
		// It will now look for new recipes.
		if (currentRecipe == null && isInvDirty()) {
			updateCurrentRecipe();
		}
		if (currentRecipe != null) {
			// If it doesn't have all the inputs reset
			if (isInvDirty() && !hasAllInputs()) {
				currentRecipe = null;
				currentTickTime = 0;
				setIsActive();
			}
			// If it has reached the recipe tick time
			if (currentRecipe != null && currentTickTime >= currentNeededTicks && hasAllInputs()) {
				boolean canGiveInvAll = true;
				// Checks to see if it can fit the output
				for (int i = 0; i < currentRecipe.getOutputsSize(); i++) {
					if (!canFitStack(currentRecipe.getOutput(i), outputSlots[i], currentRecipe.useOreDic())) {
						canGiveInvAll = false;
					}
				}
				// The slots that have been filled
				ArrayList<Integer> filledSlots = new ArrayList<>();
				if (canGiveInvAll && currentRecipe.onCraft(parentTile)) {
					for (int i = 0; i < currentRecipe.getOutputsSize(); i++) {
						// Checks it has not been filled
						if (!filledSlots.contains(outputSlots[i])) {
							// Fills the slot with the output stack
							fitStack(currentRecipe.getOutput(i).copy(), outputSlots[i]);
							filledSlots.add(outputSlots[i]);
						}
					}
					// This uses all the inputs
					useAllInputs();
					// Reset
					currentRecipe = null;
					currentTickTime = 0;
					updateCurrentRecipe();
					//Update active sate if the tile isnt going to start crafting again
					if (currentRecipe == null) {
						setIsActive();
					}
				}
			} else if (currentRecipe != null && currentTickTime < currentNeededTicks) {
				// This uses the power
				if (energy.canUseEnergy(getEuPerTick(currentRecipe.euPerTick()))) {
					energy.useEnergy(getEuPerTick(currentRecipe.euPerTick()));
					// Increase the ticktime
					currentTickTime++;
					if (currentTickTime == 1 || currentTickTime % 20 == 0 && soundHanlder != null) {
						soundHanlder.playSound(false, parentTile);
					}
				}
			}
		}
		setInvDirty(false);
	}

	public void updateCurrentRecipe() {
		currentTickTime = 0;
		for (IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(recipeName)) {
			// This checks to see if it has all of the inputs
			if (recipe.canCraft(parentTile) && hasAllInputs(recipe)) {
				// This checks to see if it can fit all of the outputs
				for (int i = 0; i < recipe.getOutputsSize(); i++) {
					if (!canFitStack(recipe.getOutput(i), outputSlots[i], recipe.useOreDic())) {
						currentRecipe = null;
						this.currentTickTime = 0;
						setIsActive();
						return;
					}
				}
				// Sets the current recipe then syncs
				setCurrentRecipe(recipe);
				this.currentNeededTicks = Math.max((int) (currentRecipe.tickTime() * (1.0 - getSpeedMultiplier())), 1);
				this.currentTickTime = 0;
				setIsActive();
				return;
			}
		}
	}

	public boolean hasAllInputs() {
		if (currentRecipe == null) {
			return false;
		}
		for (Object input : currentRecipe.getInputs()) {
			boolean hasItem = false;
			boolean useOreDict = input instanceof String || input instanceof OreRecipeInput || currentRecipe.useOreDic();
			boolean checkSize = input instanceof ItemStack || input instanceof OreRecipeInput;
			for (int inputslot : inputSlots) {
				if (ItemUtils.isInputEqual(input, inventory.getStackInSlot(inputslot), true, true,
					useOreDict)) {
					ItemStack stack = RecipeTranslator.getStackFromObject(input);
					if (!checkSize || inventory.getStackInSlot(inputslot).getCount() >= stack.getCount()) {
						hasItem = true;
					}
				}
			}
			if (!hasItem) {
				return false;
			}
		}
		return true;
	}

	public boolean hasAllInputs(IBaseRecipeType recipeType) {
		if (recipeType == null) {
			return false;
		}
		for (Object input : recipeType.getInputs()) {
			boolean hasItem = false;
			boolean useOreDict = input instanceof String || input instanceof OreRecipeInput || recipeType.useOreDic();
			boolean checkSize = input instanceof ItemStack || input instanceof OreRecipeInput;
			for (int inputslot : inputSlots) {
				if (ItemUtils.isInputEqual(input, inventory.getStackInSlot(inputslot), true, true,
					useOreDict)) {
					ItemStack stack = RecipeTranslator.getStackFromObject(input);
					if (!checkSize || inventory.getStackInSlot(inputslot).getCount() >= stack.getCount()) {
						hasItem = true;
					}

				}
			}
			if (!hasItem) {
				return false;
			}
		}
		return true;
	}

	public void useAllInputs() {
		if (currentRecipe == null) {
			return;
		}
		for (Object input : currentRecipe.getInputs()) {
			for (int inputSlot : inputSlots) {// Uses all of the inputs
				if (ItemUtils.isInputEqual(input, inventory.getStackInSlot(inputSlot), true, true,
					currentRecipe.useOreDic())) {
					int count = 1;
					if (input instanceof ItemStack || input instanceof OreRecipeInput) {
						count = RecipeTranslator.getStackFromObject(input).getCount();
					}
					if (inventory.getStackInSlot(inputSlot).getCount() >= count) {
						inventory.shrinkSlot(inputSlot, count);
						break;
					}
				}
			}
		}
	}

	public boolean canFitStack(ItemStack stack, int slot, boolean oreDic) {// Checks to see if it can fit the stack
		if (stack.isEmpty()) {
			return true;
		}
		if (inventory.getStackInSlot(slot).isEmpty()) {
			return true;
		}
		if (ItemUtils.isItemEqual(inventory.getStackInSlot(slot), stack, true, true, oreDic)) {
			if (stack.getCount() + inventory.getStackInSlot(slot).getCount() <= stack.getMaxStackSize()) {
				return true;
			}
		}
		return false;
	}

	public void fitStack(ItemStack stack, int slot) {// This fits a stack into a slot
		if (stack.isEmpty()) {
			return;
		}
		if (inventory.getStackInSlot(slot).isEmpty()) {// If the slot is empty set the contents
			inventory.setStackInSlot(slot, stack);
			return;
		}
		if (ItemUtils.isItemEqual(inventory.getStackInSlot(slot), stack, true, true, currentRecipe.useOreDic())) {// If the slot has stuff in
			if (stack.getCount() + inventory.getStackInSlot(slot).getCount() <= stack.getMaxStackSize()) {// Check to see if it fits
				ItemStack newStack = stack.copy();
				newStack.setCount(inventory.getStackInSlot(slot).getCount() + stack.getCount());// Sets
				// the
				// new
				// stack
				// size
				inventory.setStackInSlot(slot, newStack);
			}
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagCompound data = tag.getCompound("Crater");

		if (data.hasKey("currentTickTime")) {
			currentTickTime = data.getInt("currentTickTime");
		}

		if (parentTile != null && parentTile.getWorld() != null && parentTile.getWorld().isRemote) {
			parentTile.getWorld().notifyBlockUpdate(parentTile.getPos(),
				parentTile.getWorld().getBlockState(parentTile.getPos()),
				parentTile.getWorld().getBlockState(parentTile.getPos()), 3);
			parentTile.getWorld().markBlockRangeForRenderUpdate(parentTile.getPos().getX(), parentTile.getPos().getY(),
				parentTile.getPos().getZ(), parentTile.getPos().getX(), parentTile.getPos().getY(),
				parentTile.getPos().getZ());
		}
	}

	public void writeToNBT(NBTTagCompound tag) {

		NBTTagCompound data = new NBTTagCompound();

		data.setDouble("currentTickTime", currentTickTime);

		tag.setTag("Crater", data);
	}

	private boolean isActive() {
		return currentRecipe != null && energy.getEnergy() >= currentRecipe.euPerTick();
	}

	public boolean canCraftAgain() {
		for (IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(recipeName)) {
			if (recipe.canCraft(parentTile) && hasAllInputs(recipe)) {
				boolean canGiveInvAll = true;
				for (int i = 0; i < recipe.getOutputsSize(); i++) {
					if (!canFitStack(recipe.getOutput(i), outputSlots[i], recipe.useOreDic())) {
						canGiveInvAll = false;
						return false;
					}
				}
				if (energy.getEnergy() < recipe.euPerTick()) {
					return false;
				}
				return canGiveInvAll;
			}
		}
		return false;
	}

	public void setIsActive() {
		if (parentTile.getWorld().getBlockState(parentTile.getPos()).getBlock() instanceof BlockMachineBase) {
			BlockMachineBase blockMachineBase = (BlockMachineBase) parentTile.getWorld()
				.getBlockState(parentTile.getPos()).getBlock();
			boolean isActive = isActive() || canCraftAgain();
			blockMachineBase.setActive(isActive, parentTile.getWorld(), parentTile.getPos());
		}
		parentTile.getWorld().notifyBlockUpdate(parentTile.getPos(),
			parentTile.getWorld().getBlockState(parentTile.getPos()),
			parentTile.getWorld().getBlockState(parentTile.getPos()), 3);
	}

	public void setCurrentRecipe(IBaseRecipeType recipe) {
		try {
			this.currentRecipe = (IBaseRecipeType) recipe.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public boolean isInvDirty() {
		return inventory.hasChanged();
	}

	public void setInvDirty(boolean isDiry) {
		inventory.setChanged(isDiry);
	}

	public boolean isStackValidInput(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		for (IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(recipeName)) {
			for (Object input : recipe.getInputs()) {
				boolean useOreDict = input instanceof String || input instanceof OreRecipeInput || recipe.useOreDic();
				if (ItemUtils.isInputEqual(input, stack, true, true,
					useOreDict)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void resetSpeedMulti() {
		parentUpgradeHandler.ifPresent(IUpgradeHandler::resetSpeedMulti);
	}

	@Override
	public double getSpeedMultiplier() {
		return Math.min(parentUpgradeHandler.map(IUpgradeHandler::getSpeedMultiplier).orElse(0D), 0.975);
	}

	@Override
	public void addPowerMulti(double amount) {
		parentUpgradeHandler.ifPresent(iUpgradeHandler -> iUpgradeHandler.addPowerMulti(amount));
	}

	@Override
	public void resetPowerMulti() {
		parentUpgradeHandler.ifPresent(IUpgradeHandler::resetPowerMulti);
	}

	@Override
	public double getPowerMultiplier() {
		return parentUpgradeHandler.map(IUpgradeHandler::getPowerMultiplier).orElse(1D);
	}

	@Override
	public double getEuPerTick(double baseEu) {
		double power = parentUpgradeHandler.map(iUpgradeHandler -> iUpgradeHandler.getEuPerTick(baseEu)).orElse(1D);
		return Math.min(power, energy.getMaxPower());
	}

	@Override
	public void addSpeedMulti(double amount) {
		parentUpgradeHandler.ifPresent(iUpgradeHandler -> iUpgradeHandler.addSpeedMulti(amount));
	}
}
