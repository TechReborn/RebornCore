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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IInventoryProvider;
import reborncore.client.gui.BaseSlot;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.container.RebornContainer;
import reborncore.common.packets.PacketHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;
import scala.xml.dtd.impl.Base;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class TileMachineBase extends TileEntity implements ITickable, IInventory, ISidedInventory
{

	public void syncWithAll()
	{
		if (!worldObj.isRemote)
		{
			PacketHandler.sendPacketToAllPlayers(getUpdatePacket(), worldObj);
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
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update()
	{
		updateEntity();
		if(getCrafterForTile().isPresent()){
			getCrafterForTile().get().updateEntity();
		}
	}

	@Deprecated
	public void updateEntity()
	{

	}

	public int getFacingInt()
	{
		Block block = worldObj.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase)
		{
			return ((BlockMachineBase) block).getFacing(worldObj.getBlockState(pos)).getIndex();
		}
		return 0;
	}

	public EnumFacing getFacingEnum()
	{
		Block block = worldObj.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase)
		{
			return ((BlockMachineBase) block).getFacing(worldObj.getBlockState(pos));
		}
		return null;
	}

	public void setFacing(EnumFacing enumFacing)
	{
		Block block = worldObj.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase)
		{
			((BlockMachineBase) block).setFacing(enumFacing, worldObj, pos);
		}
	}

	public boolean isActive()
	{
		Block block = worldObj.getBlockState(pos).getBlock();
		if (block instanceof BlockMachineBase)
		{
			return worldObj.getBlockState(pos).getValue(BlockMachineBase.ACTIVE);
		}
		return false;
	}

	// This stops the tile from getting cleared when the state is
	// updated(rotation and on/off)
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		if (oldState.getBlock() != newSate.getBlock())
		{
			return true;
		}
		return false;
	}

	protected Optional<Inventory> getInventoryForTile(){
		if(this instanceof IInventoryProvider){
			IInventoryProvider inventory = (IInventoryProvider) this;
			if(inventory.getInventory() == null){
				return Optional.empty();
			}
			return Optional.of(inventory.getInventory());
		} else {
			return Optional.empty();
		}
	}

	protected Optional<RecipeCrafter> getCrafterForTile(){
		if(this instanceof IRecipeCrafterProvider){
			IRecipeCrafterProvider crafterProvider = (IRecipeCrafterProvider) this;
			if(crafterProvider.getRecipeCrafter() == null){
				return Optional.empty();
			}
			return Optional.of(crafterProvider.getRecipeCrafter());
		} else {
			return Optional.empty();
		}
	}

	protected Optional<RebornContainer> getContainerForTile(){
		if(this instanceof IContainerProvider){
			IContainerProvider containerProvider = (IContainerProvider) this;
			if(containerProvider.getContainer() == null){
				return Optional.empty();
			}
			return Optional.of(containerProvider.getContainer());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().readFromNBT(tagCompound);
		}
		if(getCrafterForTile().isPresent()){
			getCrafterForTile().get().readFromNBT(tagCompound);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().writeToNBT(tagCompound);
		}
		if(getCrafterForTile().isPresent()){
			getCrafterForTile().get().writeToNBT(tagCompound);
		}
		return tagCompound;
	}

	//Inventory Start
	@Override
	public int getSizeInventory() {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getStackInSlot(index);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().decrStackSize(index, count);
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().removeStackFromSlot(index);
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().setInventorySlotContents(index, stack);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().isUseableByPlayer(player);
		}
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().openInventory(player);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().closeInventory(player);
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().isItemValidForSlot(index, stack);
		}
		return false;
	}

	@Override
	public int getField(int id) {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getField(id);
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().setField(id, value);
		}
	}

	@Override
	public int getFieldCount() {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getFieldCount();
		}
		return 0;
	}

	@Override
	public void clear() {
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().clear();
		}
	}

	@Override
	public String getName() {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getName();
		}
		return null;
	}

	@Override
	public boolean hasCustomName() {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().hasCustomName();
		}
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		if(getInventoryForTile().isPresent()){
			return getInventoryForTile().get().getDisplayName();
		}
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(getContainerForTile().isPresent()){
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
		if(getContainerForTile().isPresent()){
			RebornContainer container = getContainerForTile().get();
			if(container.slotMap.containsKey(index)){
				Slot slot = container.slotMap.get(index);
				if(slot.isItemValid(itemStackIn)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if(getContainerForTile().isPresent()){
			RebornContainer container = getContainerForTile().get();
			if(container.slotMap.containsKey(index)){
				BaseSlot slot = container.slotMap.get(index);
				if(slot.canWorldBlockRemove()){
					return true;
				}
			}
		}
		return false;
	}
	//Inventory end
}
