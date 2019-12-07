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
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import reborncore.api.ToolManager;
import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.api.blockentity.IUpgrade;
import reborncore.api.blockentity.IUpgradeable;
import reborncore.api.items.InventoryUtils;
import reborncore.common.BaseBlockEntityProvider;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.fluid.FluidUtil;
import reborncore.common.util.ItemHandlerUtils;
import reborncore.common.util.Tank;
import reborncore.common.util.WrenchUtils;

public abstract class BlockMachineBase extends BaseBlockEntityProvider implements InventoryProvider {

	public static DirectionProperty FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
	public static BooleanProperty ACTIVE = BooleanProperty.of("active");

	boolean hasCustomStates;

	public BlockMachineBase() {
		this(Block.Settings.of(Material.METAL).strength(2F, 2F));
	}

	public BlockMachineBase(Block.Settings builder) {
		this(builder, false);
	}

	public BlockMachineBase(Block.Settings builder, boolean hasCustomStates) {
		super(builder);
		this.hasCustomStates = hasCustomStates;
		if (!hasCustomStates) {
			this.setDefaultState(
				this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(ACTIVE, false));
		}
		BlockWrenchEventHandler.wrenableBlocks.add(this);
	}


	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
		ACTIVE = BooleanProperty.of("active");
		builder.add(FACING, ACTIVE);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView worldIn) {
		return null;
	}

	@Override
	public void onPlaced(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onPlaced(worldIn, pos, state, placer, stack);
		setFacing(placer.getHorizontalFacing().getOpposite(), worldIn, pos);

		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if(blockEntity instanceof MachineBaseBlockEntity){
			((MachineBaseBlockEntity) blockEntity).onPlace(worldIn, pos, state, placer, stack);
		}
	}

	@Override
	public boolean allowsSpawning(BlockState state, BlockView world, BlockPos pos, EntityType<?> entityType_1) {
		return false;
	}

	@Override
	public void onBlockRemoved(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			ItemHandlerUtils.dropContainedItems(worldIn, pos);
			super.onBlockRemoved(state, worldIn, pos, newState, isMoving);
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
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockHitResult hitResult) {

		ItemStack stack = playerIn.getStackInHand(hand);
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);

		// We extended BlockTileBase. Thus we should always have blockEntity entity. I hope.
		if (blockEntity == null) {
			return ActionResult.PASS;
		}

		if (blockEntity instanceof MachineBaseBlockEntity) {
			Tank tank = ((MachineBaseBlockEntity) blockEntity).getTank();
			if (tank != null && FluidUtil.interactWithFluidHandler(playerIn, hand, tank)) {
				return ActionResult.SUCCESS;
			}
		}

		if (!stack.isEmpty()) {
			if (ToolManager.INSTANCE.canHandleTool(stack)) {
				if (WrenchUtils.handleWrench(stack, worldIn, pos, playerIn, hitResult.getSide())) {
					return ActionResult.SUCCESS;
				}
			} else if (stack.getItem() instanceof IUpgrade && blockEntity instanceof IUpgradeable) {
				IUpgradeable upgradeableEntity = (IUpgradeable) blockEntity;
				if (upgradeableEntity.canBeUpgraded()) {
					if (InventoryUtils.insertItemStacked(upgradeableEntity.getUpgradeInvetory(), stack,
					                                     true).getCount() > 0) {
						stack = InventoryUtils.insertItemStacked(upgradeableEntity.getUpgradeInvetory(), stack, false);
						playerIn.setStackInHand(Hand.MAIN_HAND, stack);
						return ActionResult.SUCCESS;
					}
				}
			}
		}

		if (getGui() != null && !playerIn.isSneaking()) {
			getGui().open(playerIn, pos, worldIn);
			return ActionResult.SUCCESS;
		}

		return super.onUse(state, worldIn, pos, playerIn, hand, hitResult);
	}

	@Override
	public SidedInventory getInventory(BlockState blockState, IWorld world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if(blockEntity instanceof MachineBaseBlockEntity){
			return (MachineBaseBlockEntity)blockEntity;
		}
		return null;
	}

	public boolean isActive(BlockState state) {
		return state.get(ACTIVE);
	}

	public Direction getFacing(BlockState state) {
		return state.get(FACING);
	}

	public void setFacing(Direction facing, World world, BlockPos pos) {
		if (hasCustomStates) {
			return;
		}
		world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing));
	}

	public void setActive(Boolean active, World world, BlockPos pos) {
		if (hasCustomStates) {
			return;
		}
		Direction facing = world.getBlockState(pos).get(FACING);
		BlockState state = world.getBlockState(pos).with(ACTIVE, active).with(FACING, facing);
		world.setBlockState(pos, state, 3);
	}

	public Direction getSideFromint(int i) {
		if (i == 0) {
			return Direction.NORTH;
		} else if (i == 1) {
			return Direction.SOUTH;
		} else if (i == 2) {
			return Direction.EAST;
		} else if (i == 3) {
			return Direction.WEST;
		}
		return Direction.NORTH;
	}

	public int getSideFromEnum(Direction facing) {
		if (facing == Direction.NORTH) {
			return 0;
		} else if (facing == Direction.SOUTH) {
			return 1;
		} else if (facing == Direction.EAST) {
			return 2;
		} else if (facing == Direction.WEST) {
			return 3;
		}
		return 0;
	}

	public abstract IMachineGuiHandler getGui();

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if(blockEntity instanceof MachineBaseBlockEntity){
			((MachineBaseBlockEntity) blockEntity).onBreak(world, playerEntity, blockPos, blockState);
		}
		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return Container.calculateComparatorOutput(getInventory(state, world, pos));
	}
}
