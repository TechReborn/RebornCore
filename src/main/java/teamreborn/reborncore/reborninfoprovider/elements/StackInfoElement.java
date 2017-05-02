package teamreborn.reborncore.reborninfoprovider.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import teamreborn.reborncore.reborninfoprovider.RebornInfoElement;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;

/**
 * File Created by Prospector.
 */
public class StackInfoElement extends RebornInfoElement {
	public ItemStack stack = ItemStack.EMPTY;
	public String string = "";
	private int width = 0;
	private int height = 0;

	public StackInfoElement(ItemStack stack, String text) {
		this.stack = stack;
		this.string = text;
	}

	public void setString(String string) {
		this.string = string;
	}

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
		if (string.isEmpty())
			string = stack.getDisplayName();
		width += mc.fontRendererObj.getStringWidth(string) + 20;
		height += mc.fontRendererObj.FONT_HEIGHT + 7;
	}

	public void reset() {
		width = 0;
		height = 0;
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
