package reborncore.client.gui.componets;

import reborncore.api.gui.IGuiComponent;
import reborncore.client.gui.BaseGui;

public class BaseComponet implements IGuiComponent
{

	int x;
	int y;

	public BaseComponet(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public void initGui(BaseGui gui)
	{

	}

	@Override
	public void drawScreen(BaseGui gui)
	{

	}

	@Override
	public void drawGuiContainerForegroundLayer(BaseGui gui)
	{

	}

	@Override
	public void drawGuiContainerBackgroundLayer(BaseGui gui)
	{

	}

	@Override
	public void mouseClicked(BaseGui gui, int x, int y, int a)
	{

	}

	@Override
	public void keyTyped(BaseGui gui, char chara, int keyCode)
	{

	}

	@Override
	public void onGuiClosed(BaseGui gui)
	{

	}

	@Override
	public void updateScreen(BaseGui gui)
	{

	}
}
