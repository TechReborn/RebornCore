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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import reborncore.api.IListInfoProvider;
import reborncore.client.RenderUtil;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.widget.GuiButtonSimple;
import reborncore.common.fluid.FluidUtil;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;
import reborncore.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class GuiBuilder {
	public static final Identifier defaultTextureSheet = new Identifier("reborncore", "textures/gui/guielements.png");
	static Identifier resourceLocation;

	public GuiBuilder() {
		GuiBuilder.resourceLocation = defaultTextureSheet;
	}

	public GuiBuilder(Identifier resourceLocation) {
		GuiBuilder.resourceLocation = resourceLocation;
	}
	
	public Identifier getResourceLocation() {
		return resourceLocation;
	}

	public void drawDefaultBackground(Screen gui, int x, int y, int width, int height) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 0, 0, width / 2, height / 2);
		gui.blit(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
		gui.blit(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
		gui.blit(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2,
			height / 2);
	}

	public void drawEnergyBar(GuiBase<?> gui, int x, int y, int height, int energyStored, int maxEnergyStored, int mouseX, int mouseY, String powerType) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(resourceLocation);

		gui.blit(x, y, 0, 150, 14, height);
		gui.blit(x, y + height - 1, 0, 255, 14, 1);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
		gui.blit(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

		if (gui.isPointInRect(x, y, 14, height, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			list.add(energyStored + " / " + maxEnergyStored + " " + powerType);
			gui.renderTooltip(list, mouseX, mouseY);
		}
	}

	public void drawPlayerSlots(Screen gui, int posX, int posY, boolean center) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(resourceLocation);

		if (center) {
			posX -= 81;
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				gui.blit(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
			}
		}

		for (int x = 0; x < 9; x++) {
			gui.blit(posX + x * 18, posY + 58, 150, 0, 18, 18);
		}
	}

	public void drawSlot(Screen gui, int posX, int posY) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.blit(posX, posY, 150, 0, 18, 18);
	}

	public void drawString(GuiBase<?> gui, String string, int x, int y) {
		gui.getTextRenderer().draw(string, x, y, 16777215);
	}

	public void drawString(GuiBase<?> gui, String string, int x, int y, int color) {
		gui.getTextRenderer().draw(string, x, y, color);
	}

	public void drawProgressBar(GuiBase<?> gui, double progress, int x, int y) {
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 150, 18, 22, 15);
		int j = (int) (progress);
		if (j > 0) {
			gui.blit(x, y, 150, 34, j + 1, 15);
		}
	}

	public void drawOutputSlot(GuiBase<?> gui, int x, int y) {
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 174, 0, 26, 26);
	}

	/**
	 * Draws button with JEI icon in the given coords.
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place button
	 * @param y int Top left corner where to place button
	 * @param layer Layer Layer to draw on
	 */
	public void drawJEIButton(GuiBase<?> gui, int x, int y, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (FabricLoader.getInstance().isModLoaded("jei")) {
			if (layer == GuiBase.Layer.BACKGROUND) {
				x += gui.getGuiLeft();
				y += gui.getGuiTop();
			}
			gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
			gui.blit(x, y, 202, 0, 12, 12);
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
	public void drawLockButton(GuiBase<?> gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer, boolean locked) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 174, 26 + (locked ? 12 : 0), 20, 12);
		if (gui.isPointInRect(x, y, 20, 12, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			if (locked) {
				list.add(StringUtils.t("reborncore.gui.tooltip.unlock_items"));
			} else {
				list.add(StringUtils.t("reborncore.gui.tooltip.lock_items"));
			}
			RenderSystem.pushMatrix();
			gui.renderTooltip(list, mouseX, mouseY);
			RenderSystem.popMatrix();
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
	public void drawHologramButton(GuiBase<?> gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		if (gui.getMachine().renderMultiblock == null) {
			gui.blit(x, y, 174, 50, 20, 12);
		} else {
			gui.blit(x, y, 174, 62, 20, 12);
		}
		if (gui.isPointInRect(x, y, 20, 12, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.hologram"));
			RenderSystem.pushMatrix();
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.renderTooltip(list, mouseX, mouseY);
			RenderSystem.popMatrix();
		}
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
	public void drawBigHeatBar(GuiBase<?> gui, int x, int y, int value, int max, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 26, 218, 114, 18);
		if (value != 0) {
			int j = (int) ((double) value / (double) max * 106);
			if (j < 0) {
				j = 0;
			}
			gui.blit(x + 4, y + 4, 26, 246, j, 10);
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
	public void drawBigBlueBar(GuiBase<?> gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, String line2, String format, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		int j = (int) ((double) value / (double) max * 106);
		if (j < 0) {
			j = 0;
		}
		gui.blit(x + 4, y + 4, 0, 236, j, 10);
		if (!suffix.equals("")) {
			suffix = " " + suffix;
		}
		gui.drawCentredString(format + suffix, y + 5, 0xFFFFFF, layer);
		if (gui.isPointInRect(x, y, 114, 18, mouseX, mouseY)) {
			int percentage = percentage(max, value);
			List<String> list = new ArrayList<>();
			list.add("" + Formatting.GOLD + value + "/" + max + suffix);
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + Formatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.dsu_fullness"));
			list.add(line2);

			if (value > max) {
				list.add(Formatting.GRAY + "Yo this is storing more than it should be able to");
				list.add(Formatting.GRAY + "prolly a bug");
				list.add(Formatting.GRAY + "pls report and tell how tf you did this");
			}
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
		}
	}

	public void drawBigBlueBar(GuiBase<?> gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, GuiBase.Layer layer) {
		drawBigBlueBar(gui, x, y, value, max, mouseX, mouseY, suffix, "", Integer.toString(value), layer);

	}

	public void drawBigBlueBar(GuiBase<?> gui, int x, int y, int value, int max, int mouseX, int mouseY, GuiBase.Layer layer) {
		drawBigBlueBar(gui, x, y, value, max, mouseX, mouseY, "", "", "", layer);
	}

	/**
	 * Shades GUI and draw gray bar on top of GUI
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param layer Layer Layer to draw on
	 */
	public void drawMultiblockMissingBar(GuiBase<?> gui, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		int x = 0;
		int y = 4;
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		RenderSystem.disableLighting();
		RenderSystem.enableDepthTest();
		RenderSystem.colorMask(true, true, true, false);
		RenderUtil.drawGradientRect(0, x, y, x + 176, y + 20, 0x000000, 0xC0000000);
		RenderUtil.drawGradientRect(0, x, y + 20, x + 176, y + 20 + 48, 0xC0000000, 0xC0000000);
		RenderUtil.drawGradientRect(0, x, y + 68, x + 176, y + 70 + 20, 0xC0000000, 0x00000000);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.disableDepthTest();
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
	public void drawUpgrades(GuiBase<?> gui, int x, int y) {
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 217, 0, 24, 81);
	}

	/**
	 * Draws tab on the left side of machine GUI. Draws on the background level.
	 *
	 * @param gui GuiBase GUI to draw on
	 * @param x int Top left corner where to place tab
	 * @param y int Top left corner where to place tab
	 * @param stack ItemStack Item to show as tab icon
	 */
	public void drawSlotTab(GuiBase<?> gui, int x, int y, ItemStack stack) {
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 217, 82, 24, 24);
		gui.getMinecraft().getItemRenderer().renderGuiItem(stack, x + 5, y + 4);
	}

	// This stuff is WIP
	public void drawSlotConfigTips(GuiBase<?> gui, int posX, int posY, int mouseX, int mouseY) {
		List<String> tips = new ArrayList<>();
		tips.add(StringUtils.t("reborncore.gui.slotconfigtip.slot"));
		tips.add(StringUtils.t("reborncore.gui.slotconfigtip.side1"));
        tips.add(StringUtils.t("reborncore.gui.slotconfigtip.side2"));
        tips.add(StringUtils.t("reborncore.gui.slotconfigtip.side3"));
		tips.add(StringUtils.t("reborncore.gui.slotconfigtip.copy1"));
        tips.add(StringUtils.t("reborncore.gui.slotconfigtip.copy2"));
		TipsListWidget explanation = new TipsListWidget(gui, gui.getContainerWidth() - 14, 54, posY, posY + 76, 9 + 2, tips);
		explanation.setLeftPos(posX - 81);
		explanation.render(mouseX, mouseY, 1.0f);
		RenderSystem.color4f(1, 1, 1, 1);
	}


	private class TipsListWidget extends EntryListWidget<TipsListWidget.TipsListEntry> {

		public TipsListWidget(GuiBase<?> gui, int width, int height, int top, int bottom, int entryHeight, List<String> tips) {
			super(gui.getMinecraft(), width, height, top, bottom, entryHeight);
			for (String tip : tips){
				this.addEntry(new TipsListEntry(tip));
			}
		}

		@Override
		public int getRowWidth() {
			return 162;
		}

		@Override
		protected void renderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom){}

		private class TipsListEntry extends EntryListWidget.Entry<TipsListWidget.TipsListEntry> {
			private String tip;

			public TipsListEntry(String tip){
				this.tip = tip;
			}

			@Override
			public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
				MinecraftClient.getInstance().textRenderer.drawTrimmed(tip, x, y, width, 11184810);
			}
		}
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
	public void drawEnergyOutput(GuiBase<?> gui, int x, int y, int maxOutput, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		String text = PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxOutput) + " "
			+ PowerSystem.getDisplayPower().abbreviation + "/t";
		int width = gui.getTextRenderer().getStringWidth(text);
		gui.drawString(text, x - width - 2, y + 5, 0, layer);
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 150, 91, 16, 16);
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
	public void drawProgressBar(GuiBase<?> gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY, ProgressDirection direction, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}

		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, direction.x, direction.y, direction.width, direction.height);
		int j = (int) ((double) progress / (double) maxProgress * 16);
		if (j < 0) {
			j = 0;
		}

		switch (direction) {
			case RIGHT:
				gui.blit(x, y, direction.xActive, direction.yActive, j, 10);
				break;
			case LEFT:
				gui.blit(x + 16 - j, y, direction.xActive + 16 - j, direction.yActive, j, 10);
				break;
			case UP:
				gui.blit(x, y + 16 - j, direction.xActive, direction.yActive + 16 - j, 10, j);
				break;
			case DOWN:
				gui.blit(x, y, direction.xActive, direction.yActive, 10, j);
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
			gui.renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
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
	public void drawMultiEnergyBar(GuiBase<?> gui, int x, int y, int energyStored, int maxEnergyStored, int mouseX,
			int mouseY, int buttonID, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}

		EnergySystem displayPower = PowerSystem.getDisplayPower();
		MinecraftClient.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, displayPower.xBar - 15, displayPower.yBar - 1, 14, 50);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (48));
		if (energyStored > maxEnergyStored) {
			draw = 48;
		}
		gui.blit(x + 1, y + 49 - draw, displayPower.xBar, 48 + displayPower.yBar - draw, 12, draw);
		int percentage = percentage(maxEnergyStored, energyStored);
		if (gui.isPointInRect(x + 1, y + 1, 11, 48, mouseX, mouseY)) {
			List<Text> list = Lists.newArrayList();
			list.add(new LiteralText(PowerSystem.getLocaliszedPowerFormattedNoSuffix(energyStored) + "/"
					+ PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxEnergyStored) + " "
					+ displayPower.abbreviation).formatted(Formatting.GOLD));
			list.add(new LiteralText(StringUtils.getPercentageColour(percentage) + "" + percentage + "%"
					+ Formatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.power_charged")));
			if (gui.be instanceof IListInfoProvider) {
				if (Screen.hasShiftDown()) {
					((IListInfoProvider) gui.be).addInfo(list, true, true);
				} else {
					list.add(new LiteralText(""));
					list.add((new LiteralText(Formatting.BLUE + "Shift" + Formatting.GRAY + " "
							+ StringUtils.t("reborncore.gui.tooltip.power_moreinfo"))));
				}
			}
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			List<String> list1 = Lists.newArrayList();

			for (Text itextcomponent : list) {
				list1.add(itextcomponent.asFormattedString());
			}
			gui.renderTooltip(list1, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
		}
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
	public void drawTank(GuiBase<?> gui, int x, int y, int mouseX, int mouseY, FluidInstance fluid, FluidValue maxCapacity, boolean isTankEmpty, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}

		int percentage = 0;
		FluidValue amount = FluidValue.EMPTY;
		if (!isTankEmpty) {
			amount = fluid.getAmount();
			percentage = percentage(maxCapacity.getRawValue(), amount.getRawValue());
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 194, 26, 22, 56);
		if (!isTankEmpty) {
			drawFluid(gui, fluid, x + 4, y + 4, 14, 48, maxCapacity.getRawValue());
		}
		gui.blit(x + 3, y + 3, 194, 82, 16, 50);

		if (gui.isPointInRect(x, y, 22, 56, mouseX, mouseY)) {
			List<String> list = new ArrayList<>();
			if (isTankEmpty) {
				list.add(Formatting.GOLD + StringUtils.t("reborncore.gui.tooltip.tank_empty"));
			} else {
				list.add(Formatting.GOLD + String.format("%s / %s", amount, maxCapacity) + " " + FluidUtil.getFluidName(fluid));
			}
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + Formatting.GRAY + " "
				+ StringUtils.t("reborncore.gui.tooltip.tank_fullness"));
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
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
	public void drawFluid(GuiBase<?> gui, FluidInstance fluid, int x, int y, int width, int height, int maxCapacity) {
		if(fluid.getFluid() == Fluids.EMPTY){
			return;
		}
		gui.getMinecraft().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		y += height;
		final Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid()).getFluidSprites(gui.getMachine().getWorld(), gui.getMachine().getPos(), fluid.getFluid().getDefaultState())[0];
		int color = FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid()).getFluidColor(gui.getMachine().getWorld(), gui.getMachine().getPos(), fluid.getFluid().getDefaultState());

		final int drawHeight = (int) (fluid.getAmount().getRawValue() / (maxCapacity * 1F) * height);
		final int iconHeight = sprite.getHeight();
		int offsetHeight = drawHeight;

		RenderSystem.color3f((color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F);

		int iteration = 0;
		while (offsetHeight != 0) {
			final int curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;

			DrawableHelper.blit(x, y - offsetHeight, 0,  width, curHeight, sprite);
			offsetHeight -= curHeight;
			iteration++;
			if (iteration > 50) {
				break;
			}
		}
		RenderSystem.color3f(1F, 1F, 1F);

		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
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
	public void drawBurnBar(GuiBase<?> gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
		if (GuiBase.slotConfigType != GuiBase.SlotConfigType.NONE) {
			return;
		}
		if (layer == GuiBase.Layer.BACKGROUND) {
			x += gui.getGuiLeft();
			y += gui.getGuiTop();
		}
		gui.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 150, 64, 13, 13);
		int j = 13 - (int) ((double) progress / (double) maxProgress * 13);
		if (j > 0) {
			gui.blit(x, y + j, 150, 51 + j, 13, 13 - j);

		}
		if (gui.isPointInRect(x, y, 12, 12, mouseX, mouseY)) {
			int percentage = percentage(maxProgress, progress);
			List<String> list = new ArrayList<>();
			list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%");
			if (layer == GuiBase.Layer.FOREGROUND) {
				mouseX -= gui.getGuiLeft();
				mouseY -= gui.getGuiTop();
			}
			gui.renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
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
	public void drawOutputSlotBar(GuiBase<?> gui, int x, int y, int count) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(resourceLocation);
		gui.blit(x, y, 150, 122, 3, 26);
		x += 3;
		for (int i = 1; i <= count; i++) {
			gui.blit(x, y, 150 + 3, 122, 20, 26);
			x += 20;
		}
		gui.blit(x, y, 150 + 23, 122, 3, 26);
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
