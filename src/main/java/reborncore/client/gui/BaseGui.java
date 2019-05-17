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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Identifier;
import reborncore.common.util.StringUtils;

public class BaseGui extends AbstractContainerScreen {
	Container container;
	BlockEntity tileEntity;
	Identifier overlays;
	Identifier guitexture;
	PlayerEntity player;
	String name;

	public BaseGui(Container container, BlockEntity tileEntity, PlayerEntity player, Identifier overlays, Identifier guitexture, String name) {
		super(container, player.inventory, new TextComponent(name));
		this.container = container;
		this.tileEntity = tileEntity;
		this.overlays = overlays;
		this.guitexture = guitexture;
		this.name = name;
		this.player = player;
	}

	@Override
	protected void drawForeground(int p_146979_1_, int p_146979_2_) {
		String name = StringUtils.t("tile." + this.name + ".name");
		this.font.draw(name, this.containerWidth / 2 - 6 - this.font.getStringWidth(name) / 2, 6, 4210752);
	}

	@Override
	protected void drawBackground(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		this.minecraft.getTextureManager().bindTexture(guitexture);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
	}
}
