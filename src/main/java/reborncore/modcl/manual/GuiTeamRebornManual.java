package reborncore.modcl.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import reborncore.modcl.manual.pages.ManualPage;
import reborncore.modcl.manual.pages.PageHome;
import reborncore.modcl.manual.widgets.GuiSmallButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prospector
 */
public class GuiTeamRebornManual extends GuiScreen {
	public int xSize = 350;
	public int ySize = 200;
	public int guiLeft;
	public int guiTop;
	ManualBuilder builder = new ManualBuilder();
	ManualPage currentPage;
	ManualPage previousPage;
	GuiSmallButton backButton = new GuiSmallButton(0, 0, 0, "< Back");
	GuiSmallButton nextButton = new GuiSmallButton(1, 0, 0, "Next >");
	GuiSmallButton homeButton = new GuiSmallButton(2, 0, 0, "") {
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {}

		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {}
	};

	public GuiTeamRebornManual() {

	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = this.width / 2 - this.xSize / 2;
		this.guiTop = this.height / 2 - this.ySize / 2;
		currentPage = new PageHome();
		previousPage = new PageHome();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawDefaultBackground();
		builder.drawDefaultBackground(this, guiLeft, guiTop, xSize, ySize);
		drawGradientRect(guiLeft + 6, 6 + guiTop, 344 + guiLeft, guiTop + 18, 0xFFA1A1A1, 0xFFA1A1A1);
		backButton.height = 12;
		backButton.setWidth(mc.fontRendererObj.getStringWidth(backButton.displayString) + 8);
		backButton.xPosition = guiLeft + 6;
		backButton.yPosition = guiTop + 6;
		backButton.drawButton(mc, mouseX, mouseY);

		nextButton.height = 12;
		nextButton.setWidth(mc.fontRendererObj.getStringWidth(nextButton.displayString) + 8);
		nextButton.enabled = false;
		nextButton.xPosition = guiLeft + 345 - nextButton.width;
		nextButton.yPosition = guiTop + 6;
		nextButton.drawButton(mc, mouseX, mouseY);

		homeButton.height = 10;
		homeButton.setWidth(mc.fontRendererObj.getStringWidth(currentPage.title()) + 2);
		homeButton.xPosition = guiLeft + (xSize / 2 - mc.fontRendererObj.getStringWidth(currentPage.title()) / 2) - 1;
		homeButton.yPosition = guiTop + 7;
		homeButton.drawButton(mc, mouseX, mouseY);
		if (homeButton.isMouseOver()) {
			List<String> list = new ArrayList<>();
			list.add("Return to Home Screen");
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, width, height, -1, mc.fontRendererObj);
			GlStateManager.disableLighting();
			GlStateManager.color(1, 1, 1, 1);
		}
		drawCentredStringShadow(currentPage.title(), 8, 0xFFFFFFFF);

	}

	protected void drawCentredString(String string, int y, int colour) {
		drawString(string, (xSize / 2 - mc.fontRendererObj.getStringWidth(string) / 2), y, colour);
	}

	protected void drawCentredStringShadow(String string, int y, int colour) {
		drawStringShadow(string, (xSize / 2 - mc.fontRendererObj.getStringWidth(string) / 2), y, colour);
	}

	protected void drawString(String string, int x, int y, int colour) {
		mc.fontRendererObj.drawString(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}

	protected void drawStringShadow(String string, int x, int y, int colour) {
		mc.fontRendererObj.drawStringWithShadow(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}
}
