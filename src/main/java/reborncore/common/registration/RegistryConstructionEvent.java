package reborncore.common.registration;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Created by modmuss50 on 08/06/2017.
 */
public class RegistryConstructionEvent extends FMLStateEvent {
	@Override
	public LoaderState.ModState getModState() {
		return null;
	}
}
