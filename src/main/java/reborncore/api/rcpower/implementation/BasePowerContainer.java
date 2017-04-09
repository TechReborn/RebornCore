package reborncore.api.rcpower.implementation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import reborncore.api.rcpower.IPowerConsumer;
import reborncore.api.rcpower.IPowerHolder;
import reborncore.api.rcpower.IPowerProducer;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class BasePowerContainer implements IPowerConsumer, IPowerProducer, IPowerHolder, INBTSerializable<NBTTagCompound>
{
    private int stored;
    private int capacity;
    private int inputRate;
    private int outputRate;
    private int tier;

    public BasePowerContainer(int stored, int capacity, int inputRate, int outputRate, int tier)
    {
        this.stored = stored;
        this.capacity = capacity;
        this.inputRate = inputRate;
        this.outputRate = outputRate;
        this.tier = tier;
    }

    public BasePowerContainer(int capacity, int inputRate, int outputRate, int tier)
    {
        this(0, capacity, inputRate, outputRate, tier);
    }

    public BasePowerContainer()
    {
        this(0, 5000, 50, 50);
    }

    @Override
    public int givePower(int amount, boolean simulated)
    {
        final int accepted = Math.min(this.getCapacity() - this.stored, Math.min(this.inputRate, amount));

        if (!simulated)
            this.stored += accepted;

        return accepted;
    }

    @Override
    public int getStoredPower()
    {
        return this.stored;
    }

    @Override
    public int getCapacity()
    {
        return this.capacity;
    }

    @Override
    public int takePower(int amount, boolean simulated)
    {
        final int removedPower = Math.min(this.stored, Math.min(this.outputRate, amount));

        if (!simulated)
            this.stored -= removedPower;

        return removedPower;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound dataTag = new NBTTagCompound();
        dataTag.setInteger("powerStored", this.stored);
        dataTag.setInteger("powerCapacity", this.capacity);
        dataTag.setInteger("powerInput", this.inputRate);
        dataTag.setInteger("powerOutput", this.outputRate);
        dataTag.setInteger("powerTier", this.tier);


        return dataTag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.stored = nbt.getInteger("powerStored");

        if (nbt.hasKey("powerCapacity"))
            this.capacity = nbt.getInteger("powerCapacity");

        if (nbt.hasKey("powerInput"))
            this.inputRate = nbt.getInteger("powerInput");

        if (nbt.hasKey("powerOutput"))
            this.outputRate = nbt.getInteger("powerOutput");

        if (nbt.hasKey("powerTier"))
            this.tier = nbt.getInteger("powerTier");

        if (this.stored > this.getCapacity())
            this.stored = this.getCapacity();
    }

    public int getInputRate()
    {
        return inputRate;
    }

    public int getOutputRate()
    {
        return outputRate;
    }

    public int getStored()
    {
        return stored;
    }

    public int getTier()
    {
        return tier;
    }
}
