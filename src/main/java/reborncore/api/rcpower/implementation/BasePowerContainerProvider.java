package reborncore.api.rcpower.implementation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import reborncore.common.capabilitys.PowerCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class BasePowerContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider
{
    private final BasePowerContainer container;

    public BasePowerContainerProvider(BasePowerContainer container)
    {
        this.container = container;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == PowerCapabilities.CAPABILITY_CONSUMER || capability == PowerCapabilities.CAPABILITY_PRODUCER || capability == PowerCapabilities.CAPABILITY_HOLDER;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability != PowerCapabilities.CAPABILITY_CONSUMER && capability != PowerCapabilities.CAPABILITY_PRODUCER && capability != PowerCapabilities.CAPABILITY_HOLDER?null: (T) this.container;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return this.container.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.container.deserializeNBT(nbt);
    }
}
