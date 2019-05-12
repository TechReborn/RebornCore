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

import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.Container;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class GuiBulderTest extends ContainerScreen {
	GuiBuilder builder = new GuiBuilder(GuiBuilder.defaultTextureSheet);

	public GuiBulderTest(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		builder.drawDefaultBackground(this, left, left, containerWidth, containerHeight);

		builder.drawSlot(this, left + 40, top + 30);
		builder.drawSlot(this, left + 120, top + 30);

		builder.drawPlayerSlots(this, left + containerWidth / 2, top + 80, true);
	}

}
