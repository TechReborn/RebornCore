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

package reborncore.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FluidConfiguration implements INBTSerializable<NBTTagCompound> {

	HashMap<EnumFacing, FluidConfig> sideMap;
	boolean input, output;

	public FluidConfiguration() {
		sideMap = new HashMap<>();
		Arrays.stream(EnumFacing.values()).forEach(facing -> sideMap.put(facing, new FluidConfig(facing)));
	}

	public FluidConfiguration(NBTTagCompound tagCompound) {
		sideMap = new HashMap<>();
		deserializeNBT(tagCompound);
	}

	public FluidConfig getSideDetail(EnumFacing side) {
		if (side == null) {
			return sideMap.get(EnumFacing.NORTH);
		}
		return sideMap.get(side);
	}

	public List<FluidConfig> getAllSides() {
		return new ArrayList<>(sideMap.values());
	}

	public void updateFluidConfig(FluidConfig config) {
		FluidConfig toEdit = sideMap.get(config.side);
		toEdit.ioConfig = config.ioConfig;
	}

	public void update(TileMachineBase machineBase) {
		if (!input && !output) {
			return;
		}
		if (machineBase.getTank() == null || machineBase.getWorld().getGameTime() % machineBase.slotTransferSpeed() != 0) {
			return;
		}
		for (EnumFacing facing : EnumFacing.values()) {
			FluidConfig fluidConfig = getSideDetail(facing);
			if (fluidConfig == null || !fluidConfig.getIoConfig().isEnabled()) {
				continue;
			}
			IFluidHandler fluidHandler = getFluidHandler(machineBase, facing);
			if (fluidHandler == null) {
				continue;
			}
			if (autoInput() && fluidConfig.getIoConfig().isInsert()) {
				FluidUtil.tryFluidTransfer(machineBase.getTank(), fluidHandler, machineBase.fluidTransferAmount(), true);
			}
			if (autoOutput() && fluidConfig.getIoConfig().isExtact()) {
				FluidUtil.tryFluidTransfer(fluidHandler, machineBase.getTank(), machineBase.fluidTransferAmount(), true);
			}
		}
	}

	private IFluidHandler getFluidHandler(TileMachineBase machine, EnumFacing facing) {
		BlockPos pos = machine.getPos().offset(facing);
		TileEntity tileEntity = machine.getWorld().getTileEntity(pos);
		if (tileEntity == null) {
			return null;
		}
		return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
	}

	public boolean autoInput() {
		return input;
	}

	public boolean autoOutput() {
		return output;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		Arrays.stream(EnumFacing.values()).forEach(facing -> compound.put("side_" + facing.ordinal(), sideMap.get(facing).serializeNBT()));
		compound.putBoolean("input", input);
		compound.putBoolean("output", output);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		sideMap.clear();
		Arrays.stream(EnumFacing.values()).forEach(facing -> {
			NBTTagCompound compound = nbt.getCompound("side_" + facing.ordinal());
			FluidConfig config = new FluidConfig(compound);
			sideMap.put(facing, config);
		});
		input = nbt.getBoolean("input");
		output = nbt.getBoolean("output");
	}

	public static class FluidConfig implements INBTSerializable<NBTTagCompound> {
		EnumFacing side;
		FluidConfiguration.ExtractConfig ioConfig;

		public FluidConfig(EnumFacing side) {
			this.side = side;
			this.ioConfig = ExtractConfig.ALL;
		}

		public FluidConfig(EnumFacing side, FluidConfiguration.ExtractConfig ioConfig) {
			this.side = side;
			this.ioConfig = ioConfig;
		}

		public FluidConfig(NBTTagCompound tagCompound) {
			deserializeNBT(tagCompound);
		}

		public EnumFacing getSide() {
			return side;
		}

		public ExtractConfig getIoConfig() {
			return ioConfig;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.putInt("side", side.ordinal());
			tagCompound.putInt("config", ioConfig.ordinal());
			return tagCompound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			side = EnumFacing.values()[nbt.getInt("side")];
			ioConfig = FluidConfiguration.ExtractConfig.values()[nbt.getInt("config")];
		}
	}

	public enum ExtractConfig {
		NONE(false, false),
		INPUT(false, true),
		OUTPUT(true, false),
		ALL(true, true);

		boolean extact;
		boolean insert;

		ExtractConfig(boolean extact, boolean insert) {
			this.extact = extact;
			this.insert = insert;
		}

		public boolean isExtact() {
			return extact;
		}

		public boolean isInsert() {
			return insert;
		}

		public boolean isEnabled() {
			return extact || insert;
		}

		public FluidConfiguration.ExtractConfig getNext() {
			int i = this.ordinal() + 1;
			if (i >= FluidConfiguration.ExtractConfig.values().length) {
				i = 0;
			}
			return FluidConfiguration.ExtractConfig.values()[i];
		}
	}
}
