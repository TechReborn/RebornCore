package reborncore.api.power;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ExternalPowerHandler extends ICapabilityProvider {

	public void tick();

	public void unload();

	public void invalidate();

	@Override
	default boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
		return false;
	}

	@Nullable @Override
	default  <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
		return null;
	}
}
