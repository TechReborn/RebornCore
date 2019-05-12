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

package reborncore.client.gui.builder;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.slot.GuiFluidConfiguration;
import reborncore.client.gui.builder.slot.GuiSlotConfiguration;
import reborncore.client.gui.builder.widget.GuiButtonHologram;
import reborncore.client.gui.builder.widget.GuiButtonPowerBar;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.common.container.RebornContainer;
import reborncore.common.tile.TileMachineBase;
import reborncore.common.util.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prospector
 */
public class GuiBase extends ContainerScreen {

	public int xSize = 176;
	public int ySize = 176;
	public GuiBuilder builder = new GuiBuilder();
	public BlockEntity tile;
	@Nullable
	public BuiltContainer container;
	public static SlotConfigType slotConfigType = SlotConfigType.NONE;
	public static ItemStack wrenchStack = ItemStack.EMPTY;
	public static FluidCellProvider fluidCellProvider = fluid -> ItemStack.EMPTY;

	public boolean upgrades;

	public GuiBase(PlayerEntity player, BlockEntity tile, BuiltContainer container) {
		super(container);
		this.tile = tile;
		this.container = container;
		slotConfigType = SlotConfigType.NONE;
	}

	public GuiBase(PlayerEntity player, BlockEntity tile, RebornContainer container) {
		super(container);
		this.tile = tile;
		this.container = null;
		slotConfigType = SlotConfigType.NONE;
	}

