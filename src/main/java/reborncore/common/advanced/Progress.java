package reborncore.common.advanced;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class Progress implements INBTSerializable<NBTTagCompound>
{
    int progress;
    int maxProgress;

    public Progress(int maxProgress)
    {
        this.progress = 0;
        this.maxProgress = maxProgress;
    }

    public Progress(int progress, int maxProgress)
    {
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    public int getProgress()
    {
        return progress;
    }

    public int getMaxProgress()
    {
        return maxProgress;
    }

    public void addProgress(int amount)
    {
        this.progress += amount;
    }

    public void setProgress(int amount)
    {
        this.progress = amount;
    }

    public void resetProgress()
    {
        this.progress = 0;
    }

    public void incrementProgress()
    {
        ++this.progress;
    }

    public boolean isProgressCompleate()
    {
        if(progress >= getMaxProgress())
        {
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound dataTag = new NBTTagCompound();
        dataTag.setInteger("progress", this.progress);
        dataTag.setInteger("maxProgress", this.maxProgress);
        return dataTag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.progress = nbt.getInteger("progress");
        this.maxProgress = nbt.getInteger("maxProgress");

        if (this.progress > this.getMaxProgress())
        {
            this.progress = this.getMaxProgress();
        }
    }
}
