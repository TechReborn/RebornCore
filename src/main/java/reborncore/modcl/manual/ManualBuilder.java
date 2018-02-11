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

package reborncore.modcl.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import reborncore.RebornCore;
import reborncore.client.guibuilder.GuiBuilder;

/**
 * Created by Prospector
 */
public class ManualBuilder extends GuiBuilder {
	public static ResourceLocation resourceLocation = new ResourceLocation(RebornCore.MOD_ID + ":" + "textures/gui/manual.png");

	public ManualBuilder() {
		super(resourceLocation);
	}

	public void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y, 256 - width / 2, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x, y + height / 2, 0, 256 - height / 2, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y + height / 2, 256 - width / 2, 256 - height / 2, width / 2, height / 2);
	}
}
