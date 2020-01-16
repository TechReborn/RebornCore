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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;

import reborncore.api.ToolManager;
import reborncore.api.tile.IMachineGuiHandler;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.api.tile.IWrenchable;
import reborncore.common.RebornCoreConfig;
import reborncore.common.fluids.RebornFluidTank;
import reborncore.common.items.WrenchHelper;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.InventoryHelper;
import reborncore.common.util.Utils;

import java.util.Set;

public abstract class RebornMachineBlock extends Block implements ITileEntityProvider, IWrenchable {
    public static ItemStack basicFrameStack;
    public static ItemStack advancedFrameStack;
    boolean hasCustomStates;

    public RebornMachineBlock() {
        this(false);
    }

    public RebornMachineBlock(boolean hasCustomStates) {
        this(hasCustomStates, Utils.HORIZONTAL_FACINGS);
    }

    public RebornMachineBlock(boolean hasCustomStates, Set<EnumFacing> supportedFacings) {
        super(Material.IRON);

        setHardness(2.0F);
        setSoundType(SoundType.METAL);

        if (!hasCustomStates) {
            setDefaultState(blockState.getBaseState()
                    .withProperty(facingProperty, EnumFacing.NORTH)
                    .withProperty(activeProperty, false));
        }

        BlockWrenchEventHandler.wrenableBlocks.add(this);
    }

    // Block >>
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, facingProperty, activeProperty);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return;

        tile.onPlaced(placer, stack);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        InventoryHelper.dropInventoryItems(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return state;

        return tile.getBlockState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(facingProperty).ordinal();
        int active = state.getValue(activeProperty) ? 6 : 0;

        return facing + active;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = false;
        if (meta >= EnumFacing.values().length) {
            active = true;
            meta -= EnumFacing.values().length;
        }

        return getDefaultState().withProperty(facingProperty, EnumFacing.values()[meta])
                .withProperty(activeProperty, active);
    }
    // << Block

    // ITileEntityProvider >>
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
    }
    // << ITileEntityProvider

    // IWrenchable >>
    @Override
    public EnumFacing getFacing(World world, BlockPos pos) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return EnumFacing.DOWN;

        return tile.getFacing();
    }

    @Override
    public boolean canSetFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return false;

        return tile.canSetFacing(newFacing);
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return false;

        return tile.setFacing(newFacing);
    }
    // << IWrenchable

    // RebornMachineBlock >>
    public boolean isActive(World world, BlockPos pos) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return false;

        return tile.isActive();
    }

    public void setActive(boolean active, World world, BlockPos pos) {
        RebornMachineTile tile = getTileEntity(world, pos);
        if (tile == null) return;

        tile.setActive(active);
    }
    // << RebornMachineBlock

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (RebornCoreConfig.wrenchRequired) {
            drops.add(isAdvanced() ? advancedFrameStack.copy() : basicFrameStack.copy());
        } else {
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

        if (tileEntity instanceof RebornMachineTile) {
            RebornFluidTank tank = ((RebornMachineTile) tileEntity).getTank();
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

    public abstract IMachineGuiHandler getGui();

    // Helpers >>
    protected static RebornMachineTile getTileEntity(IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof RebornMachineTile ? (RebornMachineTile) tileEntity : null;
    }
    // << Helpers

    // Fields >>
    public static final IProperty<EnumFacing> facingProperty = PropertyDirection.create("facing", Utils.HORIZONTAL_FACINGS);
    public static final IProperty<Boolean> activeProperty = PropertyBool.create("active");
    // << Fields
}
