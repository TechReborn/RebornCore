package reborncore.client.gui;

import net.minecraft.client.gui.Gui;

public class GuiUtil
{

	// TODO rewrite for 1.8
	//
	// public static void drawRepeated(IIcon icon, double x, double y, double
	// width, double height, double z) {
	// double iconWidthStep = (icon.getMaxU() - icon.getMinU()) / 16.0D;
	// double iconHeightStep = (icon.getMaxV() - icon.getMinV()) / 16.0D;
	//
	// Tessellator tessellator = Tessellator.instance;
	// tessellator.startDrawingQuads();
	// for (double cy = y; cy < y + height; cy += 16.0D) {
	// double quadHeight = Math.min(16.0D, height + y - cy);
	// double maxY = cy + quadHeight;
	// double maxV = icon.getMinV() + iconHeightStep * quadHeight;
	// for (double cx = x; cx < x + width; cx += 16.0D) {
	// double quadWidth = Math.min(16.0D, width + x - cx);
	// double maxX = cx + quadWidth;
	// double maxU = icon.getMinU() + iconWidthStep * quadWidth;
	//
	// tessellator.addVertexWithUV(cx, maxY, z, icon.getMinU(), maxV);
	// tessellator.addVertexWithUV(maxX, maxY, z, maxU, maxV);
	// tessellator.addVertexWithUV(maxX, cy, z, maxU, icon.getMinV());
	// tessellator.addVertexWithUV(cx, cy, z, icon.getMinU(), icon.getMinV());
	// }
	// }
	// tessellator.draw();
	// }

	public static void drawTooltipBox(int x, int y, int w, int h)
	{
		int bg = 0xf0100010;
		drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
		drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
		drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);// center
		drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
		drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
		int grad1 = 0x505000ff;
		int grad2 = 0x5028007F;
		drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
		drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);

		drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
		drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
	}

	public static void drawGradientRect(int x, int y, int w, int h, int colour1, int colour2)
	{
		new GuiHook().drawGradientRect(x, y, x + w, y + h, colour1, colour2);
	}

	public static class GuiHook extends Gui
	{
		@Override
		public void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
		{
			super.drawGradientRect(par1, par2, par3, par4, par5, par6);
		}
	}

	public static void drawColouredBox(int colour, int alpha, double posX, double posY, double width, double height)
	{
		drawColouredBox(colour, alpha, posX, posY, width, height, 0);
	}

	public static void drawColouredBox(int colour, int alpha, double posX, double posY, double width, double height,
			double zLevel)
	{
		int r = (colour >> 16 & 0xff);
		int g = (colour >> 8 & 0xff);
		int b = (colour & 0xff);
		drawColouredBox(r, g, b, alpha, posX, posY, width, height, zLevel);
	}

	public static void drawColouredBox(int r, int g, int b, int alpha, double posX, double posY, double width,
			double height, double zLevel)
	{
		if (width <= 0 || height <= 0)
		{
			return;
		} // TODO wait for mappings for new worldRenderer
			// GL11.glDisable(GL11.GL_TEXTURE_2D);
			// Tessellator tessellator = Tessellator.getInstance();
			// WorldRenderer worldRenderer = tessellator.getWorldRenderer();
			// worldRenderer.startDrawingQuads();
			// worldRenderer.setColorRGBA(r, g, b, alpha);
			// worldRenderer.addVertex(posX, posY + height, zLevel);
			// worldRenderer.addVertex(posX + width, posY + height, zLevel);
			// worldRenderer.addVertex(posX + width, posY, zLevel);
			// worldRenderer.addVertex(posX, posY, zLevel);
			// tessellator.draw();
			// GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
