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

package reborncore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.api.ToolManager;
import reborncore.api.tile.IMachineGuiHandler;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.common.BaseTileBlock;
import reborncore.common.RebornCoreConfig;
import reborncore.common.tile.TileMachineBase;
import reborncore.common.util.ItemHandlerUtils;
import reborncore.common.util.Tank;
import reborncore.common.util.WrenchUtils;

import javax.annotation.Nullable;

public abstract class BlockMachineBase extends BaseTileBlock {

	public static DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static BooleanProperty ACTIVE = BooleanProperty.create("active");

	public static ItemStack basicFrameStack;
	public static ItemStack advancedFrameStack;
	boolean hasCustomStaes;

	public BlockMachineBase(Block.Properties builder) {
		this(builder, false);
	}

	public BlockMachineBase(Block.Properties builder, boolean hasCustomStates) {
		super(builder);
		this.hasCustomStaes = hasCustomStates;
		if (!hasCustomStates) {
			this.setDefaultState(
				this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(ACTIVE, false));
		}
		BlockWrenchEventHandler.wrenableBlocks.add(this);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);
		ACTIVE = BooleanProperty.create("active");
		builder.add(FACING, ACTIVE);
	}



	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return null;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		setFacing(placer.getHorizontalFacing().getOpposite(), worldIn, pos);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type,
	                                @Nullable
		                                EntityType<? extends EntityLiving> entityType) {
		return false;
	}

	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			ItemHandlerUtils.dropContainedItems(worldIn, pos);
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
		if (RebornCoreConfig.wrenchRequired) {
			drops.add(isAdvanced() ? advancedFrameStack.copy() : basicFrameStack.copy());
		} else {
			super.getDrops(state, drops, world, pos, fortune);
		}
	}



	public boolean isAdvanced() {
		return false;
	}

	/*
	 *  Right-click should open GUI for all non-wrench items
	 *  Shift-Right-click should apply special action, like fill\drain bucket, install behavior, etc.
	 */
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		// We extended BlockTileBase. Thus we should always have tile entity. I hope.
		if (tileEntity == null) {
			return false;
		}

		if (tileEntity instanceof TileMachineBase) {
			Tank tank = ((TileMachineBase) tileEntity).getTank();
			if (tank != null && FluidUtil.interactWithFluidHandler(playerIn, hand, tank)) {
				return true;
			}
		}

		if (!stack.isEmpty()) {
			if (ToolManager.INSTANCE.canHandleTool(stack)) {
				if (WrenchUtils.handleWrench(stack, worldIn, pos, playerIn, side)) {
					return true;
				}
			} else if (stack.getItem() instanceof IUpgrade && tileEntity instanceof IUpgradeable) {
				IUpgradeable upgradeableEntity = (IUpgradeable) tileEntity;
				if (upgradeableEntity.canBeUpgraded()) {
					if (ItemHandlerHelper.insertItemStacked(upgradeableEntity.getUpgradeInvetory(), stack,
						true).getCount() > 0) {
						stack = ItemHandlerHelper.insertItemStacked(upgradeableEntity.getUpgradeInvetory(), stack, false);
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

		return super.onBlockActivated(state, worldIn, pos, playerIn, hand, side, hitX, hitY, hitZ);
	}

	public boolean isActive(IBlockState state) {
		return state.get(ACTIVE);
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.get(FACING);
	}

	public void setFacing(EnumFacing facing, World world, BlockPos pos) {
		if (hasCustomStaes) {
			return;
		}
		world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing));
	}

	public void setActive(Boolean active, World world, BlockPos pos) {
		if (hasCustomStaes) {
			return;
		}
		EnumFacing facing = world.getBlockState(pos).get(FACING);
		IBlockState state = world.getBlockState(pos).with(ACTIVE, active).with(FACING, facing);
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
