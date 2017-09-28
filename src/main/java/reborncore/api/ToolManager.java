package reborncore.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ToolManager implements ICustomToolHandler {

	public static final ToolManager INSTANCE = new ToolManager();
	public List<ICustomToolHandler> customToolHandlerList = new ArrayList<>();

	@Override
	public boolean handleTool(ItemStack stack, BlockPos pos, World world, EntityPlayer player, EnumFacing side, boolean damage) {
		if(stack == null || stack.isEmpty()){
			return false;
		}
		if(stack.getItem() instanceof IToolHandler){
			return ((IToolHandler) stack.getItem()).handleTool(stack, pos, world, player, side, damage);
		}
		for(ICustomToolHandler customToolHandler : customToolHandlerList){
			if(customToolHandler.canHandleTool(stack)){
				return customToolHandler.handleTool(stack, pos, world, player, side, damage);
			}
		}
		return false;
	}

	@Override
	public boolean canHandleTool(ItemStack stack) {
		if(stack == null || stack.isEmpty()){
			return false;
		}
		if(stack.getItem() instanceof IToolHandler){
			return true;
		}
		for(ICustomToolHandler customToolHandler : customToolHandlerList){
			if(customToolHandler.canHandleTool(stack)){
				return true;
			}
		}
		return false;
	}
}
