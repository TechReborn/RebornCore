package reborncore.common.recipes;

import net.minecraft.inventory.IInventory;
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

import java.util.ArrayList;

/**
 * Use this in your tile entity to craft things
 */
public class RecipeCrafter {

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
	public IInventory inventory;

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
	/**
	 * This is used to change the speed of the crafting operation.
	 * <p/>
	 * 0 = none; 0.2 = 20% speed increase 0.75 = 75% increase
	 */
	double speedMultiplier = 0;
	/**
	 * This is used to change the power of the crafting operation.
	 * <p/>
	 * 1 = none; 1.2 = 20% speed increase 1.75 = 75% increase 5 = uses 5 times
	 * more power
	 */
	double powerMultiplier = 1;
	int ticksSinceLastChange;

	public RecipeCrafter(String recipeName, TileEntity parentTile, int inputs, int outputs, Inventory inventory,
	                     int[] inputSlots, int[] outputSlots) {
		this.recipeName = recipeName;
		this.parentTile = parentTile;
		if (parentTile instanceof IEnergyInterfaceTile) {
			energy = (IEnergyInterfaceTile) parentTile;
		}
		this.inputs = inputs;
		this.outputs = outputs;
		this.inventory = inventory;
		this.inputSlots = inputSlots;
		this.outputSlots = outputSlots;
		if (!(parentTile instanceof IRecipeCrafterProvider)) {
			RebornCore.logHelper.error(parentTile.getClass().getName() + " does not use IRecipeCrafterProvider report this to the issue tracker!");
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
		if (ticksSinceLastChange == 20) {// Force a has chanced every second
			setInvDirty(true);
			ticksSinceLastChange = 0;
		}
		if (currentRecipe == null && isInvDirty()) {// It will now look for new recipes.
			currentTickTime = 0;
			for (IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(recipeName)) {
				if (recipe.canCraft(parentTile) && hasAllInputs(recipe)) {// This checks to see if it has all of the inputs
					boolean canGiveInvAll = true;
					for (int i = 0; i < recipe.getOutputsSize(); i++) {// This checks to see if it can fit all of the outputs
						if (!canFitStack(recipe.getOutput(i), outputSlots[i], recipe.useOreDic())) {
							canGiveInvAll = false;
							return;
						}
					}
					if (canGiveInvAll) {
						setCurrentRecipe(recipe);// Sets the current recipe then
						// syncs
						this.currentNeededTicks = (int) (currentRecipe.tickTime() * (1.0 - speedMultiplier));
						this.currentTickTime = -1;
						setIsActive();
					} else {
						this.currentTickTime = -1;
					}
				}
			}
		} else {
			if (isInvDirty() && !hasAllInputs()) {// If it doesn't have all the inputs reset
				currentRecipe = null;
				currentTickTime = -1;
				setIsActive();
			}
			if (currentRecipe != null && currentTickTime >= currentNeededTicks) {// If it has reached the recipe tick time
				boolean canGiveInvAll = true;
				for (int i = 0; i < currentRecipe.getOutputsSize(); i++) {// Checks to see if it can fit the output
					if (!canFitStack(currentRecipe.getOutput(i), outputSlots[i], currentRecipe.useOreDic())) {
						canGiveInvAll = false;
					}
				}
				ArrayList<Integer> filledSlots = new ArrayList<>();// The
				// slots
				// that
				// have
				// been
				// filled
				if (canGiveInvAll && currentRecipe.onCraft(parentTile)) {
					for (int i = 0; i < currentRecipe.getOutputsSize(); i++) {
						if (!filledSlots.contains(outputSlots[i])) {// checks it has not been filled
							fitStack(currentRecipe.getOutput(i).copy(), outputSlots[i]);// fills
							// the
							// slot
							// with
							// the
							// output
							// stack
							filledSlots.add(outputSlots[i]);
						}
					}
					useAllInputs();// this uses all the inputs
					currentRecipe = null;// resets
					currentTickTime = -1;
					setIsActive();
				}
			} else if (currentRecipe != null && currentTickTime < currentNeededTicks) {
				if (energy.canUseEnergy(getEuPerTick())) {// This uses the power
					energy.useEnergy(getEuPerTick());
					currentTickTime++;// increase the ticktime
				}
			}
		}
		setInvDirty(false);
	}

	public boolean hasAllInputs() {
		if (currentRecipe == null) {
			return false;
		}
		for (Object input : currentRecipe.getInputs()) {
			boolean hasItem = false;
			boolean useOreDict = input instanceof String || currentRecipe.useOreDic();
			boolean checkSize = input instanceof ItemStack;
			for (int inputslot : inputSlots) {
				if (ItemUtils.isInputEqual(input, inventory.getStackInSlot(inputslot), true, true,
					useOreDict)) {
					ItemStack stack = RecipeTranslator.getStackFromObject(input);
					if (!checkSize || inventory.getStackInSlot(inputslot).getCount() >= stack.getCount()) {
						hasItem = true;
					}
				}
			}
			if (!hasItem)
				return false;
		}
		return true;
	}

	public boolean hasAllInputs(IBaseRecipeType recipeType) {
		if (recipeType == null) {
			return false;
		}
		for (Object input : recipeType.getInputs()) {
			boolean hasItem = false;
			boolean useOreDict = input instanceof String || recipeType.useOreDic();
			boolean checkSize = input instanceof ItemStack;
			for (int inputslot : inputSlots) {
				if (ItemUtils.isInputEqual(input, inventory.getStackInSlot(inputslot), true, true,
					useOreDict)) {
					ItemStack stack = RecipeTranslator.getStackFromObject(input);
					if (!checkSize || inventory.getStackInSlot(inputslot).getCount() >= stack.getCount()) {
						hasItem = true;
					}

				}
			}
			if (!hasItem)
				return false;
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
					if (input instanceof ItemStack) {
						count = RecipeTranslator.getStackFromObject(input).getCount();
					}
					inventory.decrStackSize(inputSlot, count);
					break;
				}
			}
		}
	}

	public boolean canFitStack(ItemStack stack, int slot, boolean oreDic) {// Checks to see if it can fit the stack
		if (stack == ItemStack.EMPTY) {
			return true;
		}
		if (inventory.getStackInSlot(slot) == ItemStack.EMPTY) {
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
		if (stack == ItemStack.EMPTY) {
			return;
		}
		if (inventory.getStackInSlot(slot) == ItemStack.EMPTY) {// If the slot is empty set the contents
			inventory.setInventorySlotContents(slot, stack);
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
				inventory.setInventorySlotContents(slot, newStack);
			}
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagCompound data = tag.getCompoundTag("Crater");

		if (data.hasKey("currentTickTime"))
			currentTickTime = data.getInteger("currentTickTime");

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

	private boolean canCraftAgain() {
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

	public void addSpeedMulti(double amount) {
		if (speedMultiplier + amount <= 0.99) {
			speedMultiplier += amount;
		} else {
			speedMultiplier = 0.99;
		}
	}

	public void resetSpeedMulti() {
		speedMultiplier = 0;
	}

	public double getSpeedMultiplier() {
		return speedMultiplier;
	}

	public void addPowerMulti(double amount) {
		powerMultiplier += amount;
	}

	public void resetPowerMulti() {
		powerMultiplier = 1;
	}

	public double getPowerMultiplier() {
		return powerMultiplier;
	}

	public double getEuPerTick() {
		return currentRecipe.euPerTick() * powerMultiplier;
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
		if (inventory instanceof Inventory) {
			return ((Inventory) inventory).isDirty;
		} else if (inventory instanceof reborncore.common.util.Inventory) {
			return ((reborncore.common.util.Inventory) inventory).hasChanged;
		}
		return true;
	}

	public void setInvDirty(boolean isDiry) {
		if (inventory instanceof Inventory) {
			((Inventory) inventory).isDirty = isDiry;
		} else if (inventory instanceof reborncore.common.util.Inventory) {
			((reborncore.common.util.Inventory) inventory).hasChanged = isDiry;
		}
	}

	public boolean isStackValidInput(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}
		for (IBaseRecipeType recipe : RecipeHandler.getRecipeClassFromName(recipeName)) {
			for (Object input : recipe.getInputs()) {
				boolean hasItem = false;
				boolean useOreDict = input instanceof String || recipe.useOreDic();
				boolean checkSize = input instanceof ItemStack;
				if (ItemUtils.isInputEqual(input, stack, true, true,
						useOreDict)) {
					return true;
				}

			}
		}
		return false;
	}
}
