/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
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

package reborncore.client.guibuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.ClientProxy;
import reborncore.client.RenderUtil;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class GuiBuilder {
	public static final ResourceLocation defaultTextureSheet = new ResourceLocation("reborncore", "textures/gui/guielements.png");
	static ResourceLocation resourceLocation;

	public GuiBuilder(ResourceLocation resourceLocation) {
		GuiBuilder.resourceLocation = resourceLocation;
	}

	public void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
	}

	public void drawEnergyBar(GuiScreen gui, int x, int y, int height, int energyStored, int maxEnergyStored, int mouseX, int mouseY, String powerType) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

		gui.drawTexturedModalRect(x, y, 0, 150, 14, height);
		gui.drawTexturedModalRect(x, y + height - 1, 0, 255, 14, 1);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
		gui.drawTexturedModalRect(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

		if (isInRect(x, y, 14, height, mouseX, mouseY)) {
			List<String> list = new ArrayList<String>();
			list.add(energyStored + " / " + maxEnergyStored + " " + powerType);
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
		}
	}

	public void drawPlayerSlots(GuiScreen gui, int posX, int posY, boolean center) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

		if (center) {
			posX -= 81;
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				gui.drawTexturedModalRect(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
			}
		}

		for (int x = 0; x < 9; x++) {
			gui.drawTexturedModalRect(posX + x * 18, posY + 58, 150, 0, 18, 18);
		}
	}

	public void drawSlot(GuiScreen gui, int posX, int posY) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(posX, posY, 150, 0, 18, 18);
	}

	public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	public void drawString(GuiScreen gui, String string, int x, int y) {
		gui.mc.fontRenderer.drawString(string, x, y, 16777215);
	}

	public void drawString(GuiScreen gui, String string, int x, int y, int color) {
		gui.mc.fontRenderer.drawString(string, x, y, color);
	}

	public void drawProgressBar(GuiScreen gui, double progress, int x, int y) {
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 150, 18, 22, 15);
		int j = (int) (progress);
		if (j > 0) {
			gui.drawTexturedModalRect(x, y, 150, 34, j + 1, 15);
		}
	}

	public void drawTank(GuiScreen gui, FluidTank tank, int x, int y, float zLevel, int width, int height, int mouseX, int mouseY) {
		RenderUtil.renderGuiTank(tank, x, y, zLevel, width, height);
		if (isInRect(x, y, 14, height, mouseX, mouseY)) {
			List<String> list = new ArrayList<String>();
			if (tank.getFluid() != null) {
				list.add(tank.getFluidAmount() + " / " + tank.getCapacity() + " " + tank.getFluid().getLocalizedName());
			} else {
				list.add("empty");
			}
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
		}
	}

	//TODO fix
	public void drawBurnBar(GuiScreen gui, double progress, int x, int y) {
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 150, 49, 22, 15);
		int j = (int) (progress);
		if (j > 0) {
			gui.drawTexturedModalRect(x, y, 150, 64, 15, j + 1);

		}
	}

	public void drawOutputSlot(GuiScreen gui, int x, int y) {
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 174, 0, 26, 26);
	}

	public void drawInfoButton(int buttonID, int x, int y, List<GuiButton> buttonList) {
		buttonList.add(new GuiButton(0, x, y, 20, 20, "i"));
	}

	public void handleInfoButtonClick(int buttonID, List<GuiButton> buttonList) {
		//        buttonList.get(buttonID).
	}

	public void drawInfo(GuiScreen gui, int x, int y, int height, int width, boolean draw) {
		if (draw) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
			gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
			gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
			gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
			gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
		}
	}
	
	/**
	 * Draws button with JEI icon in the given coords.
	 *  
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param layer Layer Layer to draw on
	 */
	public void drawJEIButton(GuiBase gui, int x, int y, GuiBase.Layer layer) {
		if(GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE){
			return;
		}
		if (Loader.isModLoaded("jei")) {
			if (layer == GuiBase.Layer.BACKGROUND) {
				x += gui.getGuiLeft();
				y += gui.getGuiTop();
			}
			gui.mc.getTextureManager().bindTexture(defaultTextureSheet);
			gui.drawTexturedModalRect(x, y, 202, 0, 12, 12);
		}
	}
	
	/**
	 *  Draws lock button in either locked or unlocked state
	 *  
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param layer Layer Layer to draw on
	 * @param locked boolean Set to true if it is in locked state
	 */
	public void drawLockButton(GuiBase gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer, boolean locked) {
		if(GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE){
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(defaultTextureSheet);
		gui.drawTexturedModalRect(x, y, 174, 26 + (locked ? 12 : 0) , 20, 12);
		if (isInRect(x, y, 20, 12, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			if(locked){
				list.add(StringUtils.t("reborncore.gui.tooltip.unlock_items"));
			} else {
				list.add(StringUtils.t("reborncore.gui.tooltip.lock_items"));
			}

			GlStateManager.pushMatrix();
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, 80, gui.mc.fontRenderer);
			GlStateManager.popMatrix();
		}
	}
	
	/**
	 *  Draws hologram toggle button
	 *  
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param layer Layer Layer to draw on
	 */
	public void drawHologramButton(GuiBase gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
		if(GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE){
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		if (layer == GuiBase.Layer.FOREGROUND) {
			mouseX -= gui.getGuiLeft();
			mouseY -= gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(defaultTextureSheet);
		if (ClientProxy.multiblockRenderEvent.currentMultiblock == null) {
			gui.drawTexturedModalRect(x, y, 174, 50, 20, 12);
		} else {
			gui.drawTexturedModalRect(x, y, 174, 62, 20, 12);
		}
		if (isInRect(x, y, 20, 12, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.hologram"));
			GlStateManager.pushMatrix();
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
			GlStateManager.popMatrix();
		}
	}
	
	/**
	 *  Draws four buttons in a raw to increase or decrease values
	 *  
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param layer Layer Layer to draw on
	 */
	public void drawUpDownButtons(GuiBase gui, int x, int y, GuiBase.Layer layer){
		if(GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE){
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(defaultTextureSheet);
		gui.drawTexturedModalRect(x, y, 174, 74, 12, 12);
		gui.drawTexturedModalRect(x + 12, y, 174, 86, 12, 12);
		gui.drawTexturedModalRect(x + 24, y, 174, 98, 12, 12);
		gui.drawTexturedModalRect(x + 36, y, 174, 110, 12, 12);
	}
	
}
