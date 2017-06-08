package reborncore.common.registration;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public enum ExecutionSide {
	COMMON,
	CLIENT,
	SERVER;

	public boolean canExcetue() {
		if (this == COMMON) {
			return true;
		}
		if (FMLLaunchHandler.side() == Side.CLIENT && this == CLIENT) {
			return true;
		}
		if (FMLLaunchHandler.side() == Side.SERVER && this == SERVER) {
			return true;
		}
		return false;
	}
}
