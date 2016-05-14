package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.capability.TeslaStorage;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class TeslaPowerManager implements ITeslaPowerManager {

    AdvancedTeslaContainer container;

    @Override
    public void readFromNBT(NBTTagCompound compound, TilePowerAcceptor powerAcceptor) {
  //      this.container = new AdvancedTeslaContainer(null, compound.getTag("TeslaContainer"), powerAcceptor);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound, TilePowerAcceptor powerAcceptor) {
      //  compound.setTag("TeslaContainer", this.container.writeNBT(null));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing, TilePowerAcceptor powerAcceptor) {
        if (capability == TeslaStorage.TESLA_HANDLER_CAPABILITY)
            return (T) this.container;

        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing, TilePowerAcceptor powerAcceptor) {
        if (capability == TeslaStorage.TESLA_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Override
    public void update(TilePowerAcceptor powerAcceptor) {
        if(powerAcceptor.canProvideEnergy(null)){
            TeslaUtils.distributePowerEqually(powerAcceptor.getWorld(), powerAcceptor.getPos(), (long) powerAcceptor.getMaxOutput(), false);
        }
    }

    @Override
    public void created(TilePowerAcceptor powerAcceptor) {
        this.container = new AdvancedTeslaContainer(powerAcceptor);
    }

    @Override
    public String getDisplayableTeslaCount(long tesla) {
        return TeslaUtils.getDisplayableTeslaCount(tesla / RebornCoreConfig.euPerRF);
    }

    public static ITeslaPowerManager getPowerManager(){
        return new TeslaPowerManager();
    }
}
