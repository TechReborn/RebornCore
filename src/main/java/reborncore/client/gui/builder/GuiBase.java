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
import net.minecraft.client.gui.screen.ingame.ScreenWithHandler;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import reborncore.api.blockentity.IUpgradeable;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.containerBuilder.builder.slot.PlayerInventorySlot;
import reborncore.client.gui.builder.slot.FluidConfigGui;
import reborncore.client.gui.builder.slot.GuiTab;
import reborncore.client.gui.builder.slot.SlotConfigGui;
import reborncore.client.gui.builder.widget.GuiButtonHologram;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.util.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Prospector
 */

public class GuiBase<T extends ScreenHandler> extends ScreenWithHandler<T> {

	public static FluidCellProvider fluidCellProvider = fluid -> ItemStack.EMPTY;
	public static ItemStack wrenchStack = ItemStack.EMPTY;

	private List<GuiTab.Builder> tabBuilders = Util.make(new ArrayList<>(), builders -> {
		builders.add(GuiTab.Builder.builder()
				.name("reborncore.gui.tooltip.config_slots")
				.enabled(guiTab -> guiTab.machine().hasSlotConfig())
				.stack(guiTab -> wrenchStack)
				.draw(SlotConfigGui::draw)
				.click(SlotConfigGui::mouseClicked)
				.mouseReleased(SlotConfigGui::mouseReleased)
				.hideGuiElements()
				.keyPressed((guiBase, keyCode, scanCode, modifiers) -> {
					if (hasControlDown() && keyCode == GLFW.GLFW_KEY_C) {
						SlotConfigGui.copyToClipboard();
						return true;
					} else if (hasControlDown() && keyCode == GLFW.GLFW_KEY_V) {
						SlotConfigGui.pasteFromClipboard();
						return true;
					} else if (keyCode == GLFW.GLFW_KEY_ESCAPE && SlotConfigGui.selectedSlot != -1) {
						SlotConfigGui.reset();
						return true;
					}
					return false;
				})
				.tips(tips -> {
					tips.add("reborncore.gui.slotconfigtip.slot");
					tips.add("reborncore.gui.slotconfigtip.side1");
					tips.add("reborncore.gui.slotconfigtip.side2");
					tips.add("reborncore.gui.slotconfigtip.side3");
					tips.add("reborncore.gui.slotconfigtip.copy1");
					tips.add("reborncore.gui.slotconfigtip.copy2");
				})
		);

		builders.add(GuiTab.Builder.builder()
				.name("reborncore.gui.tooltip.config_fluids")
				.enabled(guiTab -> guiTab.machine().showTankConfig())
				.stack(guiTab -> GuiBase.fluidCellProvider.provide(Fluids.LAVA))
				.draw(FluidConfigGui::draw)
				.click(FluidConfigGui::mouseClicked)
				.mouseReleased(FluidConfigGui::mouseReleased)
				.hideGuiElements()
		);

		builders.add(GuiTab.Builder.builder()
				.name("reborncore.gui.tooltip.config_redstone")
				.stack(guiTab -> new ItemStack(Items.REDSTONE))
				.draw(RedstoneConfigGui::draw)
				.click(RedstoneConfigGui::mouseClicked)
		);
	});

	public GuiBuilder builder = new GuiBuilder();
	public BlockEntity be;
	@Nullable
	public BuiltContainer builtContainer;
	private int xSize = 176;
	private int ySize = 176;

	private GuiTab selectedTab;
	private List<GuiTab> tabs;

	public boolean upgrades;

	public GuiBase(PlayerEntity player, BlockEntity blockEntity, T container) {
		super(container, player.inventory, new LiteralText(I18n.translate(blockEntity.getCachedState().getBlock().getTranslationKey())));
		this.be = blockEntity;
		this.builtContainer = (BuiltContainer) container;
		selectedTab = null;
		populateSlots();
	}

	private void populateSlots() {
		tabs = tabBuilders.stream()
				.map(builder -> builder.build(getMachine(), this))
				.filter(GuiTab::enabled)
				.collect(Collectors.toList());
	}

