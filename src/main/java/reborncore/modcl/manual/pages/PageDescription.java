/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Prospector
 */
public class PageDescription extends ManualPage {
	private String title = "Blank Title";
	private List<String> description = new ArrayList<>();

	private static List<String> wrapDescriptionLines(List<String> descriptionLines) {
		Minecraft minecraft = Minecraft.getMinecraft();
		List<String> output = new ArrayList<String>();
		for (String descriptionLine : descriptionLines) {
			List<String> textLines = minecraft.fontRenderer.listFormattedStringToWidth(descriptionLine, GuiTeamRebornManual.xSize - 12);
			output.addAll(textLines);
		}
		return output;
	}

	private void addStrings(String... strings) {
		for (String string : strings) {
			description.add(string);
		}
	}

	private List<String> expandLines(List<String> strings) {
		List<String> output = new ArrayList<>();
		for (String string : strings) {
			String[] expandedStrings = string.split("\\\\n");
			Collections.addAll(output, expandedStrings);
		}
		return output;
	}

	@Override
	public ManualPage nextPage() {
		return null;
	}

	@Override
	public String title() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(String... description) {

		addStrings(description);
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	@Override
	public void draw(Minecraft mc, GuiTeamRebornManual manual) {
		description = expandLines(getDescription());
		description = wrapDescriptionLines(getDescription());

		int y = 21;
		for (String string : getDescription()) {
			if (y > GuiTeamRebornManual.ySize - 6 - mc.fontRenderer.FONT_HEIGHT) {
				break;
			}
			drawString(string, 6, y, 0xFF525252, manual);
			y += mc.fontRenderer.FONT_HEIGHT + 2;
		}
	}
}
