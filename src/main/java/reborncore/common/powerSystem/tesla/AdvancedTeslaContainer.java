package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TileEnergyBase;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class AdvancedTeslaContainer implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

    public TileEnergyBase tile;

    public AdvancedTeslaContainer(TileEnergyBase tile) {
        this.tile = tile;
    }

    public AdvancedTeslaContainer(NBTBase nbt, TileEnergyBase tile) {
        this.tile = tile;
        this.readNBT(nbt);
    }

    public long getStoredPower() {
        return (long) tile.getEnergy() * RebornCoreConfig.euPerRF;
    }

    //Receive
    public long givePower(long tesla, boolean simulated) {
        return (long) tile.addEnergy(tesla * RebornCoreConfig.euPerRF);
    }

    //Take power out
    public long takePower(long tesla,boolean simulated) {
        return (int) tile.useEnergy(tesla * RebornCoreConfig.euPerRF);
    }

    public long getCapacity() {
        return (long) tile.getMaxPower() * RebornCoreConfig.euPerRF;
    }

    public long getInputRate() {
        return (long)tile.getMaxInput() * RebornCoreConfig.euPerRF;
    }

    public long getOutputRate() {
        return (long)tile.getMaxOutput() * RebornCoreConfig.euPerRF;
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
