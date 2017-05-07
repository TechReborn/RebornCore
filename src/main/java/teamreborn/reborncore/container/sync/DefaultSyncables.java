package teamreborn.reborncore.container.sync;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultSyncables
{

	public static final class SyncableInteger extends SyncableProperty<Integer>
	{

		public SyncableInteger(final Supplier<Integer> supplier, final Consumer<Integer> consumer)
		{
			super(supplier, consumer);
		}

		@Override
		public NBTTagCompound toNBT(final NBTTagCompound tag)
		{
			tag.setInteger("integer", this.stored);
			return tag;
		}

		@Override
		public void fromNBT(final NBTTagCompound tag)
		{
			this.stored = tag.getInteger("integer");
		}
	}

	public static final class SyncableBoolean extends SyncableProperty<Boolean>
	{

		public SyncableBoolean(final Supplier<Boolean> supplier, final Consumer<Boolean> consumer)
		{
			super(supplier, consumer);
		}

		@Override
		public NBTTagCompound toNBT(final NBTTagCompound tag)
		{
			tag.setBoolean("boolean", this.stored);
			return tag;
		}

		@Override
		public void fromNBT(final NBTTagCompound tag)
		{
			this.stored = tag.getBoolean("boolean");
		}
	}

	public static final class SyncableString extends SyncableProperty<String>
	{

		public SyncableString(final Supplier<String> supplier, final Consumer<String> consumer)
		{
			super(supplier, consumer);
		}

		@Override
		public NBTTagCompound toNBT(final NBTTagCompound tag)
		{
			tag.setString("strings", this.stored);
			return tag;
		}

		@Override
		public void fromNBT(final NBTTagCompound tag)
		{
			this.stored = tag.getString("strings");
		}
	}

	public static final class SyncableFluid extends SyncableProperty<FluidStack>
	{

		public SyncableFluid(final Supplier<FluidStack> supplier, final Consumer<FluidStack> consumer)
		{
			super(supplier, consumer);
		}

		@Override
		public boolean areEquals(final FluidStack other)
		{
			return this.stored.equals(other) && this.stored.amount == other.amount;
		}

		@Override
		public NBTTagCompound toNBT(final NBTTagCompound tag)
		{
			this.stored.writeToNBT(tag);
			return tag;
		}

		@Override
		public void fromNBT(final NBTTagCompound tag)
		{
			this.stored = FluidStack.loadFluidStackFromNBT(tag);
		}
	}

	public static final class SyncableItem extends SyncableProperty<ItemStack>
	{

		public SyncableItem(final Supplier<ItemStack> supplier, final Consumer<ItemStack> consumer)
		{
			super(supplier, consumer);
		}

		@Override
		public NBTTagCompound toNBT(final NBTTagCompound tag)
		{
			this.stored.writeToNBT(tag);
			return tag;
		}

		@Override
		public void fromNBT(final NBTTagCompound tag)
		{
			this.stored = new ItemStack(tag);
		}
	}
}
