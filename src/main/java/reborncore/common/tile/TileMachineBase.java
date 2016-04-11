package reborncore.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IInventoryProvider;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.packets.PacketHandler;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;

import java.util.Optional;

public class TileMachineBase extends TileEntity implements ITickable, IInventory
{

	public void syncWithAll()
	{
		if (!worldObj.isRemote)
		{
			PacketHandler.sendPacketToAllPlayers(getDescriptionPacket(), worldObj);
		}
	}

	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		worldObj.markBlockRangeForRenderUpdate(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX(),
				getPos().getY(), getPos().getZ());
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
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		if(getInventoryForTile().isPresent()){
			getInventoryForTile().get().writeToNBT(tagCompound);
		}
		if(getCrafterForTile().isPresent()){
			getCrafterForTile().get().writeToNBT(tagCompound);
		}
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
	//Inventory end
}
