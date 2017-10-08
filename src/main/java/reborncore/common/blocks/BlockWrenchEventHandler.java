package reborncore.common.blocks;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.api.ToolManager;

public class BlockWrenchEventHandler {

	@SubscribeEvent
	public static void rightClickBlockEvent(PlayerInteractEvent.RightClickBlock event){
		//TODO check that the block actaully needs to be wrenched, other wise this may cuase some weird issues
		if(ToolManager.INSTANCE.canHandleTool(event.getItemStack())){
			event.setUseBlock(Event.Result.ALLOW);
		}
	}


}
