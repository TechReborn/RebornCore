package reborncore.mcmultipart.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.mcmultipart.multipart.PartSlot;

public interface ISlottedCapabilityProvider {

	public boolean hasCapability(Capability<?> capability, PartSlot slot, EnumFacing facing);

	public <T> T getCapability(Capability<T> capability, PartSlot slot, EnumFacing facing);

}
