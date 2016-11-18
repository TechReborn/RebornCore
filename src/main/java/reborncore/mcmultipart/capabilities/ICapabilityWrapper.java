package reborncore.mcmultipart.capabilities;

import net.minecraftforge.common.capabilities.Capability;

import java.util.Collection;

public interface ICapabilityWrapper<T> {

	public Capability<T> getCapability();

	public T wrapImplementations(Collection<T> implementations);

}
