package reborncore.common.powerSystem.tesla;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public interface ITeslaPowerManager {

	void readFromNBT(NBTTagCompound compound, TilePowerAcceptor powerAcceptor);

	void writeToNBT(NBTTagCompound compound, TilePowerAcceptor powerAcceptor);

	<T> T getCapability(Capability<T> capability, EnumFacing facing, TilePowerAcceptor powerAcceptor);

	boolean hasCapability(Capability<?> capability, EnumFacing facing, TilePowerAcceptor powerAcceptor);

	void update(TilePowerAcceptor acceptor);

	void created(TilePowerAcceptor acceptor);

	String getDisplayableTeslaCount(long tesla);
}
