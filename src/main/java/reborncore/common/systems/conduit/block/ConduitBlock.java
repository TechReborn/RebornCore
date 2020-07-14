/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.common.systems.conduit.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import reborncore.common.systems.conduit.IConduit;
import reborncore.common.util.WorldUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by DimmerWorld on 12/07/2020.
 */
public class ConduitBlock<T> extends BlockWithEntity {

	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty DOWN = BooleanProperty.of("down");

	final Class<? extends IConduit<T>> conduitEntityClass;

	public static final Map<Direction, BooleanProperty> PROPERTY_MAP = Util.make(new HashMap<>(), map -> {
		map.put(Direction.EAST, EAST);
		map.put(Direction.WEST, WEST);
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.UP, UP);
		map.put(Direction.DOWN, DOWN);
	});

	private final ConduitShapeUtil<T> conduitShapeUtil;

	Supplier<BlockEntity> blockEntityClass;

	public ConduitBlock(Supplier<BlockEntity> blockEntityClass, Class<? extends IConduit<T>> conduitEntityClass) {
		super(Settings.of(Material.STONE).strength(1f, 8f).nonOpaque());

		this.conduitEntityClass = conduitEntityClass;
		this.blockEntityClass = blockEntityClass;

		setDefaultState(this.getStateManager().getDefaultState().with(EAST, false).with(WEST, false).with(NORTH, false)
				.with(SOUTH, false).with(UP, false).with(DOWN, false));

		conduitShapeUtil = new ConduitShapeUtil<>(this);
	}

	public BooleanProperty getProperty(Direction facing) {
		switch (facing) {
		case EAST:
			return EAST;
		case WEST:
			return WEST;
		case NORTH:
			return NORTH;
		case SOUTH:
			return SOUTH;
		case UP:
			return UP;
		case DOWN:
			return DOWN;
		default:
			return EAST;
		}
	}

	private boolean connectToConduit(WorldAccess world, BlockPos ourPos, Direction direction) {
		BlockEntity ourBaseEntity = world.getBlockEntity(ourPos);
		BlockEntity otherBaseEntity = world.getBlockEntity(ourPos.offset(direction));


		if(!(conduitEntityClass.isInstance(ourBaseEntity))){
			return false;
		}

		// Cast to a variable our entity
		IConduit<T> ourEntity = conduitEntityClass.cast(ourBaseEntity);

		if(!(conduitEntityClass.isInstance(otherBaseEntity))){
			return false;
		}

		IConduit<T> otherEntity = conduitEntityClass.cast(otherBaseEntity);

		// Can't connect according to entities.
		if(!ourEntity.canConnect(direction, otherEntity) || !otherEntity.canConnect(direction.getOpposite(), ourEntity)){
			ourEntity.removeConduit(direction);
			return false;
		}

		// Add to entity as we've connected
		ourEntity.addConduit(direction, otherEntity);

		return true;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity entity = world.getBlockEntity(pos);

		if(hand != Hand.MAIN_HAND || !(conduitEntityClass.isInstance(entity))) return super.onUse(state, world, pos, player, hand, hit);

		IConduit<T> conduit = conduitEntityClass.cast(entity);
		Direction face = hit.getSide();

		// Sneaking to remove
		if(player.isSneaking()){
			ItemStack outcome = conduit.removeFunctionality(face);

			if(!outcome.isEmpty()){
				if (player.inventory.insertStack(outcome)) {
					return ActionResult.SUCCESS;
				}

				WorldUtils.dropItem(outcome, world, player.getBlockPos());
			}
		}else {
			if (conduit.addFunctionality(face, player.getMainHandStack())) {
				return ActionResult.SUCCESS;
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	// BlockContainer
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView worldIn) {
		if (blockEntityClass == null) {
			return null;
		}

		return blockEntityClass.get();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(EAST, WEST, NORTH, SOUTH, UP, DOWN);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState ourState, Direction ourFacing, BlockState otherState,
			WorldAccess worldIn, BlockPos ourPos, BlockPos otherPos) {

		if(worldIn.isClient()){
			return super.getStateForNeighborUpdate(ourState, ourFacing, otherState, worldIn, ourPos,otherPos);
		}

		return ourState.with(getProperty(ourFacing), connectToConduit(worldIn, ourPos, ourFacing));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext shapeContext) {
		return conduitShapeUtil.getShape(state);
	}




}
