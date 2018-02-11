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

package reborncore.modcl.manual.pages;

import net.minecraft.client.Minecraft;
import reborncore.modcl.manual.GuiTeamRebornManual;

import javax.annotation.Nullable;

/**
 * Created by Prospector
 */
public abstract class ManualPage {
	public ManualPage() {
	}

	protected static void drawString(String text, int x, int y, int colour, GuiTeamRebornManual manual) {
		manual.drawString(text, x, y, colour);
	}

	protected static void drawStringWithShadow(String text, int x, int y, int colour, GuiTeamRebornManual manual) {
		manual.drawStringShadow(text, x, y, colour);
	}

	protected static void drawCentredString(String text, int x, int y, int colour, GuiTeamRebornManual manual) {
		manual.drawCentredString(text, y, colour);
	}

	protected static void drawCentredStringWithShadow(String text, int x, int y, int colour, GuiTeamRebornManual manual) {
		manual.drawCentredStringShadow(text, y, colour);
	}

	@Nullable
	public abstract ManualPage nextPage();

	public abstract String title();

	public abstract void draw(Minecraft mc, GuiTeamRebornManual manual);
}
