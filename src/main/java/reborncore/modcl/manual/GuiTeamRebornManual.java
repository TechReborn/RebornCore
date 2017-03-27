package reborncore.modcl.manual;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import reborncore.modcl.manual.pages.ManualPage;
import reborncore.modcl.manual.pages.PageDescription;
import reborncore.modcl.manual.pages.PageHome;
import reborncore.modcl.manual.widgets.GuiInvisibutton;
import reborncore.modcl.manual.widgets.GuiSmallButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prospector
 */
public class GuiTeamRebornManual extends GuiScreen {
	public static int xSize = 350;
	public static int ySize = 201;
	public int guiLeft;
	public int guiTop;
	ManualBuilder builder = new ManualBuilder();
	ManualPage homePage = new PageHome();
	ManualPage currentPage;
	ManualPage previousPage;
	ManualPage nextPage;

	public GuiTeamRebornManual() {

	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = this.width / 2 - this.xSize / 2;
		this.guiTop = this.height / 2 - this.ySize / 2;
		PageDescription desc = new PageDescription();
		desc.setTitle("Tech Reborn > Getting Started");
		desc.setDescription("First thing's first, to get started you must find a Rubber Tree in the world. They're more commonly found in swamps, but you can find them in most biomes that have trees. Craft a treetap and use it on a yellowish sap spot on a rubber tree to harvest it's sap. You can smelt the sap into rubber, which is used later on in things.");
		currentPage = desc;
		previousPage = homePage;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		buttonList.clear();
		GuiSmallButton backButton = new GuiSmallButton(0, guiLeft + 6, guiTop + 6, 0, 12, "< Back");
		GuiSmallButton nextButton = new GuiSmallButton(1, 0, guiTop + 6, 0, 12, "Next >");
		GuiInvisibutton homeButton = new GuiInvisibutton(2, guiLeft + (xSize / 2 - mc.fontRendererObj.getStringWidth(currentPage.title()) / 2) - 1, guiTop + 7, mc.fontRendererObj.getStringWidth(currentPage.title()) + 2, 10);

		drawDefaultBackground();
		builder.drawDefaultBackground(this, guiLeft, guiTop, xSize, ySize);
		drawGradientRect(guiLeft + 6, 6 + guiTop, 344 + guiLeft, guiTop + 18, 0xFFA1A1A1, 0xFFA1A1A1);

		if (previousPage.equals(currentPage)) {
			backButton.enabled = false;
		}
		backButton.setWidth(mc.fontRendererObj.getStringWidth(backButton.displayString) + 8);
		backButton.drawButton(mc, mouseX, mouseY);
		buttonList.add(backButton);

		if (currentPage.nextPage() == null) {
			nextButton.enabled = false;
		}
		nextButton.width = mc.fontRendererObj.getStringWidth(nextButton.displayString) + 8;
		nextButton.xPosition = guiLeft + 345 - nextButton.width;
		nextButton.drawButton(mc, mouseX, mouseY);
		buttonList.add(nextButton);

		homeButton.drawButton(mc, mouseX, mouseY);
		buttonList.add(homeButton);

		if (homeButton.isMouseOver()) {
			List<String> list = new ArrayList<>();
			list.add("Return to Home Screen");
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, width, height, -1, mc.fontRendererObj);
			GlStateManager.disableLighting();
			GlStateManager.color(1, 1, 1, 1);
		}
		drawCentredStringShadow(currentPage.title(), 8, 0xFFFFFFFF);

		currentPage.draw(mc, this);
	}

	public void drawCentredString(String string, int y, int colour) {
		drawString(string, (xSize / 2 - mc.fontRendererObj.getStringWidth(string) / 2), y, colour);
	}

	public void drawCentredStringShadow(String string, int y, int colour) {
		drawStringShadow(string, (xSize / 2 - mc.fontRendererObj.getStringWidth(string) / 2), y, colour);
	}

	public void drawString(String string, int x, int y, int colour) {
		mc.fontRendererObj.drawString(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}

	public void drawStringShadow(String string, int x, int y, int colour) {
		mc.fontRendererObj.drawStringWithShadow(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public void actionPerformed(final GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 0) {
			currentPage = previousPage;
		}
		if (button.id == 2) {
			currentPage = homePage;
		}
	}
}