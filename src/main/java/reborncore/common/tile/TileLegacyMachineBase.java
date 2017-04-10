package reborncore.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.ArrayUtils;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IInventoryProvider;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.container.RebornContainer;
import reborncore.common.packets.PacketHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by modmuss50 on 04/11/2016.
 */
public class TileLegacyMachineBase extends TileEntity implements ITickable, IInventory, ISidedInventory, IUpgradeable {

	public Inventory upgradeInventory = new Inventory(getUpgradeSlotCount(), "upgrades", 64, this);

	public void syncWithAll() {
		if (!world.isRemote) {
			PacketHandler.sendPacketToAllPlayers(getUpdatePacket(), world);
		}
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), writeToNBT(new NBTTagCompound()));
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = super.writeToNBT(new NBTTagCompound());
		writeToNBT(compound);
		return compound;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update() {
		updateEntity();
		if (getCrafterForTile().isPresent()) {
			getCrafterForTile().get().updateEntity();
		}
	}

	@Deprecated
	public void updateEntity() {

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
			return world.getBlockState(pos).getValue(BlockMachineBase.ACTIVE);
		}
		return false;
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

	protected Optional<Inventory> getInventoryForTile() {
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

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		if (getInventoryForTile().isPresent()) {
			getInventoryForTile().get().readFromNBT(tagCompound);
		}
		if (getCrafterForTile().isPresent()) {
			getCrafterForTile().get().readFromNBT(tagCompound);
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
		upgradeInventory.writeToNBT(tagCompound, "Upgrades");
		return tagCompound;
	}

	//Inventory Start
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
		if (getInventoryForTile().isPresent()) {
			return getInventoryForTile().get().isUsableByPlayer(player);
		}
		return false;
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
	public ITextComponent getDisplayName() {
		if (getInventoryForTile().isPresent()) {
			return getInventoryForTile().get().getDisplayName();
		}
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (getContainerForTile().isPresent()) {
			RebornContainer container = getContainerForTile().get();
			ArrayList<Integer> intList = new ArrayList<>();
			for (int i = 0; i < container.slotMap.size(); i++) {
				intList.add(i);
			}
			int[] intArr = ArrayUtils.toPrimitive(intList.toArray(new Integer[intList.size()]));
			return intArr;
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if (getContainerForTile().isPresent()) {
			RebornContainer container = getContainerForTile().get();
			if (container.slotMap.containsKey(index)) {
				Slot slot = container.slotMap.get(index);
				if (slot.isItemValid(itemStackIn)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (getContainerForTile().isPresent()) {
			RebornContainer container = getContainerForTile().get();
			if (container.slotMap.containsKey(index)) {
				BaseSlot slot = container.slotMap.get(index);
				if (slot.canWorldBlockRemove()) {
					return true;
				}
			}
		}
		return false;
	}
	//Inventory end

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) new SidedInvWrapper(this, facing);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public IInventory getUpgradeInvetory() {
		return upgradeInventory;
	}

	@Override
	public int getUpgradeSlotCount() {
		return 4;
	}
}
