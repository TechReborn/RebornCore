/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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

package reborncore.common.blocks;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import reborncore.api.IToolDrop;
import reborncore.api.IToolHandler;
import reborncore.api.tile.IMachineGuiHandler;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.common.BaseTileBlock;
import reborncore.common.RebornCoreConfig;
import reborncore.common.tile.TileMachineBase;
import reborncore.common.util.InventoryHelper;
import reborncore.common.util.WorldUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockMachineBase extends BaseTileBlock {

	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static PropertyBool ACTIVE = PropertyBool.create("active");

	public static ItemStack basicFrameStack;
	public static ItemStack advancedFrameStack;
	boolean hasCustomStaes;

	public BlockMachineBase() {
		this(false);
	}

	public BlockMachineBase(boolean hasCustomStates) {
		super(Material.IRON);
		setHardness(2f);
		setSoundType(SoundType.METAL);
		this.hasCustomStaes = hasCustomStates;
		if (!hasCustomStates) {
			this.setDefaultState(
				this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
		}
	}

	protected BlockStateContainer createBlockState() {

		FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
		ACTIVE = PropertyBool.create("active");
		return new BlockStateContainer(this, FACING, ACTIVE);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return null;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
	                            ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		setFacing(placer.getHorizontalFacing().getOpposite(), worldIn, pos);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		dropInventory(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	protected void dropInventory(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null) {
			return;
		}
		if (!(tileEntity instanceof IInventory)) {
			return;
		}

		IInventory inventory = (IInventory) tileEntity;
		List<ItemStack> items = new ArrayList<ItemStack>();
		addItemsToList(inventory, items);
		if (tileEntity instanceof IUpgradeable) {
			addItemsToList(((IUpgradeable) tileEntity).getUpgradeInvetory(), items);
		}
		WorldUtils.dropItems(items, world, pos);
	}

	private void addItemsToList(IInventory inventory, List<ItemStack> items) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack itemStack = inventory.getStackInSlot(i);

			if (itemStack == ItemStack.EMPTY) {
				continue;
			}
			if (itemStack != ItemStack.EMPTY && itemStack.getCount() > 0) {
				if (itemStack.getItem() instanceof ItemBlock) {
					if (((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockFluidBase
						|| ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockStaticLiquid
						|| ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockDynamicLiquid) {
						continue;
					}
				}
			}
			items.add(itemStack.copy());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		if (RebornCoreConfig.wrenchRequired){
			items.add(isAdvanced() ? advancedFrameStack.copy() : basicFrameStack.copy());
		}
		else {
			items = super.getDrops(world, pos, state, fortune);
		}
		
		return items;
	}

	public boolean isAdvanced() {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		if (axis == null) {
			return false;
		} else {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof TileMachineBase) {
				world.setBlockState(pos,
					world.getBlockState(pos).withProperty(FACING, axis));
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
	                                EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (fillBlockWithFluid(worldIn, pos, playerIn)) {
			return true;
		}
		ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
		if (playerIn.isSneaking()) {
			if (!stack.isEmpty() && stack.getItem() instanceof IUpgrade) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);
				if (tileEntity instanceof IUpgradeable) {
					if (((IUpgradeable) tileEntity).canBeUpgraded()) {
						if (InventoryHelper.testInventoryInsertion(((IUpgradeable) tileEntity).getUpgradeInvetory(), stack, null) > 0) {
							InventoryHelper.insertItemIntoInventory(((IUpgradeable) tileEntity).getUpgradeInvetory(), stack);
							playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							return true;
						}
					}
				}
			}
		}
		if (!stack.isEmpty() && stack.getItem() instanceof IToolHandler) {
			IToolHandler toolHandler = (IToolHandler) stack.getItem();
			if (toolHandler.handleTool(stack, pos, worldIn, playerIn, side, false)) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);
				if (playerIn.isSneaking()) {
					if (tileEntity instanceof IToolDrop) {
						ItemStack drop = ((IToolDrop) tileEntity).getToolDrop(playerIn);
						if (drop == null) {
							return false;
						}
						if (!drop.isEmpty()){
							spawnAsEntity(worldIn, pos, drop);
						}
						
//						worldIn.playSound(null, playerIn.posX, playerIn.posY,
//							playerIn.posZ, ModSounds.BLOCK_DISMANTLE,
//							SoundCategory.BLOCKS, 0.6F, 1F);
						if (!worldIn.isRemote) {
							worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
						}
						return true;
					}
				} else {
					tileEntity.rotate(Rotation.CLOCKWISE_90);
					return true;
				}
			}
		} else if (getGui() != null && !playerIn.isSneaking()){
			getGui().open(playerIn, pos, worldIn);
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}

	//	public boolean fillBlockWithFluid(World world, BlockPos pos, EntityPlayer entityplayer)
	//	{
	//		ItemStack current = entityplayer.inventory.getCurrentItem();
	//
	//		if (current != ItemStack.EMPTY)
	//		{
	//			TileEntity tile = world.getTileEntity(pos);
	//
	//			if (tile instanceof IFluidHandler)
	//			{
	//				IFluidHandler tank = (IFluidHandler) tile;
	//				// Handle FluidContainerRegistry
	//				if (FluidContainerRegistry.isContainer(current))
	//				{
	//					FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);
	//					// Handle filled containers
	//					if (liquid != null)
	//					{
	//						int qty = tank.fill(null, liquid, true);
	//
	//						if (qty != 0 && !entityplayer.capabilities.isCreativeMode)
	//						{
	//							if (current.getCount() > 1)
	//							{
	//								if (!entityplayer.inventory
	//										.addItemStackToInventory(FluidContainerRegistry.drainFluidContainer(current)))
	//								{
	//									entityplayer.dropItem(
	//											FluidContainerRegistry.drainFluidContainer(current), false);
	//								}
	//
	//								entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
	//										consumeItem(current));
	//							} else
	//							{
	//								entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem,
	//										FluidContainerRegistry.drainFluidContainer(current));
	//							}
	//						}
	//
	//						return true;
	//
	//						// Handle empty containers
	//					} else
	//					{
	//						FluidStack available = tank.getTankInfo(null)[0].fluid;
	//
	//						if (available != null)
	//						{
	//							ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
	//
	//							liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
	//
	//							if (liquid != null)
	//							{
	//								if (!entityplayer.capabilities.isCreativeMode)
	//								{
	//									if (current.getCount() > 1)
	//									{
	//										if (!entityplayer.inventory.addItemStackToInventory(filled))
	//										{
	//											return false;
	//										} else
	//										{
	//											entityplayer.inventory.setInventorySlotContents(
	//													entityplayer.inventory.currentItem, consumeItem(current));
	//										}
	//									} else
	//									{
	//										entityplayer.inventory.setInventorySlotContents(
	//												entityplayer.inventory.currentItem, consumeItem(current));
	//										entityplayer.inventory
	//												.setInventorySlotContents(entityplayer.inventory.currentItem, filled);
	//									}
	//								}
	//
	//								tank.drain(null, liquid.amount, true);
	//
	//								return true;
	//							}
	//						}
	//					}
	//				} else if (current.getItem() instanceof IFluidContainerItem)
	//				{
	//					if (current.getCount() != 1)
	//					{
	//						return false;
	//					}
	//
	//					if (!world.isRemote)
	//					{
	//						IFluidContainerItem container = (IFluidContainerItem) current.getItem();
	//						FluidStack liquid = container.getFluid(current);
	//						FluidStack tankLiquid = tank.getTankInfo(null)[0].fluid;
	//						boolean mustDrain = liquid == null || liquid.amount == 0;
	//						boolean mustFill = tankLiquid == null || tankLiquid.amount == 0;
	//						if (mustDrain && mustFill)
	//						{
	//							// Both are empty, do nothing
	//						} else if (mustDrain || !entityplayer.isSneaking())
	//						{
	//							liquid = tank.drain(null, 1000, false);
	//							int qtyToFill = container.fill(current, liquid, true);
	//							tank.drain(null, qtyToFill, true);
	//						} else if (mustFill || entityplayer.isSneaking())
	//						{
	//							if (liquid.amount > 0)
	//							{
	//								int qty = tank.fill(null, liquid, false);
	//								tank.fill(null, container.drain(current, qty, true), true);
	//							}
	//						}
	//					}
	//
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}

	//TODO 1.11 rewrite when I have something to test with
	public boolean fillBlockWithFluid(World world, BlockPos pos, EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facingInt = getSideFromEnum(state.getValue(FACING));
		int activeInt = state.getValue(ACTIVE) ? 0 : 4;
		return facingInt + activeInt;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean active = false;
		int facingInt = meta;
		if (facingInt > 4) {
			active = true;
			facingInt = facingInt - 4;
		}
		EnumFacing facing = getSideFromint(facingInt);
		return this.getDefaultState().withProperty(FACING, facing).withProperty(ACTIVE, active);
	}

	public boolean isActive(IBlockState state) {
		return state.getValue(ACTIVE);
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(FACING);
	}

	public void setFacing(EnumFacing facing, World world, BlockPos pos) {
		if (hasCustomStaes) {
			return;
		}
		world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, facing));
	}

	public void setActive(Boolean active, World world, BlockPos pos) {
		if (hasCustomStaes) {
			return;
		}
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		IBlockState state = world.getBlockState(pos).withProperty(ACTIVE, active).withProperty(FACING, facing);
		world.setBlockState(pos, state, 3);
	}

	public EnumFacing getSideFromint(int i) {
		if (i == 0) {
			return EnumFacing.NORTH;
		} else if (i == 1) {
			return EnumFacing.SOUTH;
		} else if (i == 2) {
			return EnumFacing.EAST;
		} else if (i == 3) {
			return EnumFacing.WEST;
		}
		return EnumFacing.NORTH;
	}

	public int getSideFromEnum(EnumFacing facing) {
		if (facing == EnumFacing.NORTH) {
			return 0;
		} else if (facing == EnumFacing.SOUTH) {
			return 1;
		} else if (facing == EnumFacing.EAST) {
			return 2;
		} else if (facing == EnumFacing.WEST) {
			return 3;
		}
		return 0;
	}

	public abstract IMachineGuiHandler getGui();
}
