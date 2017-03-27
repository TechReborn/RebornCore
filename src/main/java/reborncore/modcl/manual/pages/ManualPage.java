package reborncore.modcl.manual.pages;

import com.sun.istack.internal.Nullable;
import net.minecraft.client.Minecraft;
import reborncore.modcl.manual.GuiTeamRebornManual;

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
