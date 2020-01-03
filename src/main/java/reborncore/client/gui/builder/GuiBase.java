/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.client.gui.builder;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import reborncore.api.blockentity.IUpgradeable;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.containerBuilder.builder.slot.PlayerInventorySlot;
import reborncore.client.gui.builder.slot.GuiFluidConfiguration;
import reborncore.client.gui.builder.slot.GuiSlotConfiguration;
import reborncore.client.gui.builder.widget.GuiButtonHologram;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.util.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Prospector
 */

public class GuiBase<T extends Container> extends AbstractContainerScreen<T> {

	public GuiBuilder builder = new GuiBuilder();
	public BlockEntity be;
	@Nullable
	public BuiltContainer builtContainer;
	public static SlotConfigType slotConfigType = SlotConfigType.NONE;
	public static ItemStack wrenchStack = ItemStack.EMPTY;
	public static FluidCellProvider fluidCellProvider = fluid -> ItemStack.EMPTY;
	private int xSize = 176;
	private int ySize = 176;

	public boolean upgrades;

	public GuiBase(PlayerEntity player, BlockEntity blockEntity, T container) {
		super(container, player.inventory, new LiteralText(I18n.translate(blockEntity.getCachedState().getBlock().getTranslationKey())));
		this.be = blockEntity;
		this.builtContainer = (BuiltContainer) container;
		slotConfigType = SlotConfigType.NONE;
	}
	
	public int getContainerWidth() {
		return containerWidth;
	}

	public void drawSlot(int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		builder.drawSlot(this, x - 1, y - 1);
	}

	public void drawOutputSlotBar(int x, int y, int count, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		builder.drawOutputSlotBar(this, x - 4, y - 4, count);
	}

	public void drawArmourSlots(int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		builder.drawSlot(this, x - 1, y - 1);
		builder.drawSlot(this, x - 1, y - 1 + 18);
		builder.drawSlot(this, x - 1, y - 1 + 18 + 18);
		builder.drawSlot(this, x - 1, y - 1 + 18 + 18 + 18);
	}

	public void drawOutputSlot(int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		builder.drawOutputSlot(this, x - 5, y - 5);
	}

	@Override
	public void init() {
		super.init();
		if (isConfigEnabled()) {
			GuiSlotConfiguration.init(this);
		}
		if (isConfigEnabled() && getMachine().getTank() != null && getMachine().showTankConfig()) {
			GuiFluidConfiguration.init(this);
		}
	}

	@Override
	protected void drawBackground(float lastFrameDuration, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		renderBackground();
		boolean drawPlayerSlots = (slotConfigType != SlotConfigType.ITEMS && slotConfigType != SlotConfigType.FLUIDS) && drawPlayerSlots();
        updateSlotDraw(drawPlayerSlots);
		builder.drawDefaultBackground(this, x, y, xSize, ySize);
		if (drawPlayerSlots) {
		    builder.drawPlayerSlots(this, x + containerWidth / 2, y + 93, true);
		}
		if (tryAddUpgrades() && be instanceof IUpgradeable) {
			IUpgradeable upgradeable = (IUpgradeable) be;
			if (upgradeable.canBeUpgraded()) {
				builder.drawUpgrades(this, x - 24, y + 6);
				upgrades = true;
			}
		}
		int offset = upgrades ? 86 : 6;
		if (isConfigEnabled() && getMachine().hasSlotConfig()) {
            builder.drawSlotTab(this, x - 24, y + offset, wrenchStack);
			if (slotConfigType == SlotConfigType.ITEMS ) {
				builder.drawSlotConfigTips(this, x + containerWidth / 2, y + 93, mouseX, mouseY);
			}
		}
		if (isConfigEnabled() && getMachine().showTankConfig()) {
			builder.drawSlotTab(this, x - 24, y + 24 + offset, fluidCellProvider.provide(Fluids.LAVA));
            if (slotConfigType == SlotConfigType.FLUIDS ) {
                builder.drawSlotConfigTips(this, x + containerWidth / 2, y + 93, mouseX, mouseY);
            }

        }
	}

