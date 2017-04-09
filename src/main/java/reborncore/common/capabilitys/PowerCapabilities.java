package reborncore.common.capabilitys;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import reborncore.api.rcpower.IPowerConsumer;
import reborncore.api.rcpower.IPowerHolder;
import reborncore.api.rcpower.IPowerProducer;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class PowerCapabilities
{
    @CapabilityInject(IPowerConsumer.class)
    public static Capability<IPowerConsumer> CAPABILITY_CONSUMER = null;

    @CapabilityInject(IPowerProducer.class)
    public static Capability<IPowerProducer> CAPABILITY_PRODUCER  = null;

    @CapabilityInject(IPowerHolder.class)
    public static Capability<IPowerHolder> CAPABILITY_HOLDER   = null;

    public static class CapabilityPowerConsumer<T extends IPowerConsumer> implements Capability.IStorage<IPowerConsumer>
    {
        @Override
        public NBTBase writeNBT(Capability<IPowerConsumer> capability, IPowerConsumer instance, EnumFacing side) {return null;}

        @Override
        public void readNBT(Capability<IPowerConsumer> capability, IPowerConsumer instance, EnumFacing side, NBTBase nbt) {}
    }

    public static class CapabilityPowerProducer<T extends IPowerProducer> implements Capability.IStorage<IPowerProducer>
    {
        @Override
        public NBTBase writeNBT(Capability<IPowerProducer> capability, IPowerProducer instance, EnumFacing side) {return null;}

        @Override
        public void readNBT(Capability<IPowerProducer> capability, IPowerProducer instance, EnumFacing side, NBTBase nbt) {}
    }

    public static class CapabilityPowerHolder<T extends IPowerHolder> implements Capability.IStorage<IPowerHolder>
    {
        @Override
        public NBTBase writeNBT(Capability<IPowerHolder> capability, IPowerHolder instance, EnumFacing side) {return null;}

        @Override
        public void readNBT(Capability<IPowerHolder> capability, IPowerHolder instance, EnumFacing side, NBTBase nbt) {}
    }
}
