package reborncore.client.gui.builder.widget;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonSimple extends GuiButton {
	public GuiButtonSimple(int buttonId, int x, int y, String buttonText) {
		super(buttonId, x, y, buttonText);
	}

	public GuiButtonSimple(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}
}
