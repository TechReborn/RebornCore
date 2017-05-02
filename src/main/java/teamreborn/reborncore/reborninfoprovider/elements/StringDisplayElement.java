package teamreborn.reborncore.reborninfoprovider.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import teamreborn.reborncore.reborninfoprovider.RebornInfoElement;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;

/**
 * File Created by Prospector.
 */
public class StringDisplayElement extends RebornInfoElement {
	public String string = "";
	private int width = 0;
	private int height = 0;

	public StringDisplayElement(String text) {
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
		width += mc.fontRendererObj.getStringWidth(string);
		height += mc.fontRendererObj.FONT_HEIGHT;
	}

	public void reset() {
		width = 0;
		height = 0;
	}

	@Override
	public void render(int x, int y, RebornInfoProviderHUD gui, FontRenderer fontRendererObj) {
		gui.drawString(fontRendererObj, string, x, y, 0xFFFFFFFF);
	}

	@Override
	public boolean isVisible() {
		if (string.isEmpty())
			return false;
		return true;
	}
}
