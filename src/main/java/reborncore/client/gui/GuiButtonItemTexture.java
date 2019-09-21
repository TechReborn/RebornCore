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

package reborncore.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import reborncore.common.util.Color;

public class GuiButtonItemTexture extends ButtonWidget {

	public int textureU;
	public int textureV;
	public ItemStack itemstack;
	public String LINKED_PAGE;
	public String NAME;

	public GuiButtonItemTexture(int xPos, int yPos, int u, int v, int width, int height, ItemStack stack,
	                            String linkedPage, String name, ButtonWidget.PressAction pressAction) {
		super(xPos, yPos, width, height, "_", pressAction);
		textureU = u;
		textureV = v;
		itemstack = stack;
		NAME = name;
		this.LINKED_PAGE = linkedPage;
	}

	@Override
	public void render(int mouseX, int mouseY, float ticks) {
		if (this.visible) {
			MinecraftClient mc = MinecraftClient.getInstance();
			boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
				&& mouseY < this.y + this.height;
			mc.getTextureManager().method_22813(WIDGETS_LOCATION);
			int u = textureU;
			int v = textureV;
			if (flag) {
				u += mc.textRenderer.getStringWidth(this.NAME) + 25;
				v += mc.textRenderer.getStringWidth(this.NAME) + 25;
				GL11.glPushMatrix();
				GL11.glColor4f(0f, 0f, 0f, 1f);
				this.blit(this.x, this.y, u, v, mc.textRenderer.getStringWidth(this.NAME) + 25, height);
				GL11.glPopMatrix();
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(32826);
			GuiLighting.enable();
			GuiLighting.enableForItems();
			ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
			itemRenderer.renderGuiItemIcon(itemstack, this.x, this.y);
			this.drawString(mc.textRenderer, this.NAME, this.x + 20, this.y + 3,
			                Color.WHITE.getColor());
		}
	}

}
