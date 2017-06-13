package reborncore.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiButtonItemTexture extends GuiButtonExt {

	public int textureU;
	public int textureV;
	public ItemStack itemstack;
	public String LINKED_PAGE;
	public String NAME;

	public GuiButtonItemTexture(int id, int xPos, int yPos, int u, int v, int width, int height, ItemStack stack,
	                            String linkedPage, String name) {
		super(id, xPos, yPos, width, height, "_");
		textureU = u;
		textureV = v;
		itemstack = stack;
		NAME = name;
		this.LINKED_PAGE = linkedPage;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float ticks) {
		if (this.visible) {
			boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
				&& mouseY < this.y + this.height;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			int u = textureU;
			int v = textureV;
			if (flag) {
				u += mc.fontRenderer.getStringWidth(this.NAME) + 25;
				v += mc.fontRenderer.getStringWidth(this.NAME) + 25;
				GL11.glPushMatrix();
				GL11.glColor4f(0f, 0f, 0f, 1f);
				this.drawTexturedModalRect(this.x, this.y, u, v, mc.fontRenderer.getStringWidth(this.NAME) + 25, height);
				GL11.glPopMatrix();
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(32826);
			RenderHelper.enableStandardItemLighting();
			RenderHelper.enableGUIStandardItemLighting();
			RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
			itemRenderer.renderItemIntoGUI(itemstack, this.x, this.y);
			this.drawString(mc.fontRenderer, this.NAME, this.x + 20, this.y + 3,
				Color.white.getRGB());
		}
	}

	public boolean getIsHovering() {
		return hovered;
	}

}
