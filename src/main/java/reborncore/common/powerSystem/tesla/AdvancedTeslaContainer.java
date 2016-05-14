package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class AdvancedTeslaContainer implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

    TilePowerAcceptor tile;

    public AdvancedTeslaContainer(TilePowerAcceptor tile) {
        this.tile = tile;
    }

    public AdvancedTeslaContainer(NBTBase nbt, TilePowerAcceptor tile) {
        this.tile = tile;
        this.readNBT(nbt);
    }

    public long getStoredPower() {
        return (long)tile.getEnergy() / RebornCoreConfig.euPerRF;
    }

    public long givePower(long tesla, boolean simulated) {
        long acceptedTesla = Math.min(getCapacity() - getStoredPower(), Math.min(getInputRate(), tesla));
        if(!simulated) {
            tile.setEnergy((long)tile.getEnergy() + (acceptedTesla * RebornCoreConfig.euPerRF));
        }

        return acceptedTesla;
    }

    public long takePower(long tesla,boolean simulated) {
        long removedPower = Math.min((long)tile.getEnergy(), Math.min(getOutputRate(), tesla));
        if(!simulated) {
            tile.setEnergy((long)tile.getEnergy() - (removedPower * RebornCoreConfig.euPerRF));
        }

        return removedPower;
    }

    public long getCapacity() {
        return (long)tile.getMaxPower()/ RebornCoreConfig.euPerRF;
    }

    public long getInputRate() {
        return (long)tile.getMaxInput() / RebornCoreConfig.euPerRF;
    }

    public long getOutputRate() {
        return (long)tile.getMaxOutput()/ RebornCoreConfig.euPerRF;
    }

    public NBTBase writeNBT() {
        NBTTagCompound dataTag = new NBTTagCompound();
        return dataTag;
    }

    public void readNBT(NBTBase nbt) {
    }

    public boolean isInputSide() {
        return true;
    }

    public boolean isOutputSide() {
        return true;
    }

}
