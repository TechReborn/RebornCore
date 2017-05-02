package teamreborn.reborncore.reborninfoprovider.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import teamreborn.reborncore.reborninfoprovider.RebornInfoElement;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;

/**
 * File Created by Prospector.
 */
public class WeatherDisplayElement extends RebornInfoElement {
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
		if (mc.world.isRaining()) {
			string = "Looks wet to me, moron";
		} else {
			string = "Mighty fine weather!";
		}
		width += mc.fontRendererObj.getStringWidth(string);
		height += mc.fontRendererObj.FONT_HEIGHT;
	}

	public void reset() {
		width = 0;
		height = 0;
		string = "";
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
