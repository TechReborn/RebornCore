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

package reborncore.common;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class BaseTileBlock extends Block implements ITileEntityProvider {
	protected BaseTileBlock(Material materialIn) {
		super(materialIn);
		setHardness(1F);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
	}

	public Optional<ItemStack> getDropWithContents(World world, BlockPos pos, ItemStack stack){
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity == null){
			return Optional.empty();
		}
		ItemStack newStack = stack.copy();
		NBTTagCompound tileData = tileEntity.writeToNBT(new NBTTagCompound());
		stripLocationData(tileData);
		if(!newStack.hasTagCompound()){
			newStack.setTagCompound(new NBTTagCompound());
		}
		newStack.getTagCompound().setTag("tile_data", tileData);
		return Optional.of(newStack);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("tile_data")){
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("tile_data");
			injectLocationData(nbt, pos);
			tileEntity.readFromNBT(nbt);
			tileEntity.markDirty();
		}
	}

	private void stripLocationData(NBTTagCompound compound){
		compound.removeTag("x");
		compound.removeTag("y");
		compound.removeTag("z");
	}

	private void injectLocationData(NBTTagCompound compound, BlockPos pos){
		compound.setInteger("x", pos.getX());
		compound.setInteger("y", pos.getY());
		compound.setInteger("z", pos.getZ());
	}
}
