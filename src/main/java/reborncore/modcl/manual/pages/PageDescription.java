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
