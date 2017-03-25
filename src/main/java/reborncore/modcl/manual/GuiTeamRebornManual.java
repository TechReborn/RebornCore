package reborncore.modcl.manual;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.translation.I18n;

/**
 * Created by Prospector
 */
public class GuiTeamRebornManual extends GuiScreen {
	public int xSize = 350;
	public int ySize = 200;
	public int guiLeft;
	public int guiTop;
	ManualBuilder builder = new ManualBuilder();

	public GuiTeamRebornManual() {

	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = this.width / 2 - this.xSize / 2;
		this.guiTop = this.height / 2 - this.ySize / 2;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawDefaultBackground();
		builder.drawDefaultBackground(this, guiLeft, guiTop, xSize, ySize);
		drawGradientRect(guiLeft + 3, 6 + guiTop, 347 + guiLeft, guiTop + 18, 0xFFA1A1A1, 0xFFA1A1A1);
		drawCentredStringShadow(I18n.translateToLocal("item.reborncore:manual.name"), 8, 0xFFFFFFFF);
	}

	protected void drawCentredString(String string, int y, int colour) {
		drawString(string, (xSize / 2 - mc.fontRendererObj.getStringWidth(string) / 2), y, colour);
	}

	protected void drawCentredStringShadow(String string, int y, int colour) {
		drawStringShadow(string, (xSize / 2 - mc.fontRendererObj.getStringWidth(string) / 2), y, colour);
	}

	protected void drawString(String string, int x, int y, int colour) {
		mc.fontRendererObj.drawString(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}

	protected void drawStringShadow(String string, int x, int y, int colour) {
		mc.fontRendererObj.drawStringWithShadow(string, x + guiLeft, y + guiTop, colour);
		GlStateManager.color(1, 1, 1, 1);
	}
}
