package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.ITeslaHandler;
import net.darkhax.tesla.api.TeslaContainer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class AdvancedTeslaContainer implements ITeslaHandler {

    TilePowerAcceptor tile;

    public AdvancedTeslaContainer(TilePowerAcceptor tile) {
        this.tile = tile;
    }

    public AdvancedTeslaContainer(EnumFacing side, NBTBase nbt, TilePowerAcceptor tile) {
        this.tile = tile;
        this.readNBT(side, nbt);
    }

    public long getStoredPower(EnumFacing side) {
        return (long)tile.getEnergy() / RebornCoreConfig.euPerRF;
    }

    public long givePower(long tesla, EnumFacing side, boolean simulated) {
        long acceptedTesla = Math.min(getCapacity(side) - getStoredPower(side), Math.min(getInputRate(), tesla));
        if(!simulated) {
            tile.setEnergy((long)tile.getEnergy() + (acceptedTesla * RebornCoreConfig.euPerRF));
        }

        return acceptedTesla;
    }

    public long takePower(long tesla, EnumFacing side, boolean simulated) {
        long removedPower = Math.min((long)tile.getEnergy(), Math.min(getOutputRate(), tesla));
        if(!simulated) {
            tile.setEnergy((long)tile.getEnergy() - (removedPower * RebornCoreConfig.euPerRF));
        }

        return removedPower;
    }

    public long getCapacity(EnumFacing side) {
        return (long)tile.getMaxPower()/ RebornCoreConfig.euPerRF;
    }

    public long getInputRate() {
        return (long)tile.getMaxInput() / RebornCoreConfig.euPerRF;
    }

    public long getOutputRate() {
        return (long)tile.getMaxOutput()/ RebornCoreConfig.euPerRF;
    }

    public NBTBase writeNBT(EnumFacing side) {
        NBTTagCompound dataTag = new NBTTagCompound();
        return dataTag;
    }

    public void readNBT(EnumFacing side, NBTBase nbt) {
    }

    public boolean isInputSide(EnumFacing side) {
        return true;
    }

    public boolean isOutputSide(EnumFacing side) {
        return true;
    }
}
