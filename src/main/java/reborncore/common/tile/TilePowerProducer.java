package reborncore.common.tile;

import cofh.api.energy.IEnergyProvider;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import reborncore.api.power.tile.IEnergyProducerTile;
import reborncore.common.RebornCoreConfig;

/**
 * Skeleton implementation of the TESLA, RF
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2")
public abstract class TilePowerProducer extends TileMachineBase implements IEnergyProvider, IEnergySource, IEnergyProducerTile {

    private double energyStored = 0;

    //IC2 ///////////////////////////////


    @Override
    public void update() {
        super.update();

        for(EnumFacing facing : EnumFacing.VALUES) {
            if(canProvideEnergy(facing))
                emitEnergy(facing, useEnergy(getMaxOutput()));
        }

    }

    /**
     * Dispatches energy to block on side
     * @param side side
     * @param amount amount of energy to dispatch
     * @return amount of energy dispatched
     */
    public abstract double emitEnergy(EnumFacing side, double amount);

    @Override
    public double getOfferedEnergy() {
        return getEnergy();
    }

    @Override
    @Optional.Method(modid = "IC2")
    public void drawEnergy(double amount) {
        useEnergy(amount);
    }

    @Override
    @Optional.Method(modid = "IC2")
    public int getSourceTier() {
        return getTier().ic2SinkTier;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
        return canProvideEnergy(enumFacing) && RebornCoreConfig.getRebornPower().rf();
    }

    @Override
    @Optional.Method(modid = "IC2")
    public void onChunkUnload() {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer())
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        super.onChunkUnload();
    }

    @Override
    @Optional.Method(modid = "IC2")
    public void onLoad() {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer())
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
        super.onLoad();
    }

    //IC2 END ///////////////////////////////


    //RF ///////////////////////////////

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return (int) (RebornCoreConfig.euPerRF * useEnergy(maxExtract / (RebornCoreConfig.euPerRF * 1F), simulate));
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (int) (getEnergy() * RebornCoreConfig.euPerRF);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return (int) (getMaxPower() * RebornCoreConfig.euPerRF);
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return canProvideEnergy(from) && RebornCoreConfig.getRebornPower().rf();
    }

    //RF END ///////////////////////////////


    @Override
    public double getEnergy() {
        return energyStored;
    }

    @Override
    public void setEnergy(double energy) {
        syncWithAll();
        this.energyStored = energy;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        if(tagCompound.hasKey("TilePowerAcceptor")) {
            NBTTagCompound data = tagCompound.getCompoundTag("TilePowerAcceptor");
            this.energyStored = data.getDouble("energy");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        NBTTagCompound data = new NBTTagCompound();
        data.setDouble("energy", energyStored);
        tagCompound.setTag("TilePowerAcceptor", data);
        return tagCompound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if(Loader.isModLoaded("Tesla") && RebornCoreConfig.getRebornPower().tesla())
            if(capability == TeslaCapabilities.CAPABILITY_PRODUCER)
                return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(Loader.isModLoaded("Tesla") && RebornCoreConfig.getRebornPower().tesla()) {
            if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
                return (T) (ITeslaProducer) (amount, simulated) ->
                        extractEnergy(facing, (int) amount, simulated);
            }
        }
        return super.getCapability(capability, facing);
    }

}
