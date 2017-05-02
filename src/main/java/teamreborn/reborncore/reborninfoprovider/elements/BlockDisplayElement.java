package teamreborn.reborncore.reborninfoprovider.elements;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;
import teamreborn.reborncore.reborninfoprovider.RebornInfoElement;

/**
 * File Created by Prospector.
 */
public class BlockDisplayElement extends RebornInfoElement {
	public IBlockState state = null;
	public ItemStack stack = ItemStack.EMPTY;
	public String string = "";
	private int width = 0;
	private int height = 0;

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void preRender(Minecraft mc) {
		reset();
		RayTraceResult rayTraceResult = mc.player.rayTrace(mc.playerController.getBlockReachDistance(), 0);
		state = mc.world.getBlockState(rayTraceResult.getBlockPos());
		stack = state.getBlock().getPickBlock(state, rayTraceResult, mc.world, rayTraceResult.getBlockPos(), mc.player);
		string = stack.getDisplayName();
		width += mc.fontRendererObj.getStringWidth(string) + 20;
		height += mc.fontRendererObj.FONT_HEIGHT + 7;
	}

	public void reset() {
		width = 0;
		height = 0;
		state = null;
		stack = ItemStack.EMPTY;
		string = "";
	}

	@Override
	public void render(int x, int y, RebornInfoProviderHUD gui, FontRenderer fontRendererObj) {
		gui.renderItemStack(stack, x, y);
		gui.drawString(fontRendererObj, string, x + 20, y + 4, 0xFFFFFFFF);
	}

	@Override
	public boolean isVisible() {
		if (stack.isEmpty())
			return false;
		return true;
	}
}
