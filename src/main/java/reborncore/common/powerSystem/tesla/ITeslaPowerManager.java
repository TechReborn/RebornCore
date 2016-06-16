package reborncore.common.powerSystem.tesla;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.common.powerSystem.TileEnergyBase;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public interface ITeslaPowerManager {

    void readFromNBT (NBTTagCompound compound, TileEnergyBase powerAcceptor);

    void writeToNBT (NBTTagCompound compound, TileEnergyBase powerAcceptor);

    <T> T getCapability (Capability<T> capability, EnumFacing facing, TileEnergyBase powerAcceptor);

    boolean hasCapability (Capability<?> capability, EnumFacing facing, TileEnergyBase powerAcceptor);

    void update(TileEnergyBase acceptor);

    void created(TileEnergyBase acceptor);

    String getDisplayableTeslaCount (long tesla);
}
