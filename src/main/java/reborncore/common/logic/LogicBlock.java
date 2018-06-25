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

package reborncore.common.logic;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class LogicBlock extends BlockContainer {
	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static PropertyBool ACTIVE = PropertyBool.create("active");

	@Nonnull
	LogicController logicController;

	public LogicBlock(LogicController logicController) {
		super(Material.IRON);
		this.logicController = logicController;
		this.setUnlocalizedName(logicController.getName());
		this.logicController.initBlock(this);
		this.setHardness(logicController.getHardness());
		if (logicController.hasRotation())
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if (logicController != null) {
			return logicController.createNewTileEntity(worldIn, meta);
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return logicController.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		logicController.addInformation(stack, world, tooltip, advanced.isAdvanced());
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if (logicController != null) {
			logicController.getRenderType(state);
		}
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (logicController != null) {
			return logicController.getBoundingBox(state, source, pos);
		}
		return super.getBoundingBox(state, source, pos);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		logicController.onBlockAdded(worldIn, pos, state);
		super.onBlockAdded(worldIn, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		logicController.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (logicController.hasRotation()) {
			setFacing(placer.getHorizontalFacing().getOpposite(), worldIn, pos);
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (dropInv()) {
			dropInventory(worldIn, pos);
		}
		super.breakBlock(worldIn, pos, state);
	}

	public boolean dropInv() {
		return logicController.dropInv();
	}

	protected void dropInventory(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null) {
			return;
		}
		if (!(tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))) {
			return;
		}

		ItemStackHandler inventory = (ItemStackHandler) tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		List<ItemStack> items = new ArrayList<ItemStack>();

		addItemsToList(inventory, items);

		for (ItemStack itemStack : items) {
			Random rand = new Random();

			float dX = rand.nextFloat() * 0.8F + 0.1F;
			float dY = rand.nextFloat() * 0.8F + 0.1F;
			float dZ = rand.nextFloat() * 0.8F + 0.1F;

			EntityItem entityItem = new EntityItem(world, pos.getX() + dX, pos.getY() + dY, pos.getZ() + dZ, itemStack.copy());

			if (itemStack.hasTagCompound()) {
				entityItem.getItem().setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
			}

			float factor = 0.05F;
			entityItem.motionX = rand.nextGaussian() * factor;
			entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
			entityItem.motionZ = rand.nextGaussian() * factor;
			world.spawnEntity(entityItem);
			itemStack.setCount(0);
		}
	}

	private void addItemsToList(IItemHandler inventory, List<ItemStack> items) {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack itemStack = inventory.getStackInSlot(i);

			if (!itemStack.isEmpty() && itemStack.getCount() > 0) {
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

	@Override
	protected BlockStateContainer createBlockState() {
		FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
		ACTIVE = PropertyBool.create("active");
		return new BlockStateContainer(this, FACING, ACTIVE);
	}

	public void setFacing(EnumFacing facing, World world, BlockPos pos) {
		if (logicController.hasRotation()) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, facing));
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facingInt = getSideFromEnum(state.getValue(FACING));
		int activeInt = state.getValue(ACTIVE) ? 0 : 4;
		return facingInt + activeInt;
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
}
