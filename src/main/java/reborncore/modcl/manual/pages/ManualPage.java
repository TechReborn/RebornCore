package reborncore.modcl.manual.pages;

import reborncore.modcl.manual.GuiTeamRebornManual;

/**
 * Created by Prospector
 */
public abstract class ManualPage {
	public ManualPage() {
	}

	public abstract String title();

	public abstract void draw(GuiTeamRebornManual manual);

}
