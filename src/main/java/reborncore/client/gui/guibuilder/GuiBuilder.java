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

package reborncore.client.gui.guibuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.config.GuiUtils;
import reborncore.ClientProxy;
import reborncore.api.IListInfoProvider;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.widget.GuiButtonSimple;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;
import reborncore.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class GuiBuilder {
	public static final ResourceLocation defaultTextureSheet = new ResourceLocation("reborncore", "textures/gui/guielements.png");
	static ResourceLocation resourceLocation;

	public GuiBuilder() {
		GuiBuilder.resourceLocation = defaultTextureSheet;
	}

	public GuiBuilder(ResourceLocation resourceLocation) {
		GuiBuilder.resourceLocation = resourceLocation;
	}

	public void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2,
			height / 2);
	}

	public void drawEnergyBar(GuiBase gui, int x, int y, int height, int energyStored, int maxEnergyStored, int mouseX, int mouseY, String powerType) {
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);

		gui.drawTexturedModalRect(x, y, 0, 150, 14, height);
		gui.drawTexturedModalRect(x, y + height - 1, 0, 255, 14, 1);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
		gui.drawTexturedModalRect(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

		if (gui.isPointInRect(x, y, 14, height, mouseX, mouseY)) {
			List<String> list = new ArrayList<String>();
			list.add(energyStored + " / " + maxEnergyStored + " " + powerType);
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height,
				-1, gui.mc.fontRenderer);
		}
	}

	public void drawPlayerSlots(GuiScreen gui, int posX, int posY, boolean center) {
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);

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
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(posX, posY, 150, 0, 18, 18);
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

	public void drawOutputSlot(GuiScreen gui, int x, int y) {
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 174, 0, 26, 26);
	}

	public void drawInfoButton(int buttonID, int x, int y, List<GuiButton> buttonList) {
		buttonList.add(new GuiButtonSimple(0, x, y, 20, 20, "i"));
	}

	public void handleInfoButtonClick(int buttonID, List<GuiButton> buttonList) {
		// buttonList.get(buttonID).
	}

	public void drawInfo(GuiScreen gui, int x, int y, int height, int width, boolean draw) {
		if (draw) {
			Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
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
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (ModList.get().isLoaded("jei")) {
			if (layer == GuiBase.Layer.BACKGROUND) {
				x += gui.getGuiLeft();
				y += gui.getGuiTop();
			}
			gui.mc.getTextureManager().bindTexture(resourceLocation);
			gui.drawTexturedModalRect(x, y, 202, 0, 12, 12);
		}
	}

	/**
	 * Draws lock button in either locked or unlocked state
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
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 174, 26 + (locked ? 12 : 0), 20, 12);
		if (gui.isPointInRect(x, y, 20, 12, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			if (locked) {
				list.add(StringUtils.t("reborncore.gui.tooltip.unlock_items"));
			} else {
				list.add(StringUtils.t("reborncore.gui.tooltip.lock_items"));
			}
			GlStateManager.pushMatrix();
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.popMatrix();
		}
	}

	/**
	 * Draws hologram toggle button
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param layer Layer Layer to draw on
	 */
	public void drawHologramButton(GuiBase gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		if (ClientProxy.multiblockRenderEvent.currentMultiblock == null) {
			gui.drawTexturedModalRect(x, y, 174, 50, 20, 12);
		} else {
			gui.drawTexturedModalRect(x, y, 174, 62, 20, 12);
		}
		if (gui.isPointInRect(x, y, 20, 12, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.hologram"));
			GlStateManager.pushMatrix();
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.popMatrix();
		}
	}

	/**
	 * Draws four buttons in a raw to increase or decrease values
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param layer Layer Layer to draw on
	 */
	public void drawUpDownButtons(GuiBase gui, int x, int y, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 174, 74, 12, 12);
		gui.drawTexturedModalRect(x + 12, y, 174, 86, 12, 12);
		gui.drawTexturedModalRect(x + 24, y, 174, 98, 12, 12);
		gui.drawTexturedModalRect(x + 36, y, 174, 110, 12, 12);
	}

	/**
	 * Draws big horizontal bar for heat value
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place bar
	 * @param y int Top left corner where to place bar
	 * @param value int Current heat value
	 * @param max int Maximum heat value
	 * @param layer Layer Layer to draw on
	 */
	public void drawBigHeatBar(GuiBase gui, int x, int y, int value, int max, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 26, 218, 114, 18);
		if (value != 0) {
			int j = (int) ((double) value / (double) max * 106);
			if (j < 0) {
				j = 0;
			}
			gui.drawTexturedModalRect(x + 4, y + 4, 26, 246, j, 10);
			gui.drawCentredString(value + StringUtils.t("reborncore.gui.heat"), y + 5, 0xFFFFFF, layer);
		}
	}

	/**
	 * Draws big horizontal blue bar
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place bar
	 * @param y int Top left corner where to place bar
	 * @param value int Current value
	 * @param max int Maximum value
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param suffix String String to put on the bar and tooltip after percentage value
	 * @param line2 String String to put into tooltip as a second line
	 * @param format String Formatted value to put on the bar
	 * @param layer Layer Layer to draw on
	 */
	public void drawBigBlueBar(GuiBase gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, String line2, String format, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		int j = (int) ((double) value / (double) max * 106);
		if (j < 0) {
			j = 0;
		}
		gui.drawTexturedModalRect(x + 4, y + 4, 0, 236, j, 10);
		if (!suffix.equals("")) {
			suffix = " " + suffix;
		}
		gui.drawCentredString(format + suffix, y + 5, 0xFFFFFF, layer);
		if (gui.isPointInRect(x, y, 114, 18, mouseX, mouseY)) {
			int percentage = percentage(max, value);
			List<String> list = new ArrayList<>();
			list.add("" + TextFormatting.GOLD + value + "/" + max + suffix);
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.dsu_fullness"));
			list.add(line2);

			if (value > max) {
				list.add(TextFormatting.GRAY + "Yo this is storing more than it should be able to");
				list.add(TextFormatting.GRAY + "prolly a bug");
				list.add(TextFormatting.GRAY + "pls report and tell how tf you did this");
			}
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
	}

	public void drawBigBlueBar(GuiBase gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, GuiBase.Layer layer) {
		drawBigBlueBar(gui, x, y, value, max, mouseX, mouseY, suffix, "", Integer.toString(value), layer);

	}

	public void drawBigBlueBar(GuiBase gui, int x, int y, int value, int max, int mouseX, int mouseY, GuiBase.Layer layer) {
		drawBigBlueBar(gui, x, y, value, max, mouseX, mouseY, "", "", "", layer);
	}

	/**
	 * Shades GUI and draw gray bar on top of GUI
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param layer Layer Layer to draw on
	 */
	public void drawMultiblockMissingBar(GuiBase gui, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		int x = 0;
		int y = 4;
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		GlStateManager.disableLighting();
		GlStateManager.enableDepthTest();
		GlStateManager.colorMask(true, true, true, false);
		GuiUtils.drawGradientRect(0, x, y, x + 176, y + 20, 0x000000, 0xC0000000);
		GuiUtils.drawGradientRect(0, x, y + 20, x + 176, y + 20 + 48, 0xC0000000, 0xC0000000);
		GuiUtils.drawGradientRect(0, x, y + 68, x + 176, y + 70 + 20, 0xC0000000, 0x00000000);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.disableDepthTest();
		gui.drawCentredString(StringUtils.t("reborncore.gui.missingmultiblock"), 43, 0xFFFFFF, layer);
	}

	/**
	 * Draws upgrade slots on the left side of machine GUI. Draws on the background
	 * level.
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place slots
	 * @param y int Top left corner where to place slots
	 */
	public void drawUpgrades(GuiBase gui, int x, int y) {
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 217, 0, 24, 81);
	}

	/**
	 * Draws tab on the left side of machine GUI. Draws on the background level.
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place tab
	 * @param y int Top left corner where to place tab
	 * @param stack ItemStack Item to show as tab icon
	 */
	public void drawSlotTab(GuiBase gui, int x, int y, ItemStack stack) {
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 217, 82, 24, 24);
		gui.mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 5, y + 4);
	}

	// This stuff is WIP
	public void drawSlotTabExpanded(GuiScreen gui, int posX, int posY, int mouseX, int mouseY, boolean upgrades,
	                                ItemStack stack) {
		int offset = -1;
		if (!upgrades) {
			offset = 80;
		}
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(posX - 79, posY + 84 - offset, 0, 0, 80, 4);
		gui.drawTexturedModalRect(posX - 79, posY + 88 - offset, 0, 4, 80, 72);
		gui.drawTexturedModalRect(posX - 79, posY + 160 - offset, 0, 146, 80, 4);
		//		renderItemStack(stack, posX - 19, posY + 92 - offset);
		//		String explanation = "Click on slot to configure.\r\n";
		//		explanation += "Orange side means output, blue side means input.\r\n";
		//		explanation += "Ctrl+C to copy slot config, Ctrl+V to paste slot config.";
		//		gui.mc.fontRenderer.drawSplitString(explanation, posX - 75, posY + 108 - offset, 72, 4210752);
		//	TipsList explanation = new TipsList(gui, 75, 76, posY + 108 - offset, posY + 182 - offset, posX - 75, 10);
		//	explanation.drawScreen(mouseX, mouseY, 1.0f);
		GlStateManager.color4f(1, 1, 1, 1);
	}

	/**
	 * Draws energy output value and icon
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place energy output
	 * @param y int Top left corner where to place energy output
	 * @param maxOutput int Energy output value
	 * @param layer Layer Layer to draw on
	 */
	public void drawEnergyOutput(GuiBase gui, int x, int y, int maxOutput, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		String text = PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxOutput) + " "
			+ PowerSystem.getDisplayPower().abbreviation + "/t";
		int width = gui.mc.fontRenderer.getStringWidth(text);
		gui.drawString(text, x - width, y + 5, 0, layer);
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 150, 91, 16, 17);
	}

	/**
	 * Draws progress arrow in direction specified.
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param progress int Current progress
	 * @param maxProgress int Maximum progress
	 * @param x int Top left corner where to place progress arrow
	 * @param y int Top left corner where to place progress arrow
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param direction ProgressDirection Direction of progress arrow
	 * @param layer Layer Layer to draw on
	 */
	public void drawProgressBar(GuiBase gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY, ProgressDirection direction, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}

		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, direction.x, direction.y, direction.width, direction.height);
		int j = (int) ((double) progress / (double) maxProgress * 16);
		if (j < 0) {
			j = 0;
		}

		switch (direction) {
			case RIGHT:
				gui.drawTexturedModalRect(x, y, direction.xActive, direction.yActive, j, 10);
				break;
			case LEFT:
				gui.drawTexturedModalRect(x + 16 - j, y, direction.xActive + 16 - j, direction.yActive, j, 10);
				break;
			case UP:
				gui.drawTexturedModalRect(x, y + 16 - j, direction.xActive, direction.yActive + 16 - j, 10, j);
				break;
			case DOWN:
				gui.drawTexturedModalRect(x, y, direction.xActive, direction.yActive, 10, j);
				break;
			default:
				return;
		}

		if (gui.isPointInRect(x, y, direction.width, direction.height, mouseX, mouseY)) {
			int percentage = percentage(maxProgress, progress);
			List<String> list = new ArrayList<>();
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%");
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
	}

	/**
	 * Draws multi-energy bar
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place energy bar
	 * @param y int Top left corner where to place energy bar
	 * @param energyStored int Current amount of energy
	 * @param maxEnergyStored int Maximum amount of energy
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param buttonID int Button ID used to switch energy systems
	 * @param layer Layer Layer to draw on
	 */
	public void drawMultiEnergyBar(GuiBase gui, int x, int y, int energyStored, int maxEnergyStored, int mouseX, int mouseY, int buttonID, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}

		EnergySystem displayPower = PowerSystem.getDisplayPower();
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, displayPower.xBar - 15, displayPower.yBar - 1, 14, 50);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (48));
		if (energyStored > maxEnergyStored) {
			draw = 48;
		}
		gui.drawTexturedModalRect(x + 1, y + 49 - draw, displayPower.xBar, 48 + displayPower.yBar - draw, 12, draw);
		int percentage = percentage(maxEnergyStored, energyStored);
		if (gui.isPointInRect(x + 1, y + 1, 11, 48, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			TextFormatting powerColour = TextFormatting.GOLD;
			list.add(powerColour + PowerSystem.getLocaliszedPowerFormattedNoSuffix(energyStored) + "/"
				+ PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxEnergyStored) + " "
				+ displayPower.abbreviation);
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " "
				+ StringUtils.t("reborncore.gui.tooltip.power_charged"));
			if (gui.tile instanceof IListInfoProvider) {
				if (GuiScreen.isShiftKeyDown()) {
					((IListInfoProvider) gui.tile).addInfo(list, true, true);
					list.add("");
					list.add(TextFormatting.BLUE + StringUtils.t("reborncore.gui.tooltip.power_click"));
				} else {
					list.add("");
					list.add(TextFormatting.BLUE + "Shift" + TextFormatting.GRAY + " "
						+ StringUtils.t("reborncore.gui.tooltip.power_moreinfo"));
				}
			}
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
		gui.addPowerButton(x, y, buttonID, layer);
	}

	/**
	 * Draws tank and fluid inside it
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner of tank
	 * @param y int Top left corner of tank
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param fluid FluidStack Fluid to draw in tank
	 * @param maxCapacity int Maximum tank capacity
	 * @param isTankEmpty boolean True if tank is empty
	 * @param layer Layer Layer to draw on
	 */
	public void drawTank(GuiBase gui, int x, int y, int mouseX, int mouseY, FluidStack fluid, int maxCapacity, boolean isTankEmpty, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}

		int percentage = 0;
		int amount = 0;
		if (!isTankEmpty) {
			amount = fluid.amount;
			percentage = percentage(maxCapacity, amount);
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 194, 26, 22, 56);
		if (!isTankEmpty) {
			drawFluid(gui, fluid, x + 4, y + 4, 14, 48, maxCapacity);
		}
		gui.drawTexturedModalRect(x + 3, y + 3, 194, 82, 16, 50);

		if (gui.isPointInRect(x, y, 22, 56, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			if (isTankEmpty) {
				list.add(TextFormatting.GOLD + StringUtils.t("reborncore.gui.tooltip.tank_empty"));
			} else {
				list.add(TextFormatting.GOLD + StringUtils.t("reborncore.gui.tooltip.tank_amount", amount, maxCapacity) + " " + fluid.getLocalizedName());
			}
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " "
				+ StringUtils.t("reborncore.gui.tooltip.tank_fullness"));
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
	}

	/**
	 * Draws fluid in tank
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param fluid FluidStack Fluid to draw
	 * @param x int Top left corner of fluid
	 * @param y int Top left corner of fluid
	 * @param width int Width of fluid to draw
	 * @param height int Height of fluid to draw
	 * @param maxCapacity int Maximum capacity of tank
	 */
	public void drawFluid(GuiBase gui, FluidStack fluid, int x, int y, int width, int height, int maxCapacity) {
		gui.mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		y += height;
		final ResourceLocation still = fluid.getFluid().getStill(fluid);
		final TextureAtlasSprite sprite = gui.mc.getTextureMap().getAtlasSprite(still.toString());

		final int drawHeight = (int) (fluid.amount / (maxCapacity * 1F) * height);
		final int iconHeight = sprite.getHeight();
		int offsetHeight = drawHeight;

		int iteration = 0;
		while (offsetHeight != 0) {
			final int curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
			gui.drawTexturedModalRect(x, y - offsetHeight, sprite, width, curHeight);
			offsetHeight -= curHeight;
			iteration++;
			if (iteration > 50) {
				break;
			}
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
	}

	/**
	 * Draws burning progress, similar to vanilla furnace
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param progress int Current progress
	 * @param maxProgress int Maximum progress
	 * @param x int Top left corner where to place burn bar
	 * @param y int Top left corner where to place burn bar
	 * @param mouseX int Mouse cursor position to check for tooltip
	 * @param mouseY int Mouse cursor position to check for tooltip
	 * @param layer Layer Layer to draw on
	 */
	public void drawBurnBar(GuiBase gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.mc.getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 150, 64, 13, 13);
		int j = 13 - (int) ((double) progress / (double) maxProgress * 13);
		if (j > 0) {
			gui.drawTexturedModalRect(x, y + j, 150, 51 + j, 13, 13 - j);

		}
		if (gui.isPointInRect(x, y, 12, 12, mouseX, mouseY)) {
			int percentage = percentage(maxProgress, progress);
			List<String> list = new ArrayList<>();
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%");
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
	}

	/**
	 * Draws bar containing output slots
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place slots bar
	 * @param y int Top left corner where to place slots bar
	 * @param count int Number of output slots
	 */
	public void drawOutputSlotBar(GuiBase gui, int x, int y, int count) {
		Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 150, 122, 3, 26);
		x += 3;
		for (int i = 1; i <= count; i++) {
			gui.drawTexturedModalRect(x, y, 150 + 3, 122, 20, 26);
			x += 20;
		}
		gui.drawTexturedModalRect(x, y, 150 + 23, 122, 3, 26);
	}

	protected int percentage(int MaxValue, int CurrentValue) {
		if (CurrentValue == 0) {
			return 0;
		}
		return (int) ((CurrentValue * 100.0f) / MaxValue);
	}

	public enum ProgressDirection {
		RIGHT(58, 150, 74, 150, 16, 10),
		LEFT(74, 160, 58, 160, 16, 10),
		DOWN(78, 170, 88, 170, 10, 16),
		UP(58, 170, 68, 170, 10, 16);
		public int x;
		public int y;
		public int xActive;
		public int yActive;
		public int width;
		public int height;

		ProgressDirection(int x, int y, int xActive, int yActive, int width, int height) {
			this.x = x;
			this.y = y;
			this.xActive = xActive;
			this.yActive = yActive;
			this.width = width;
			this.height = height;
		}
	}
}
