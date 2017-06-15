/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
	public static int xSize = 0;
	public static int ySize = 0;
	public int guiLeft;
	public int guiTop;
	ManualBuilder builder = new ManualBuilder();
	ManualPage homePage = new PageHome();
	ManualPage currentPage;
	ManualPage previousPage;
	ManualPage nextPage;
	GuiSmallButton backButton;
	GuiSmallButton nextButton;
	GuiInvisibutton homeButton;

	public GuiTeamRebornManual() {

	}

	@Override
	public void initGui() {
		super.initGui();
		xSize = 350;
		ySize = 201;
		this.guiLeft = this.width / 2 - this.xSize / 2;
		this.guiTop = this.height / 2 - this.ySize / 2;
		PageDescription desc = new PageDescription();
		desc.setTitle("Tech Reborn > Getting Started");
		desc.setDescription("First thing's first, to get started you must find a Rubber Tree in the world. They're more commonly found in swamps, but you can find them in most biomes that have trees. Craft a treetap and use it on a yellowish sap spot on a rubber tree to harvest it's sap. You can smelt the sap into rubber, which is used later on in things.");
		currentPage = desc;
		previousPage = homePage;
	}

	@Override
	public void drawBackground(int tint) {
		super.drawBackground(tint);
		drawDefaultBackground();
		builder.drawDefaultBackground(this, guiLeft, guiTop, xSize, ySize);
		drawGradientRect(guiLeft + 6, 6 + guiTop, xSize - 6 + guiLeft, guiTop + 18, 0xFFA1A1A1, 0xFFA1A1A1);

		backButton = new GuiSmallButton(0, guiLeft + 6, guiTop + 6, 0, 12, "< Back");
		nextButton = new GuiSmallButton(1, 0, guiTop + 6, 0, 12, "Next >");
		homeButton = new GuiInvisibutton(2, guiLeft + (xSize / 2 - mc.fontRenderer.getStringWidth(currentPage.title()) / 2) - 1, guiTop + 7, mc.fontRenderer.getStringWidth(currentPage.title()) + 2, 10);

		backButton.setWidth(mc.fontRenderer.getStringWidth(backButton.displayString) + 8);
		nextButton.width = mc.fontRenderer.getStringWidth(nextButton.displayString) + 8;
		nextButton.x = guiLeft + xSize - 5 - nextButton.width;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (previousPage.equals(currentPage)) {
			backButton.enabled = false;
		}
		if (currentPage.nextPage() == null) {
			nextButton.enabled = false;
		}

		if (homeButton.isMouseOver()) {
			List<String> list = new ArrayList<>();
			list.add("Return to Home Screen");
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, width, height, -1, mc.fontRenderer);
			GlStateManager.disableLighting();
			GlStateManager.color(1, 1, 1, 1);
		}

		backButton.drawButton(mc, mouseX, mouseY);

		drawCentredStringShadow(currentPage.title(), 8, 0xFFFFFFFF);

		currentPage.draw(mc, this);
	}

	public void drawCentredString(String string, int y, int colour) {
		drawString(string, (xSize / 2 - mc.fontRenderer.getStringWidth(string) / 2), y, colour);
	}

	public void drawCentredStringShadow(String string, int y, int colour) {
		drawStringShadow(string, (xSize / 2 - mc.fontRenderer.getStringWidth(string) / 2), y, colour);
	}

	public void drawString(String string, int x, int y, int colour) {
		mc.fontRenderer.drawString(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}

	public void drawStringShadow(String string, int x, int y, int colour) {
		mc.fontRenderer.drawStringWithShadow(string, x + guiLeft, y + guiTop, colour);
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
