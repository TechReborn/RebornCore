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

package reborncore.common.tile;

import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import reborncore.api.IListInfoProvider;
import reborncore.api.items.InventoryWrapper;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.api.tile.ItemHandlerProvider;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.container.RebornContainer;
import reborncore.common.network.ClientBoundPackets;
import reborncore.common.network.NetworkManager;
import reborncore.common.recipes.IUpgradeHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;
import reborncore.common.util.Tank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Created by modmuss50 on 04/11/2016.
 */
public class TileMachineBase extends BlockEntity implements Tickable, IUpgradeable, IUpgradeHandler, IListInfoProvider {

	public Inventory<TileMachineBase> upgradeInventory = new Inventory<>(getUpgradeSlotCount(), "upgrades", 1, this, (slotID, stack, face, direction, tile) -> true);
	public SlotConfiguration slotConfiguration;
	public FluidConfiguration fluidConfiguration;

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

	public TileMachineBase(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public void syncWithAll() {
		if (!world.isClient) {
			NetworkManager.sendToTracking(ClientBoundPackets.createCustomDescriptionPacket(this), this);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (slotConfiguration == null) {
			if (getInventoryForTile().isPresent()) {
				slotConfiguration = new SlotConfiguration(getInventoryForTile().get());
			} else {
				slotConfiguration = new SlotConfiguration();
			}
		}
		if (getTank() != null) {
			if (fluidConfiguration == null) {
				fluidConfiguration = new FluidConfiguration();
			}
		}
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(getPos(), 0, toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag compound = super.toTag(new CompoundTag());
		toTag(compound);
		return compound;
	}

	@Override
	public void tick() {
		@Nullable
		RecipeCrafter crafter = null;
		if (getCrafterForTile().isPresent()) {
			crafter = getCrafterForTile().get();
		}
		if (canBeUpgraded()) {
			resetUpgrades();
			for (int i = 0; i < getUpgradeSlotCount(); i++) {
				ItemStack stack = getUpgradeInvetory().getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IUpgrade) {
					((IUpgrade) stack.getItem()).process(this, this, stack);
				}
			}
		}
		if (!world.isClient) {
			if (crafter != null) {
				crafter.updateEntity();
			}
			if (slotConfiguration != null) {
				slotConfiguration.update(this);
			}
			if (fluidConfiguration != null) {
				fluidConfiguration.update(this);
			}
		}

	}

	public void resetUpgrades() {
		resetPowerMulti();
		resetSpeedMulti();
	}

	public int getFacingInt() {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase) {
			return ((BlockMachineBase) block).getFacing(world.getBlockState(pos)).getId();
		}
		return 0;
	}

	public Direction getFacingEnum() {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase) {
			return ((BlockMachineBase) block).getFacing(world.getBlockState(pos));
		}
		return null;
	}

