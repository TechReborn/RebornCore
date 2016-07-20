package reborncore.common.tile;

import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import reborncore.api.power.tile.IEnergyReceiverTile;
import reborncore.common.RebornCoreConfig;

@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2")
public abstract class TilePowerAcceptor extends TileMachineBase implements IEnergySink, IEnergyReceiverTile, IEnergyReceiver {

    private double energyStored = 0;

    //IC2 ///////////////////////////////

    @Override
    @Optional.Method(modid = "IC2")
    public double getDemandedEnergy() {
        return getMaxPower() - getEnergy();
    }

    @Override
    @Optional.Method(modid = "IC2")
    public int getSinkTier() {
        return getTier().ic2SinkTier;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public double injectEnergy(EnumFacing enumFacing, double amount, double voltage) {
        return amount - addEnergy(amount);
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
        return canAcceptEnergy(enumFacing) && RebornCoreConfig.getRebornPower().eu();
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
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return (int) (RebornCoreConfig.euPerRF * addEnergy(maxReceive / (RebornCoreConfig.euPerRF * 1F), simulate));
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
        return canAcceptEnergy(from) && RebornCoreConfig.getRebornPower().rf();
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
            if(capability == TeslaCapabilities.CAPABILITY_CONSUMER)
                return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(Loader.isModLoaded("Tesla") && RebornCoreConfig.getRebornPower().tesla()) {
            if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
                return (T) (ITeslaConsumer) (amount, simulated) ->
                        receiveEnergy(facing, (int) amount, simulated);
            }
        }
        return super.getCapability(capability, facing);
    }

}
