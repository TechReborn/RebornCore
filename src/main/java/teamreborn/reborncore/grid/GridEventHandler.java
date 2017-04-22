package teamreborn.reborncore.grid;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import teamreborn.reborncore.api.registry.RebornRegistry;
import teamreborn.reborncore.api.registry.impl.EventRegistry;

/**
 * Created by Mark on 22/04/2017.
 */
@RebornRegistry
@EventRegistry
public class GridEventHandler {

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event){

	}


}
