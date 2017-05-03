package teamreborn.reborncore.gui.assembler;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * File Created by Prospector.
 */
public class GuiAssembler {
	public static final ResourceLocation BACKGROUND_SHEET = new ResourceLocation("reborncore", "textures/gui/assembler_background.png");
	public static final ResourceLocation DEFAULT_ELEMENTS = new ResourceLocation("reborncore", "textures/gui/assembler_elements.png");
	public final ResourceLocation customElementSheet;

	public GuiAssembler(ResourceLocation elementSheet) {
		this.customElementSheet = elementSheet;
	}

	public GuiAssembler() {
		this.customElementSheet = null;
	}

	public static void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
		x = adjustX(gui, x);
		y = adjustY(gui, y);
		gui.mc.getTextureManager().bindTexture(BACKGROUND_SHEET);

		gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y, 256 - width / 2, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x, y + height / 2, 0, 256 - height / 2, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y + height / 2, 256 - width / 2, 256 - height / 2, width / 2, height / 2);
	}

	public static void drawRect(GuiScreen gui, int x, int y, int width, int height, int colour) {
		drawGradientRect(gui, x, y, width, height, colour, colour);
	}

	public static void drawGradientRect(GuiScreen gui, int x, int y, int width, int height, int startColor, int endColor) {
		x = adjustX(gui, x);
		y = adjustY(gui, y);

		int left = x;
		int top = y;
		int right = x + width;
		int bottom = y + height;
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double) right, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
		vertexbuffer.pos((double) left, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
		vertexbuffer.pos((double) left, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
		vertexbuffer.pos((double) right, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static int adjustX(GuiScreen gui, int x) {
		if (gui instanceof IDynamicAdjustmentGUI) {
			return ((IDynamicAdjustmentGUI) gui).getOffsetFactorX() + x;
		}
		return 0;
	}

	public static int adjustY(GuiScreen gui, int y) {
		if (gui instanceof IDynamicAdjustmentGUI) {
			return ((IDynamicAdjustmentGUI) gui).getOffsetFactorY() + y;
		}
		return 0;
	}

	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	public static void drawPlayerSlots(GuiScreen gui, int posX, int posY, boolean center) {
		if (center)
			posX -= 81;
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				drawSlot(gui, posX + x * 18, posY + y * 18);

		for (int x = 0; x < 9; x++) {
			drawSlot(gui, posX + x * 18, posY + 58);
		}
	}

	public static void drawSlot(GuiScreen gui, int posX, int posY) {
		posX = adjustX(gui, posX);
		posY = adjustY(gui, posY);
		gui.mc.getTextureManager().bindTexture(BACKGROUND_SHEET);

		gui.drawTexturedModalRect(posX, posY, 150, 0, 18, 18);
	}

	public static void drawString(GuiScreen gui, String string, int x, int y, int color) {
		gui.mc.fontRendererObj.drawString(string, x, y, color);
	}

	public static void drawString(GuiScreen gui, String string, int x, int y) {
		drawString(gui, string, x, y, 16777215);
	}
}