	private void updateSlotDraw(boolean doDraw){
	    if (builtContainer == null){
	        return;
        }
	    for (Slot slot : builtContainer.slotList){
            if (slot instanceof PlayerInventorySlot) {
                ((PlayerInventorySlot) slot).doDraw = doDraw;
            }
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
		super.render(mouseX, mouseY, partialTicks);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawMouseoverTooltip(int mouseX, int mouseY) {
		if (isPointWithinBounds(-25, 6, 24, 80, mouseX, mouseY) && upgrades) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.upgrades"));
			renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
		}
		int offset = upgrades ? 81 : 0;
		if (isConfigEnabled() && isPointWithinBounds(-26, 6 + offset, 24, 24, mouseX, mouseY) && getMachine().hasSlotConfig()) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.config_slots"));
			renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
		}
		if (isConfigEnabled() && isPointWithinBounds(-26, 6 + offset + 25, 24, 24, mouseX, mouseY) && getMachine().showTankConfig()) {
			List<String> list = new ArrayList<>();
			list.add(StringUtils.t("reborncore.gui.tooltip.config_fluids"));
			renderTooltip(list, mouseX, mouseY);
			RenderSystem.disableLighting();
			RenderSystem.color4f(1, 1, 1, 1);
		}
        for (AbstractButtonWidget abstractButtonWidget : buttons) {
            if (abstractButtonWidget.isHovered()) {
                abstractButtonWidget.renderToolTip(mouseX, mouseY);
                break;
            }
        }
		super.drawMouseoverTooltip(mouseX, mouseY);
	}

	protected void drawTitle() {
		drawCentredString(StringUtils.t(be.getCachedState().getBlock().getTranslationKey()), 6, 4210752, Layer.FOREGROUND);
	}

	public void drawCentredString(String string, int y, int colour, Layer layer) {
		drawString(string, (containerWidth / 2 - getTextRenderer().getStringWidth(string) / 2), y, colour, layer);
	}

	protected void drawCentredString(String string, int y, int colour, int modifier, Layer layer) {
		drawString(string, (containerWidth / 2 - (getTextRenderer().getStringWidth(string)) / 2) + modifier, y, colour, layer);
	}

	public void drawString(String string, int x, int y, int colour, Layer layer) {
		int factorX = 0;
		int factorY = 0;
		if (layer == Layer.BACKGROUND) {
			factorX = this.x;
			factorY = this.y;
		}
		getTextRenderer().draw(string, x + factorX, y + factorY, colour);
		RenderSystem.color4f(1, 1, 1, 1);
	}

	public GuiButtonHologram addHologramButton(int x, int y, int id, Layer layer) {
		GuiButtonHologram buttonHologram = new GuiButtonHologram(x + this.x, y + this.y, this, layer, var1 -> {});
		addButton(buttonHologram);
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
			if (hasControlDown() && keyCode == GLFW.GLFW_KEY_C) {
				GuiSlotConfiguration.copyToClipboard();
				return true;
			} else if (hasControlDown() && keyCode == GLFW.GLFW_KEY_V) {
				GuiSlotConfiguration.pasteFromClipboard();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
	}

	@Override
	public void onClose() {
		slotConfigType = SlotConfigType.NONE;
		super.onClose();
	}

	@Nullable
	public MachineBaseBlockEntity getMachine() {
		return (MachineBaseBlockEntity) be;
	}

	/**
	 * @param rectX int Top left corner of region
	 * @param rectY int Top left corner of region
	 * @param rectWidth int Width of region
	 * @param rectHeight int Height of region
	 * @param pointX int Mouse pointer
	 * @param pointY int Mouse pointer
	 * @return boolean Returns true if mouse pointer is in region specified
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
		ItemStack provide(Fluid fluid);
	}

	public boolean isConfigEnabled() {
		return be instanceof MachineBaseBlockEntity && builtContainer != null;
	}

	public int getGuiLeft(){
		return x;
	}

	public int getGuiTop(){
		return y;
	}

	public MinecraftClient getMinecraft() {
		// Just to stop complains from IDEA
		if (minecraft == null){
			throw new NullPointerException("Minecraft client is null.");
		}
		return this.minecraft;
	}

	public TextRenderer getTextRenderer(){
		return this.font;
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int mouseButton) {
		//Expanded the width to allow for the upgrades
		return super.isClickOutsideBounds(mouseX + 40, mouseY, left + 40, top, mouseButton);
	}
}
