package reborncore.api.gui;

import reborncore.client.gui.BaseGui;

/**
 * This is what you implement if you want to make a custom gui component.
 */
public interface IGuiComponent
{

	void initGui(BaseGui gui);

	void drawScreen(BaseGui gui);

	void drawGuiContainerForegroundLayer(BaseGui gui);

	void drawGuiContainerBackgroundLayer(BaseGui gui);

	void mouseClicked(BaseGui gui, int x, int y, int a);

	void keyTyped(BaseGui gui, char chara, int keyCode);

	void onGuiClosed(BaseGui gui);

	void updateScreen(BaseGui gui);
}