	public void setFacing(Direction enumFacing) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase) {
			((BlockMachineBase) block).setFacing(enumFacing, world, pos);
		}
	}

	public boolean isActive() {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase) {
			return world.getBlockState(pos).get(BlockMachineBase.ACTIVE);
		}
		return false;
	}

	// This stops the tile from getting cleared when the state is
	// updated(rotation and on/off)
	//TODO 1.13 tile patches seem missing?
	//	@Override
	//	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
	//		if (oldState.getBlock() != newSate.getBlock()) {
	//			return true;
	//		}
	//		return false;
	//	}

	public Optional<Inventory> getInventoryForTile() {
		if (this instanceof ItemHandlerProvider) {
			ItemHandlerProvider inventory = (ItemHandlerProvider) this;
			if (inventory.getInventory() == null) {
				return Optional.empty();
			}
			return Optional.of((Inventory) inventory.getInventory());
		} else {
			return Optional.empty();
		}
	}

	protected Optional<RecipeCrafter> getCrafterForTile() {
		if (this instanceof IRecipeCrafterProvider) {
			IRecipeCrafterProvider crafterProvider = (IRecipeCrafterProvider) this;
			if (crafterProvider.getRecipeCrafter() == null) {
				return Optional.empty();
			}
			return Optional.of(crafterProvider.getRecipeCrafter());
		} else {
			return Optional.empty();
		}
	}

	protected Optional<RebornContainer> getContainerForTile() {
		if (this instanceof IContainerProvider) {
			IContainerProvider containerProvider = (IContainerProvider) this;
			if (containerProvider.getContainer() == null) {
				return Optional.empty();
			}
			return Optional.of(containerProvider.getContainer());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void fromTag(CompoundTag tagCompound) {
		super.fromTag(tagCompound);
		if (getInventoryForTile().isPresent()) {
			getInventoryForTile().get().read(tagCompound);
		}
		if (getCrafterForTile().isPresent()) {
			getCrafterForTile().get().read(tagCompound);
		}
		if (tagCompound.containsKey("slotConfig")) {
			slotConfiguration = new SlotConfiguration(tagCompound.getCompound("slotConfig"));
		} else {
			if (getInventoryForTile().isPresent()) {
				slotConfiguration = new SlotConfiguration(getInventoryForTile().get());
			} else {
				slotConfiguration = new SlotConfiguration();
			}
		}
		if (tagCompound.containsKey("fluidConfig") && getTank() != null) {
			fluidConfiguration = new FluidConfiguration(tagCompound.getCompound("fluidConfig"));
		} else if (getTank() != null && fluidConfiguration == null) {
			fluidConfiguration = new FluidConfiguration();
		}
		upgradeInventory.read(tagCompound, "Upgrades");
	}

	@Override
	public CompoundTag toTag(CompoundTag tagCompound) {
		super.toTag(tagCompound);
		if (getInventoryForTile().isPresent()) {
			getInventoryForTile().get().write(tagCompound);
		}
		if (getCrafterForTile().isPresent()) {
			getCrafterForTile().get().write(tagCompound);
		}
		if (slotConfiguration != null) {
			tagCompound.put("slotConfig", slotConfiguration.toTag());
		}
		if (fluidConfiguration != null) {
			tagCompound.put("fluidConfig", fluidConfiguration.toTag());
		}
		upgradeInventory.write(tagCompound, "Upgrades");
		return tagCompound;
	}

	private boolean isItemValidForSlot(int index, ItemStack stack) {
		if (slotConfiguration == null) {
			return false;
		}
		SlotConfiguration.SlotConfigHolder slotConfigHolder = slotConfiguration.getSlotDetails(index);
		if (slotConfigHolder.filter() && getCrafterForTile().isPresent()) {
			RecipeCrafter crafter = getCrafterForTile().get();
			if (!crafter.isStackValidInput(stack)) {
				return false;
			}
		}
		return false;
	}
	//Inventory end

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
//		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getInventoryForTile().isPresent()) {
//			return LazyOptional.of(() -> (T) getInventoryForTile().get().getExternal(facing));
//		}
//		if (getTank() != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
//			if (fluidConfiguration != null && fluidConfiguration.getSideDetail(facing) != null) {
//				FluidConfiguration.FluidConfig fluidConfig = fluidConfiguration.getSideDetail(facing);
//				if (!fluidConfig.getIoConfig().isEnabled()) {
//					return LazyOptional.empty();
//				}
//			}
//			getTank().setSide(facing);
//			return LazyOptional.of(() -> (T) getTank());
//		}
//		return super.getCapability(capability, facing);
//	}

	@Override
	public InventoryWrapper getUpgradeInvetory() {
		return upgradeInventory;
	}

	@Override
	public int getUpgradeSlotCount() {
		return 4;
	}

	public Direction getFacing() {
		return getFacingEnum();
	}

	@Override
	public void applyRotation(BlockRotation rotationIn) {
		setFacing(rotationIn.rotate(getFacing()));
	}

	@Override
	public void resetSpeedMulti() {
		speedMultiplier = 0;
	}

	@Override
	public double getSpeedMultiplier() {
		return speedMultiplier;
	}

	@Override
	public void addPowerMulti(double amount) {
		powerMultiplier = powerMultiplier * (1f + amount);
	}

	@Override
	public void resetPowerMulti() {
		powerMultiplier = 1;
	}

	@Override
	public double getPowerMultiplier() {
		return powerMultiplier;
	}

	@Override
	public double getEuPerTick(double baseEu) {
		return baseEu * powerMultiplier;
	}

	@Override
	public void addSpeedMulti(double amount) {
		if (speedMultiplier + amount <= 0.99) {
			speedMultiplier += amount;
		} else {
			speedMultiplier = 0.99;
		}
	}

	public boolean hasSlotConfig() {
		return true;
	}

	@Nullable
	public Tank getTank() {
		return null;
	}

	public boolean showTankConfig() {
		return getTank() != null;
	}

	//The amount of ticks between a slot tranfer atempt, less is faster
	public int slotTransferSpeed() {
		return 4;
	}

	//The amount of fluid transfured each tick buy the fluid config
	public int fluidTransferAmount() {
		return 250;
	}

	@Override
	public void addInfo(List<Component> info, boolean isRealTile, boolean hasData) {
		if (hasData) {
			if (getInventoryForTile().isPresent()) {
				info.add(new TextComponent(ChatFormat.GOLD + "" + getInventoryForTile().get().getContents() + ChatFormat.GRAY + " items"));
			}
			if (!upgradeInventory.isEmpty()) {
				info.add(new TextComponent(ChatFormat.GOLD + "" + upgradeInventory.getContents() + ChatFormat.GRAY + " upgrades"));
			}
		}
	}

	public Block getBlockType(){
		return world.getBlockState(pos).getBlock();
	}
}