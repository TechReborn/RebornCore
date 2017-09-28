package reborncore.api;

import net.minecraft.item.ItemStack;

public interface ICustomToolHandler extends IToolHandler {

	boolean canHandleTool(ItemStack stack);

}
