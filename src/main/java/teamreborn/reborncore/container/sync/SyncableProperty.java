package teamreborn.reborncore.container.sync;

import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SyncableProperty<T extends Object>
{

	private final Supplier<T> supplier;
	private final Consumer<T> consumer;
	protected     T           stored;

	public SyncableProperty(final Supplier<T> supplier, final Consumer<T> consumer)
	{
		this.supplier = supplier;
		this.consumer = consumer;
		this.stored = null;
	}

	public boolean areEquals(final T other)
	{
		return this.stored.equals(other);
	}

	public boolean needRefresh()
	{
		final T supplied = this.supplier.get();
		if (this.stored == null && supplied != null)
		{
			return true;
		}
		if (this.stored != null && supplied == null)
		{
			return true;
		}
		if (this.stored == null && supplied == null)
		{
			return false;
		}
		return !this.areEquals(supplied);
	}

	public void updateInternal()
	{
		this.stored = this.supplier.get();
	}

	public void update()
	{
		this.consumer.accept(this.stored);
	}

	public abstract NBTTagCompound toNBT(final NBTTagCompound tag);

	public abstract void fromNBT(NBTTagCompound tag);

	public Supplier<T> getSupplier()
	{
		return this.supplier;
	}

	public Consumer<T> getConsumer()
	{
		return this.consumer;
	}

	public T getStored()
	{
		return this.stored;
	}

	public void setStored(final T stored)
	{
		this.stored = stored;
	}
}
