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

package reborncore.client.gui.builder.widget;

import com.mojang.blaze3d.platform.GlStateManager;

import java.util.ArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

public abstract class GuiWidget<T extends Container> extends AbstractContainerScreen {

	public static final Language translate = Language.getInstance();

	private final ArrayList<Widget> widgets = new ArrayList<>();
	private final Identifier background;

	public GuiWidget(T inventorySlotsIn, Identifier background, int xSize, int ySize, Component title) {
		super(inventorySlotsIn, MinecraftClient.getInstance().player.inventory, title);
		this.containerWidth = xSize;
		this.containerHeight = ySize;
		this.background = background;
	}

	@SuppressWarnings("unchecked")
	public T getContainer() {
		return (T) container;
	}

	@Override
	public void init() {
		super.init();
		widgets.clear();
		initWidgets();
	}

	public void addWidget(Widget widget) {
		widgets.add(widget);
	}

	public void removeWidget(Widget widget) {
		widgets.remove(widget);
	}

	public abstract void initWidgets();

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(background);
		int x = (this.width - this.containerWidth) / 2;
		int y = (this.height - this.containerHeight) / 2;
		this.blit(x, y, 0, 0, this.containerWidth, this.containerHeight);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		int x = (this.width - this.containerWidth) / 2;
		int y = (this.height - this.containerHeight) / 2;
		String name = translate.translate("tile.techreborn.industrialgrinder.name");

		font.draw(name, containerWidth / 2 - font.getStringWidth(name) / 2, 6, 4210752);
		font.draw(translate.translate("container.inventory"), 8, containerHeight - 94, 4210752);

		for (Widget widget : widgets) {
			widget.drawWidget(this, x, y, mouseX, mouseY);
		}
	}

	public TextRenderer getFontRenderer() {
		return font;
	}

}
