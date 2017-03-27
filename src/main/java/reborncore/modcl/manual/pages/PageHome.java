package reborncore.modcl.manual.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import reborncore.modcl.manual.GuiTeamRebornManual;

/**
 * Created by Prospector
 */
public class PageHome extends ManualPage {
	@Override
	public String title() {
		return I18n.translateToLocal("item.reborncore:manual.name");
	}

	@Override
	public void draw(Minecraft mc, GuiTeamRebornManual manual) {

	}

	@Override
	public ManualPage nextPage() {
		return null;
	}
}
