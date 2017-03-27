package reborncore.modcl.manual.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import reborncore.RebornCore;

/**
 * Created by Prospector
 */
public class GuiSmallButton extends GuiButton {
	public static ResourceLocation ELEMENTS = new ResourceLocation(RebornCore.MOD_ID + ":" + "textures/gui/manual_elements.png");

	public GuiSmallButton(int buttonId, int x, int y, String buttonText) {
		super(buttonId, x, y, buttonText);
	}

	public GuiSmallButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(ELEMENTS);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			int textureY = 0;
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, textureY + i * 12, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, textureY + i * 12, this.width / 2, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if (packedFGColour != 0) {
				j = packedFGColour;
			} else if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}

			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
		}
	}

}