	public int getContainerWidth() {
		return backgroundWidth;
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
			SlotConfigGui.init(this);
		}
		if (isConfigEnabled() && getMachine().getTank() != null && getMachine().showTankConfig()) {
			FluidConfigGui.init(this);
		}
	}

	@Override
	protected void drawBackground(float lastFrameDuration, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		renderBackground();
		boolean drawPlayerSlots = selectedTab == null && drawPlayerSlots();
		updateSlotDraw(drawPlayerSlots);
		builder.drawDefaultBackground(this, x, y, xSize, ySize);
		if (drawPlayerSlots) {
			builder.drawPlayerSlots(this, x + backgroundWidth / 2, y + 93, true);
		}
		if (tryAddUpgrades() && be instanceof IUpgradeable) {
			IUpgradeable upgradeable = (IUpgradeable) be;
			if (upgradeable.canBeUpgraded()) {
				builder.drawUpgrades(this, x - 24, y + 6);
				upgrades = true;
			}
		}
		int offset = upgrades ? 86 : 6;
		for (GuiTab slot : tabs) {
			if (slot.enabled()) {
				builder.drawSlotTab(this, x - 24, y + offset, slot.stack());
				offset += 24;
			}
		}

		final GuiBase<T> gui = this;
		getTab().ifPresent(guiTab -> builder.drawSlotConfigTips(gui, x + backgroundWidth / 2, y + 93, mouseX, mouseY, guiTab));

	}

	private void updateSlotDraw(boolean doDraw) {
		if (builtContainer == null) {
			return;
		}
		for (Slot slot : builtContainer.slots) {
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
		getTab().ifPresent(guiTab -> guiTab.draw(mouseX, mouseY));
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
		int offset = upgrades ? 82 : 0;
		for (GuiTab tab : tabs) {
			if (isPointWithinBounds(-26, 6 + offset, 24, 23, mouseX, mouseY)) {
				renderTooltip(Collections.singletonList(StringUtils.t(tab.name())), mouseX, mouseY);
				RenderSystem.disableLighting();
				RenderSystem.color4f(1, 1, 1, 1);
			}
			offset += 24;
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
		drawString(string, (backgroundWidth / 2 - getTextRenderer().getStringWidth(string) / 2), y, colour, layer);
	}

	protected void drawCentredString(String string, int y, int colour, int modifier, Layer layer) {
		drawString(string, (backgroundWidth / 2 - (getTextRenderer().getStringWidth(string)) / 2) + modifier, y, colour, layer);
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
		GuiButtonHologram buttonHologram = new GuiButtonHologram(x + this.x, y + this.y, this, layer, var1 -> {
		});
		addButton(buttonHologram);
		return buttonHologram;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (getTab().map(guiTab -> guiTab.click(mouseX, mouseY, mouseButton)).orElse(false)) {
			return true;
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
		for (GuiTab tab : tabs) {
			if (isPointWithinBounds(-26, 84 - offset, 30, 23, mouseX, mouseY)) {
				if (selectedTab == tab) {
					closeSelectedTab();
				} else {
					selectedTab = tab;
				}
				SlotConfigGui.reset();
				break;
			}
			offset -= 24;
		}

		if (getTab().map(guiTab -> guiTab.mouseReleased(mouseX, mouseY, state)).orElse(false)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getTab().map(guiTab -> guiTab.keyPress(keyCode, scanCode, modifiers)).orElse(false)) {
			return true;
		}
		if (selectedTab != null && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			closeSelectedTab();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onClose() {
		closeSelectedTab();
		super.onClose();
	}

	@Nullable
	public MachineBaseBlockEntity getMachine() {
		return (MachineBaseBlockEntity) be;
	}

	/**
	 * @param rectX      int Top left corner of region
	 * @param rectY      int Top left corner of region
	 * @param rectWidth  int Width of region
	 * @param rectHeight int Height of region
	 * @param pointX     int Mouse pointer
	 * @param pointY     int Mouse pointer
	 * @return boolean Returns true if mouse pointer is in region specified
	 */
	public boolean isPointInRect(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY) {
		return super.isPointWithinBounds(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
	}

	public enum Layer {
		BACKGROUND, FOREGROUND
	}

	public interface FluidCellProvider {
		ItemStack provide(Fluid fluid);
	}

	public boolean isConfigEnabled() {
		return be instanceof MachineBaseBlockEntity && builtContainer != null;
	}

	public int getGuiLeft() {
		return x;
	}

	public int getGuiTop() {
		return y;
	}

	public MinecraftClient getMinecraft() {
		// Just to stop complains from IDEA
		if (client == null) {
			throw new NullPointerException("Minecraft client is null.");
		}
		return this.client;
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	public Optional<GuiTab> getTab() {
		if (!isConfigEnabled()) {
			return Optional.empty();
		}
		return Optional.ofNullable(selectedTab);
	}

	public boolean isTabOpen() {
		return selectedTab != null;
	}

	public boolean hideGuiElements() {
		return selectedTab != null && selectedTab.hideGuiElements();
	}

	public void closeSelectedTab() {
		selectedTab = null;
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int mouseButton) {
		//Expanded the width to allow for the upgrades
		return super.isClickOutsideBounds(mouseX + 40, mouseY, left + 40, top, mouseButton);
	}
}
