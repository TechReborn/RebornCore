package teamreborn.reborncore.reborninfoprovider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * File Created by Prospector.
 */
public abstract class RebornInfoElement {
	public String meta = "none";

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void preRender(Minecraft mc);

	public abstract void render(int x, int y, RebornInfoProviderHUD gui, FontRenderer fontRendererObj);

	public boolean isVisible() {
		return true;
	}

	public void remove() {
		RebornInfoProviderHUD.removeElement(this);
	}
}
