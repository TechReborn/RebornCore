package reborncore.common.tile;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import reborncore.RebornCore;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.tile.IEnergyProducerTile;
import reborncore.api.power.tile.IEnergyReceiverTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;

import java.util.List;

@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
        @Optional.Interface(iface = "ic2.api.tile.IEnergyStorage", modid = "IC2")})
public abstract class TilePowerAcceptorProducer extends TileMachineBase implements
        IEnergyProducerTile, IEnergyReceiverTile,
        IEnergySource, IEnergySink, IEnergyStorage,
        IEnergyReceiver, IEnergyProvider, cofh.api.energy.IEnergyStorage,
        IListInfoProvider {

    private double energyStored = 0;

    //IC2 ///////////////////////////////

    @Override
    @Optional.Method(modid = "IC2")
    public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
        return canAcceptEnergy(enumFacing) && RebornCoreConfig.getRebornPower().eu();
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
        return canProvideEnergy(enumFacing) && RebornCoreConfig.getRebornPower().eu();
    }

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
        if(RebornCoreConfig.getRebornPower().eu())
            return amount - addEnergy(amount);
        return amount;
    }

    @Override
    @Optional.Method(modid = "IC2")
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
    public boolean isTeleporterCompatible(EnumFacing enumFacing) {
        return getTier().ic2SinkTier >= 2;
    }

    @Override
    public int getStored() {
        return (int) getEnergy();
    }

    @Override
    public void setStored(int amount) {
        setEnergy(amount);
    }

    @Override
    public int addEnergy(int amount) {
        return (int) (getEnergy() + addEnergy(amount * 1F));
    }

    @Override
    public int getCapacity() {
        return (int) getMaxPower();
    }

    @Override
    public int getOutput() {
        return (int) getMaxOutput();
    }

    @Override
    public double getOutputEnergyUnitsPerTick() {
        return getMaxOutput();
    }

    //IC2 END ///////////////////////////////


    //RF ///////////////////////////////

    @Override
    public int getEnergyStored(EnumFacing from) {
        return getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaxEnergyStored();
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        if(canProvideEnergy(from))
            return extractEnergy(maxExtract, simulate);
        return 0;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if(canAcceptEnergy(from))
            return receiveEnergy(maxReceive, simulate);
        return 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(RebornCoreConfig.getRebornPower().rf()) {
            return (int) (useEnergy(maxReceive / (RebornCoreConfig.euPerRF * 1F), simulate) * RebornCoreConfig.euPerRF);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if(RebornCoreConfig.getRebornPower().rf()) {
            return (int) (useEnergy(maxExtract / (RebornCoreConfig.euPerRF * 1F), simulate) * RebornCoreConfig.euPerRF);
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return (int) (RebornCoreConfig.euPerRF * getEnergy());
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) (RebornCoreConfig.euPerRF * getMaxPower());
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return RebornCoreConfig.getRebornPower().rf() && (canProvideEnergy(from) || canAcceptEnergy(from));
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
    public void addInfo(List<String> info, boolean isRealTile) {
        info.add(TextFormatting.LIGHT_PURPLE + "Energy Stored " +
                TextFormatting.GREEN + PowerSystem.getLocalizedPower(getEnergy()) + " / " +
                TextFormatting.GREEN + PowerSystem.getLocalizedPower(getMaxPower()));

        info.add(TextFormatting.LIGHT_PURPLE + "Max Input " +
                TextFormatting.GREEN + PowerSystem.getLocalizedPower(getMaxInput()));

        info.add(TextFormatting.LIGHT_PURPLE + "Max Output " +
                TextFormatting.GREEN + PowerSystem.getLocalizedPower(getMaxOutput()));


        info.add(TextFormatting.LIGHT_PURPLE + "Tier " +
                TextFormatting.GREEN + getTier());
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
        if(Loader.isModLoaded("Tesla") && RebornCoreConfig.getRebornPower().tesla()) {
            if (capability == TeslaCapabilities.CAPABILITY_PRODUCER && canProvideEnergy(facing))
                return true;
            else if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && canAcceptEnergy(facing))
                return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(Loader.isModLoaded("Tesla") && RebornCoreConfig.getRebornPower().tesla()) {
            if (capability == TeslaCapabilities.CAPABILITY_PRODUCER && canProvideEnergy(facing)) {
                return (T) new ITeslaProducer() {
                    @Override
                    public long takePower(long amount, boolean simulated) {
                        return extractEnergy(facing, (int) amount, simulated);
                    }
                };
            } else if(capability == TeslaCapabilities.CAPABILITY_CONSUMER && canAcceptEnergy(facing)) {
                return (T) new ITeslaConsumer() {
                    @Override
                    public long givePower(long amount, boolean simulated) {
                        return receiveEnergy(facing, (int) amount, simulated);
                    }
                };
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean canProvideEnergy(EnumFacing direction) {
        return getFacingEnum() == direction;
    }

    @Override
    public boolean canAcceptEnergy(EnumFacing direction) {
        return getFacingEnum() != direction;
    }

}
