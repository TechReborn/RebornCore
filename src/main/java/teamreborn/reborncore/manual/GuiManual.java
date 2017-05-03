package teamreborn.reborncore.manual;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import teamreborn.reborncore.gui.assembler.GuiAssembler;
import teamreborn.reborncore.gui.assembler.IDynamicAdjustmentGUI;

import java.io.IOException;

/**
 * File Created by Prospector.
 */
public class GuiManual extends GuiScreen implements IDynamicAdjustmentGUI {
	public static int xSize = 0;
	public static int ySize = 0;
	public int guiLeft;
	public int guiTop;
	public int xFactor;
	public int yFactor;
	GuiAssembler assembler = new GuiAssembler();

	public GuiManual() {

	}

	@Override
	public void initGui() {
		super.initGui();
		xSize = 350;
		ySize = 201;
		this.guiLeft = this.width / 2 - this.xSize / 2;
		this.guiTop = this.height / 2 - this.ySize / 2;
	}

	@Override
	public void drawBackground(int tint) {
		super.drawBackground(tint);
		xFactor = 0;
		yFactor = 0;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		xFactor = guiLeft;
		yFactor = guiTop;
		drawDefaultBackground();
		assembler.drawDefaultBackground(this, 0, 0, xSize, ySize);
		assembler.drawRect(this, 6, 6, xSize - 12, 18 - 6, 0xFFA1A1A1);

	}

	@Override
	public void actionPerformed(final GuiButton button) throws IOException {
		super.actionPerformed(button);
/*		if (button.id == 0) {
			currentPage = previousPage;
		}
		if (button.id == 2) {
			currentPage = homePage;
		}*/
	}

	@Override
	public int getOffsetFactorX() {
		return xFactor;
	}

	@Override
	public int getOffsetFactorY() {
		return yFactor;
	}
}
