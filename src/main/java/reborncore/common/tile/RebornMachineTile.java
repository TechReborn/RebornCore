/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IInventoryProvider;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blocks.RebornMachineBlock;
import reborncore.common.blocks.RebornOrientableTileBlock;
import reborncore.common.container.RebornContainer;
import reborncore.common.fluids.RebornFluidHandler;
import reborncore.common.fluids.RebornFluidTank;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.CustomDescriptionPacket;
import reborncore.common.recipes.IUpgradeHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by modmuss50 on 04/11/2016.
 */
public class RebornMachineTile extends TileEntity implements ITickable, ISidedInventory, IUpgradeable, IUpgradeHandler {

    public Inventory upgradeInventory = new Inventory(getUpgradeSlotCount(), "upgrades", 1, this);
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

    public void syncWithAll() {
        if (!world.isRemote) {
            NetworkManager.sendToAllAround(new CustomDescriptionPacket(this.pos, this.writeToNBT(new NBTTagCompound())), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64));
        }
    }
    
    public void resetUpgrades() {
        resetPowerMulti();
        resetSpeedMulti();
    }

    public int getFacingInt() {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof RebornMachineBlock) {
            return ((RebornMachineBlock) block).getFacing(world, pos).getIndex();
        }
        return 0;
    }

    public EnumFacing getFacing() {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof RebornMachineBlock) {
            return ((RebornMachineBlock) block).getFacing(world, pos);
        } else if (block instanceof RebornOrientableTileBlock) {
	        return ((RebornOrientableTileBlock) block).getFacing(world, pos);
        }
        return null;
    }

	public boolean canSetFacing(EnumFacing facing) {
		return facing != getFacing();
	}

	public boolean setFacing(EnumFacing facing) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof RebornMachineBlock) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(RebornMachineBlock.FACING, facing));
			return true;
		} else if (block instanceof RebornOrientableTileBlock) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(RebornOrientableTileBlock.FACING, facing));
		}
		return false;
	}

    public Optional<Inventory> getInventoryForTile() {
        if (this instanceof IInventoryProvider) {
            IInventoryProvider inventory = (IInventoryProvider) this;
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
    
    public boolean hasSlotConfig() {
        return true;
    }

    @Nullable
    public RebornFluidTank getTank() {
        return null;
    }

    public boolean showTankConfig() {
        return getTank() != null;
    }

    //The amount of ticks between a slot transfer attempt, less is faster
    public int slotTransferSpeed() {
        return 4;
    }

    //The amount of fluid transfured each tick buy the fluid config
    public int fluidTransferAmount() {
        return 250;
    }

    public void setActive(boolean active) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof RebornMachineBlock) {
			((RebornMachineBlock) block).setActive(active, world, pos);
		}
		return;
    }
    
    public boolean isActive() {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof RebornMachineBlock) {
			return ((RebornMachineBlock) block).isActive(world.getBlockState(pos));
		}
		return false;
    }

    // TileEntity
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
        if (upgradeFacing != null) {
        	setFacing(upgradeFacing);
        	upgradeFacing = null;
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = super.writeToNBT(new NBTTagCompound());
        writeToNBT(compound);
        return compound;
    }
    
    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.getNbtCompound());
    }
    
    // This stops the tile from getting cleared when the state is
    // updated(rotation and on/off)
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        if (oldState.getBlock() != newSate.getBlock()) {
            return true;
        }
        return false;
    }

    private EnumFacing upgradeFacing = null;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        if (tagCompound.hasKey("facing")) {
        	// Upgrade from older versions of the mod
	        byte facingValue = tagCompound.getByte("facing");
	        upgradeFacing = EnumFacing.VALUES[facingValue];
        }

        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().readFromNBT(tagCompound);
        }
        if (getCrafterForTile().isPresent()) {
            getCrafterForTile().get().readFromNBT(tagCompound);
        }
        if (tagCompound.hasKey("slotConfig")) {
            slotConfiguration = new SlotConfiguration(tagCompound.getCompoundTag("slotConfig"));
        } else {
            if (getInventoryForTile().isPresent()) {
                slotConfiguration = new SlotConfiguration(getInventoryForTile().get());
            } else {
                slotConfiguration = new SlotConfiguration();
            }
        }
        if (tagCompound.hasKey("fluidConfig") && getTank() != null) {
            fluidConfiguration = new FluidConfiguration(tagCompound.getCompoundTag("fluidConfig"));
        } else if (getTank() != null && fluidConfiguration == null) {
            fluidConfiguration = new FluidConfiguration();
        }
        upgradeInventory.readFromNBT(tagCompound, "Upgrades");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().writeToNBT(tagCompound);
        }
        if (getCrafterForTile().isPresent()) {
            getCrafterForTile().get().writeToNBT(tagCompound);
        }
        if (slotConfiguration != null) {
            tagCompound.setTag("slotConfig", slotConfiguration.serializeNBT());
        }
        if (fluidConfiguration != null) {
            tagCompound.setTag("fluidConfig", fluidConfiguration.serializeNBT());
        }
        upgradeInventory.writeToNBT(tagCompound, "Upgrades");
        return tagCompound;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getDisplayName();
        }
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;

        if (getTank() != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (fluidConfiguration != null && fluidConfiguration.getSideDetail(facing) != null) {
                FluidConfiguration.FluidConfig fluidConfig = fluidConfiguration.getSideDetail(facing);
				return fluidConfig.getIoConfig().isEnabled();
            }

            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
        }

        if (getTank() != null && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        	return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new RebornFluidHandler(this, facing));
        }

        return super.getCapability(capability, facing);
    }

    // ITIckable
    @Override
    public void update() {
        @Nullable
        RecipeCrafter crafter = null;
        if (getCrafterForTile().isPresent()) {
            crafter = getCrafterForTile().get();
        }
        if (canBeUpgraded()) {
            resetUpgrades();
            for (int i = 0; i < getUpgradeSlotCount(); i++) {
            	ItemStack stack = getUpgradeInventory().getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IUpgrade) {
                    ((IUpgrade) stack.getItem()).process(this, stack);
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

    //ISidedInventory
    @Override
    public int getSizeInventory() {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getSizeInventory();
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        if (!getInventoryForTile().isPresent()) {
            return true;
        }
        for (ItemStack itemstack : getInventoryForTile().get().contents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getStackInSlot(index);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().decrStackSize(index, count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().removeStackFromSlot(index);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().setInventorySlotContents(index, stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getInventoryStackLimit();
        }
        return 0;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(this.pos) == this &&
                player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().openInventory(player);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().closeInventory(player);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
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
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().isItemValidForSlot(index, stack);
        }
        return false;
    }

    @Override
    public int getField(int id) {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getField(id);
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().setField(id, value);
        }
    }

    @Override
    public int getFieldCount() {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getFieldCount();
        }
        return 0;
    }

    @Override
    public void clear() {
        if (getInventoryForTile().isPresent()) {
            getInventoryForTile().get().clear();
        }
    }

    @Override
    public String getName() {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().getName();
        }
        return null;
    }

    @Override
    public boolean hasCustomName() {
        if (getInventoryForTile().isPresent()) {
            return getInventoryForTile().get().hasCustomName();
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (slotConfiguration == null) {
            return new int[]{}; //I think should be ok, if needed this can return all the slots
        }
        return slotConfiguration.getSlotsForSide(side).stream()
                .filter(Objects::nonNull)
                .filter(slotConfig -> slotConfig.slotIO.ioConfig != SlotConfiguration.ExtractConfig.NONE)
                .mapToInt(value -> value.slotID).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        SlotConfiguration.SlotConfigHolder slotConfigHolder = slotConfiguration.getSlotDetails(index);
        SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(direction);
        if (slotConfig.slotIO.ioConfig.isInsert()) {
            if (slotConfigHolder.filter() && getCrafterForTile().isPresent()) {
                RecipeCrafter crafter = getCrafterForTile().get();
                if (!crafter.isStackValidInput(itemStackIn)) {
                    return false;
                }
            }
            if (getContainerForTile().isPresent()) {
                RebornContainer container = getContainerForTile().get();
                if (container.slotMap.containsKey(index)) {
                    Slot slot = container.slotMap.get(index);
                    return slot.isItemValid(itemStackIn);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        SlotConfiguration.SlotConfigHolder slotConfigHolder = slotConfiguration.getSlotDetails(index);
        SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(direction);
        if (slotConfig.slotIO.ioConfig.isExtact()) {
            if (getContainerForTile().isPresent()) {
                RebornContainer container = getContainerForTile().get();
                if (container.slotMap.containsKey(index)) {
                    BaseSlot slot = container.slotMap.get(index);
                    return slot.canWorldBlockRemove();
                }
            } else {
                return true;
            }
        }
        return false;
    }
    //Inventory end


    // IUpgradable
    @Override
    public ISidedInventory getUpgradeInventory() {
        return upgradeInventory;
    }

    @Override
    public int getUpgradeSlotCount() {
        return 4;
    }

    // IUpgradeHandler
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
        if (RebornCoreConfig.exponentialMachineSpeedScaling) {
            // Algorithm: ProcessingTimePercentage = (1 - OverclockerSpeed)^NumberOfOverclockers

            // Convert speed reductions into percentages of original speed.
            // Example: A 30% speed reduction means 70% of original speed, since 100% - 30% = 70%.
            double percentSpeedToApply = 1.0 - amount;
            double percentSpeedCurrent = 1.0 - speedMultiplier;

            // Apply the speed scaling amount to the current percent speed.
            double newPercentSpeed = percentSpeedToApply * percentSpeedCurrent;

            // Convert back from percentage of original speed to speed reduction amount
            speedMultiplier = 1.0 - newPercentSpeed;
        } else {
            // Algorithm: ProcessingTimePercentage = (1 - OverclockerSpeed*NumberOfOverclockers)

            if (speedMultiplier + amount <= 0.99) {
                speedMultiplier += amount;
            } else {
                speedMultiplier = 0.99;
            }
        }
    }
}