	protected void drawSlot(int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += left;
			y += top;
		}
		builder.drawSlot(this, x - 1, y - 1);
	}

	protected void drawOutputSlotBar(int x, int y, int count, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += left;
			y += top;
		}
		builder.drawOutputSlotBar(this, x - 4, y - 4, count);
	}

	protected void drawArmourSlots(int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += left;
			y += top;
		}
		builder.drawSlot(this, x - 1, y - 1);
		builder.drawSlot(this, x - 1, y - 1 + 18);
		builder.drawSlot(this, x - 1, y - 1 + 18 + 18);
		builder.drawSlot(this, x - 1, y - 1 + 18 + 18 + 18);
	}

	protected void drawOutputSlot(int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += left;
			y += top;
		}
		builder.drawOutputSlot(this, x - 5, y - 5);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (isConfigEnabled()) {
			GuiSlotConfiguration.init(this);
		}
		if (isConfigEnabled() && getMachine().getTank() != null && getMachine().showTankConfig()) {
			GuiFluidConfiguration.init(this);
		}
	}

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		builder.drawDefaultBackground(this, left, top, containerWidth, containerHeight);
		if (drawPlayerSlots()) {
			builder.drawPlayerSlots(this, left + containerWidth / 2, top + 93, true);
		}
		if (tryAddUpgrades() && tile instanceof IUpgradeable) {
			IUpgradeable upgradeable = (IUpgradeable) tile;
			if (upgradeable.canBeUpgraded()) {
				builder.drawUpgrades(this, left - 24, top + 6);
				upgrades = true;
			}
		}
		int offset = upgrades ? 86 : 6;
		if (isConfigEnabled() && getMachine().hasSlotConfig()) {
			builder.drawSlotTab(this, left - 24, top + offset, wrenchStack);
		}
		if (isConfigEnabled() && getMachine().showTankConfig()) {
			builder.drawSlotTab(this, left - 24, top + 24 + offset, fluidCellProvider.provide(null));//TODO 1.13 fluids FluidRegistry.LAVA));
		}
	}

	public boolean drawPlayerSlots() {
		return true;
	}

	public boolean tryAddUpgrades() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.buttons.clear();
		drawTitle();
		if (isConfigEnabled() && slotConfigType == SlotConfigType.ITEMS && getMachine().hasSlotConfig()) {
			GuiSlotConfiguration.draw(this, mouseX, mouseY);
		}

		if (isConfigEnabled() && slotConfigType == SlotConfigType.FLUIDS && getMachine().showTankConfig()) {
			GuiFluidConfiguration.draw(this, mouseX, mouseY);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawMouseoverTooltip(int mouseX, int mouseY) {
		if (isPointWithinBounds(-25, 6, 24, 80, mouseX, mouseY) && upgrades) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.upgrades"));
			drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
		int offset = upgrades ? 81 : 0;
		if (isConfigEnabled() && isPointWithinBounds(-26, 6 + offset, 24, 24, mouseX, mouseY) && getMachine().hasSlotConfig()) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.config_slots"));
			drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
		if (isConfigEnabled() && isPointWithinBounds(-26, 6 + offset + 25, 24, 24, mouseX, mouseY) && getMachine().showTankConfig()) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.config_fluids"));
			drawHoveringText(list, mouseX, mouseY);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
		}
		super.drawMouseoverTooltip(mouseX, mouseY);
	}

	protected void drawTitle() {
		drawCentredString(I18n.translate(tile.getCachedState().getBlock().getTranslationKey()), 6, 4210752, Layer.FOREGROUND);
	}

	public void drawCentredString(String string, int y, int colour, Layer layer) {
		drawString(string, (containerWidth / 2 - mc.fontRenderer.getStringWidth(string) / 2), y, colour, layer);
	}

	protected void drawCentredString(String string, int y, int colour, int modifier, Layer layer) {
		drawString(string, (containerWidth / 2 - (mc.fontRenderer.getStringWidth(string)) / 2) + modifier, y, colour, layer);
	}

	public void drawString(String string, int x, int y, int colour, Layer layer) {
		int factorX = 0;
		int factorY = 0;
		if (layer == Layer.BACKGROUND) {
			factorX = left;
			factorY = top;
		}
		mc.fontRenderer.drawString(string, x + factorX, y + factorY, colour);
		GlStateManager.color4f(1, 1, 1, 1);
	}

	public void addPowerButton(int x, int y, int id, Layer layer) {
		int factorX = 0;
		int factorY = 0;
		if (layer == Layer.BACKGROUND) {
			factorX = left;
			factorY = top;
		}
		buttons.add(new GuiButtonPowerBar(id, x + factorX, y + factorY, this, layer));
	}

	public GuiButtonHologram addHologramButton(int x, int y, int id, Layer layer) {
		int factorX = 0;
		int factorY = 0;
		if (layer == Layer.BACKGROUND) {
			factorX = left;
			factorY = top;
		}
		GuiButtonHologram buttonHologram = new GuiButtonHologram(id, x + factorX, y + factorY, this, layer);
		buttons.add(buttonHologram);
		return buttonHologram;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (isConfigEnabled() && slotConfigType == SlotConfigType.ITEMS && getMachine().hasSlotConfig()) {
			if (GuiSlotConfiguration.mouseClicked(mouseX, mouseY, mouseButton, this)) {
				return true;
			}
		}
		if (isConfigEnabled() && slotConfigType == SlotConfigType.FLUIDS && getMachine().showTankConfig()) {
			if (GuiFluidConfiguration.mouseClicked(mouseX, mouseY, mouseButton, this)) {
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	//	@Override
	//	protected void mouseClickMove(double mouseX, double mouseY, int clickedMouseButton, long timeSinceLastClick) {
	//		if (isConfigEnabled() && slotConfigType == SlotConfigType.ITEMS && getMachine().hasSlotConfig()) {
	//			GuiSlotConfiguration.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, this);
	//		}
	//		if (isConfigEnabled() && slotConfigType == SlotConfigType.FLUIDS && getMachine().showTankConfig()) {
	//			GuiFluidConfiguration.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, this);
	//		}
	//		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	//	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int state) {
		int offset = 0;
		if (!upgrades) {
			offset = 80;
		}
		if (isConfigEnabled() && isPointWithinBounds(-26, 84 - offset, 30, 30, mouseX, mouseY) && getMachine().hasSlotConfig()) {
			if (slotConfigType != SlotConfigType.ITEMS) {
				slotConfigType = SlotConfigType.ITEMS;
			} else {
				slotConfigType = SlotConfigType.NONE;
			}
			if (slotConfigType == SlotConfigType.ITEMS) {
				GuiSlotConfiguration.reset();
			}
		}
		if (isConfigEnabled() && isPointWithinBounds(-26, 84 - offset + 27, 30, 30, mouseX, mouseY) && getMachine().hasSlotConfig()) {
			if (slotConfigType != SlotConfigType.FLUIDS) {
				slotConfigType = SlotConfigType.FLUIDS;
			} else {
				slotConfigType = SlotConfigType.NONE;
			}
		}
		if (isConfigEnabled() && slotConfigType == SlotConfigType.ITEMS && getMachine().hasSlotConfig()) {
			if (GuiSlotConfiguration.mouseReleased(mouseX, mouseY, state, this)) {
				return true;
			}
		}
		if (isConfigEnabled() && slotConfigType == SlotConfigType.FLUIDS && getMachine().showTankConfig()) {
			if (GuiFluidConfiguration.mouseReleased(mouseX, mouseY, state, this)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
		if (isConfigEnabled() && slotConfigType == SlotConfigType.ITEMS) {
			if (isCtrlKeyDown() && keyCode == GLFW.GLFW_KEY_C) {
				GuiSlotConfiguration.copyToClipboard();
				return true;
			} else if (isCtrlKeyDown() && keyCode == GLFW.GLFW_KEY_V) {
				GuiSlotConfiguration.pasteFromClipboard();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
	}

	@Override
	public void onGuiClosed() {
		slotConfigType = SlotConfigType.NONE;
		super.onGuiClosed();
	}

	@Nullable
	public TileMachineBase getMachine() {
		return (TileMachineBase) tile;
	}

	/**
	 * @param rectX int Top left corner of region
	 * @param rectY int Top left corner of region
	 * @param rectWidth int Width of region
	 * @param rectHeight int Height of region
	 * @param pointX int Mouse pointer
	 * @param pointY int Mouse pointer
	 * @return boolean Returns true if mouse pointer is in region specified
	 * @see net.minecraft.client.gui.inventory.GuiContainer()
	 */
	public boolean isPointInRect(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY) {
		return super.isPointWithinBounds(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
	}

	public enum Layer {
		BACKGROUND, FOREGROUND
	}

	public enum SlotConfigType {
		NONE,
		ITEMS,
		FLUIDS
	}

	public interface FluidCellProvider {
		public ItemStack provide(Fluid fluid);
	}

	public boolean isConfigEnabled() {
		return tile instanceof TileMachineBase && container != null;
	}

	public int getGuiLeft(){
		return left;
	}

	public int getGuiTop(){
		return top;
	}

	public MinecraftClient getMinecraft(){
		return minecraft;
	}
}
