package reborncore.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FluidConfiguration implements INBTSerializable<NBTTagCompound> {

	HashMap<EnumFacing, FluidConfig> sideMap;
	boolean input, output;

	public FluidConfiguration() {
		sideMap = new HashMap<>();
		Arrays.stream(EnumFacing.VALUES).forEach(facing -> sideMap.put(facing, new FluidConfig(facing)));
	}

	public FluidConfiguration(NBTTagCompound tagCompound) {
		sideMap = new HashMap<>();
		deserializeNBT(tagCompound);
	}

	public FluidConfig getSideDetail(EnumFacing side) {
		return sideMap.get(side);
	}

	public List<FluidConfig> getAllSides() {
		return new ArrayList<>(sideMap.values());
	}

	public void updateFluidConfig(FluidConfig config) {
		FluidConfig toEdit = sideMap.get(config.side);
		toEdit.FluidIO = config.FluidIO;
	}

	public void update(TileLegacyMachineBase machineBase) {
		if (!input && !output) {
			return;
		}
		//TODO handle push and pull of fluids
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
		Arrays.stream(EnumFacing.VALUES).forEach(facing -> compound.setTag("side_" + facing.ordinal(), sideMap.get(facing).serializeNBT()));
		compound.setBoolean("input", input);
		compound.setBoolean("output", output);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		sideMap.clear();
		Arrays.stream(EnumFacing.VALUES).forEach(facing -> {
			NBTTagCompound compound = nbt.getCompoundTag("side_" + facing.ordinal());
			FluidConfig config = new FluidConfig(compound);
			sideMap.put(facing, config);
		});
		input = nbt.getBoolean("input");
		output = nbt.getBoolean("output");
	}

	public static class FluidConfig implements INBTSerializable<NBTTagCompound> {
		EnumFacing side;
		FluidIO FluidIO;

		public FluidConfig(EnumFacing side) {
			this.side = side;
			this.FluidIO = new FluidConfiguration.FluidIO(FluidConfiguration.ExtractConfig.NONE);
		}

		public FluidConfig(EnumFacing side, FluidConfiguration.FluidIO FluidIO) {
			this.side = side;
			this.FluidIO = FluidIO;
		}

		public FluidConfig(NBTTagCompound tagCompound) {
			deserializeNBT(tagCompound);
		}

		public EnumFacing getSide() {
			return side;
		}

		public FluidConfiguration.FluidIO getFluidIO() {
			return FluidIO;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setInteger("side", side.ordinal());
			tagCompound.setTag("config", FluidIO.serializeNBT());
			return tagCompound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			side = EnumFacing.VALUES[nbt.getInteger("side")];
			FluidIO = new FluidConfiguration.FluidIO(nbt.getCompoundTag("config"));
		}
	}

	public static class FluidIO implements INBTSerializable<NBTTagCompound> {
		FluidConfiguration.ExtractConfig ioConfig;

		public FluidIO(NBTTagCompound tagCompound) {
			deserializeNBT(tagCompound);
		}

		public FluidIO(FluidConfiguration.ExtractConfig ioConfig) {
			this.ioConfig = ioConfig;
		}

		public FluidConfiguration.ExtractConfig getIoConfig() {
			return ioConfig;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("config", ioConfig.ordinal());
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			ioConfig = FluidConfiguration.ExtractConfig.values()[nbt.getInteger("config")];
		}
	}

	public enum ExtractConfig {
		NONE(false, false),
		INPUT(false, true),
		OUTPUT(true, false);

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

		public FluidConfiguration.ExtractConfig getNext() {
			int i = this.ordinal() + 1;
			if (i >= FluidConfiguration.ExtractConfig.values().length) {
				i = 0;
			}
			return FluidConfiguration.ExtractConfig.values()[i];
		}
	}
}
