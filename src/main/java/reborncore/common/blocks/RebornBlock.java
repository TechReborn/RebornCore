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

package reborncore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;

import reborncore.api.ToolManager;
import reborncore.api.tile.IMachineGuiHandler;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.common.RebornCoreConfig;
import reborncore.common.fluids.RebornFluidTank;
import reborncore.common.items.WrenchHelper;
import reborncore.common.tile.TileLegacyMachineBase;
import reborncore.common.util.InventoryHelper;
import reborncore.common.util.Utils;

import java.util.Set;

public abstract class RebornBlock extends Block implements ITileEntityProvider {
	public static ItemStack basicFrameStack;
	public static ItemStack advancedFrameStack;
	boolean hasCustomStates;

	public RebornBlock() {
		this(false);
	}

	public RebornBlock(boolean hasCustomStates) {
		this(hasCustomStates, Utils.HORIZONTAL_FACINGS);
	}

	public RebornBlock(boolean hasCustomStates, Set<EnumFacing> supportedFacings) {
		super(Material.IRON);
		setHardness(2f);
		setSoundType(SoundType.METAL);

		if (!hasCustomStates) {
			setDefaultState(blockState.getBaseState()
					.withProperty(facingProperty, EnumFacing.NORTH)
					.withProperty(activeProperty, false));
		}

		this.supportedFacings = supportedFacings;

		BlockWrenchEventHandler.wrenableBlocks.add(this);
	}

	// Block >>
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, facingProperty, activeProperty);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = getFacingForPlacement(placer, placer.getHorizontalFacing());
		setFacing(facing, world, pos);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		EnumFacing current = EnumFacing.values()[currentFacing];
		EnumFacing target = current.rotateAround(axis.getAxis());
		if (supportedFacings.contains(target) && current != target) {
			setFacing(target, world, pos);
			return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		InventoryHelper.dropInventoryItems(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
	// << Block

	// ITileEntityProvider >>
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return null;
	}
	// << ITileEntityProvider

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {	
		if (RebornCoreConfig.wrenchRequired){
			drops.add(isAdvanced() ? advancedFrameStack.copy() : basicFrameStack.copy());
		}
		else {
			super.getDrops(drops, world, pos, state, fortune);
		}
	}

	public boolean isAdvanced() {
		return false;
	}

	/* 
	 *  Right-click should open GUI for all non-wrench items
	 *  Shift-Right-click should apply special action, like fill\drain bucket, install upgrade, etc.
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		ItemStack stack = playerIn.getHeldItem(hand);
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		// We extended BlockTileBase. Thus we should always have tile entity. I hope.
		if (tileEntity == null) {
			return false;
		}

		if(tileEntity instanceof TileLegacyMachineBase){
			RebornFluidTank tank = ((TileLegacyMachineBase) tileEntity).getTank();
			if (tank != null && FluidUtil.interactWithFluidHandler(playerIn, hand, tank)) {
				return true;
			}
		}

		if (!stack.isEmpty()) {
			if (ToolManager.INSTANCE.canHandleTool(stack)) {
				if (WrenchHelper.handleWrench(stack, worldIn, pos, playerIn, side)) {
					return true;
				}			
			} else if (stack.getItem() instanceof IUpgrade && tileEntity instanceof IUpgradeable) {
				IUpgradeable upgradeableEntity = (IUpgradeable) tileEntity;
				if (upgradeableEntity.canBeUpgraded()) {
					if (InventoryHelper.testInventoryInsertion(upgradeableEntity.getUpgradeInvetory(), stack,
							null) > 0) {
						InventoryHelper.insertItemIntoInventory(upgradeableEntity.getUpgradeInvetory(), stack);
						playerIn.setHeldItem(EnumHand.MAIN_HAND, stack);
						return true;
					}
				}
			}
		}

		if (getGui() != null && !playerIn.isSneaking()) {
			getGui().open(playerIn, pos, worldIn);
			return true;
		}

		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}



	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = currentFacing;
		int active = isActive ? 0 : 4;
		return facing + active;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean active = false;
		int facingInt = meta;
		if (facingInt >= 4) {
			active = true;
			facingInt = facingInt - 4;
		}
		isActive = active;
		currentFacing = (byte) facingInt;
		EnumFacing facing = EnumFacing.values()[facingInt];
		return this.getDefaultState().withProperty(facingProperty, facing).withProperty(activeProperty, active);
	}


	public abstract IMachineGuiHandler getGui();

	// BlockTileEntity >>
	public boolean isActive(IBlockState state) {
		return state.getValue(activeProperty);
	}

	public void setActive(boolean active, World world, BlockPos pos) {
		isActive = active;
		EnumFacing facing = EnumFacing.values()[currentFacing];
		IBlockState state = world.getBlockState(pos).withProperty(activeProperty, active).withProperty(facingProperty, facing);
		world.setBlockState(pos, state, 3);
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(facingProperty);
	}

	public void setFacing(EnumFacing facing, World world, BlockPos pos) {
		currentFacing = (byte) facing.ordinal();
		IBlockState state = world.getBlockState(pos).withProperty(activeProperty, isActive).withProperty(facingProperty, facing);
		world.setBlockState(pos, state, 3);
	}
	// << BlockTileEntity

	// Helpers >>
	protected static TileLegacyMachineBase getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		return tileEntity instanceof TileLegacyMachineBase ? (TileLegacyMachineBase) tileEntity : null;
	}

	protected EnumFacing getFacingForPlacement(EntityLivingBase placer, EnumFacing facing) {
		if (supportedFacings.isEmpty()) return EnumFacing.DOWN;

		if (placer == null) {
			return facing != null && supportedFacings.contains(facing.getOpposite())
					? facing.getOpposite()
					: supportedFacings.iterator().next();
		}

		EnumFacing bestFacing = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		Vec3d dir = placer.getLookVec();
		for (EnumFacing entry : supportedFacings) {
			double score = dir.dotProduct(new Vec3d(entry.getOpposite().getDirectionVec()));
			if (score > bestScore) {
				bestScore = score;
				bestFacing = entry;
			}
		}

		return bestFacing;
	}
	// << Helpers

	// Fields >>
	public static final IProperty<EnumFacing> facingProperty = PropertyDirection.create("facing");
	public static final IProperty<Boolean> activeProperty = PropertyBool.create("active");

	protected Set<EnumFacing> supportedFacings = Utils.HORIZONTAL_FACINGS;
	protected boolean isActive = false;
	protected byte currentFacing = 0;
	// << Fields
}
