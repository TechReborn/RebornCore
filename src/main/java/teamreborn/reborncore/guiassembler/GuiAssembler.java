package teamreborn.reborncore.guiassembler;

import net.minecraft.client.gui.GuiScreen;
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

	public static void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
		x = adjustX(gui, x);
		y = adjustY(gui, x);
		gui.mc.getTextureManager().bindTexture(BACKGROUND_SHEET);

		gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y, 256 - width / 2, 0, width / 2, height / 2);
		gui.drawTexturedModalRect(x, y + height / 2, 0, 256 - height / 2, width / 2, height / 2);
		gui.drawTexturedModalRect(x + width / 2, y + height / 2, 256 - width / 2, 256 - height / 2, width / 2, height / 2);
	}
}
