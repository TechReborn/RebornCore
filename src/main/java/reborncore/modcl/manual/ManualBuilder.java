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
