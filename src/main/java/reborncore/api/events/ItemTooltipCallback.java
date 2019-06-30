package reborncore.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public interface ItemTooltipCallback {

	Event<ItemTooltipCallback> EVENT = EventFactory.createArrayBacked(ItemTooltipCallback.class, (listeners) -> (stack, tooltipContext, components) -> {
		for(ItemTooltipCallback callback : listeners){
			callback.getTooltip(stack, tooltipContext, components);
		}
	});


	void getTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> components);

}
