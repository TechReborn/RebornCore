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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.NonNullSupplier;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import reborncore.api.IListInfoProvider;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.api.tile.ItemHandlerProvider;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.container.RebornContainer;
import reborncore.common.recipes.IUpgradeHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;
import reborncore.common.util.Tank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Created by modmuss50 on 04/11/2016.
 */
public class TileMachineBase extends TileEntity implements ITickable, IUpgradeable, IUpgradeHandler, IListInfoProvider {

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

	public TileMachineBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public void syncWithAll() {
		if (!world.isRemote) {
			//TODO 1.13 networking
			//NetworkManager.sendToAllAround(new CustomDescriptionPacket(this.pos, this.write(new NBTTagCompound())), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64));
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = super.write(new NBTTagCompound());
		write(compound);
		return compound;
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		read(pkt.getNbtCompound());
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
		if (!world.isRemote) {
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
			return ((BlockMachineBase) block).getFacing(world.getBlockState(pos)).getIndex();
		}
		return 0;
	}

	public EnumFacing getFacingEnum() {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase) {
			return ((BlockMachineBase) block).getFacing(world.getBlockState(pos));
		}
		return null;
	}

	public void setFacing(EnumFacing enumFacing) {
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
	public void read(NBTTagCompound tagCompound) {
		super.read(tagCompound);
		if (getInventoryForTile().isPresent()) {
			getInventoryForTile().get().read(tagCompound);
		}
		if (getCrafterForTile().isPresent()) {
			getCrafterForTile().get().read(tagCompound);
		}
		if (tagCompound.hasKey("slotConfig")) {
			slotConfiguration = new SlotConfiguration(tagCompound.getCompound("slotConfig"));
		} else {
			if (getInventoryForTile().isPresent()) {
				slotConfiguration = new SlotConfiguration(getInventoryForTile().get());
			} else {
				slotConfiguration = new SlotConfiguration();
			}
		}
		if (tagCompound.hasKey("fluidConfig") && getTank() != null) {
			fluidConfiguration = new FluidConfiguration(tagCompound.getCompound("fluidConfig"));
		} else if (getTank() != null && fluidConfiguration == null) {
			fluidConfiguration = new FluidConfiguration();
		}
		upgradeInventory.read(tagCompound, "Upgrades");
	}

	@Override
	public NBTTagCompound write(NBTTagCompound tagCompound) {
		super.write(tagCompound);
		if (getInventoryForTile().isPresent()) {
			getInventoryForTile().get().write(tagCompound);
		}
		if (getCrafterForTile().isPresent()) {
			getCrafterForTile().get().write(tagCompound);
		}
		if (slotConfiguration != null) {
			tagCompound.setTag("slotConfig", slotConfiguration.serializeNBT());
		}
		if (fluidConfiguration != null) {
			tagCompound.setTag("fluidConfig", fluidConfiguration.serializeNBT());
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

	@Override
	public <T> OptionalCapabilityInstance<T> getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getInventoryForTile().isPresent()) {
			return OptionalCapabilityInstance.of(new NonNullSupplier<T>() {
				@Nonnull
				@Override
				public T get() {
					return (T) getInventoryForTile().get().getExternal(facing);
				}
			});
		}
		if (getTank() != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if (fluidConfiguration != null && fluidConfiguration.getSideDetail(facing) != null) {
				FluidConfiguration.FluidConfig fluidConfig = fluidConfiguration.getSideDetail(facing);
				if (!fluidConfig.getIoConfig().isEnabled()) {
					return null;
				}
			}
			getTank().setSide(facing);
			return OptionalCapabilityInstance.of(new NonNullSupplier<T>() {
				@Nonnull
				@Override
				public T get() {
					return (T) getTank();
				}
			});
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public IItemHandler getUpgradeInvetory() {
		return upgradeInventory;
	}

	@Override
	public int getUpgradeSlotCount() {
		return 4;
	}

	public EnumFacing getFacing() {
		return getFacingEnum();
	}

	@Override
	public void rotate(Rotation rotationIn) {
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
	public void addInfo(List<String> info, boolean isRealTile, boolean hasData) {
		if (hasData) {
			if (getInventoryForTile().isPresent()) {
				info.add(TextFormatting.GOLD + "" + getInventoryForTile().get().getContents() + TextFormatting.GRAY + " items");
			}
			if (!upgradeInventory.isEmpty()) {
				info.add(TextFormatting.GOLD + "" + upgradeInventory.getContents() + TextFormatting.GRAY + " upgrades");
			}
		}
	}
}