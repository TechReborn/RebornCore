package reborncore.api.gui;

import net.minecraft.util.ResourceLocation;
import reborncore.client.gui.BaseGui;

public class GuiTexture {

    ResourceLocation resourceLocation;
    int width;
    int height;
    int sheetX;
    int sheetY;

    public GuiTexture(ResourceLocation resourceLocation, int width, int height, int sheetX, int sheetY) {
        this.resourceLocation = resourceLocation;
        this.width = width;
        this.height = height;
        this.sheetX = sheetX;
        this.sheetY = sheetY;
    }


    public void draw(int x, int y, int width, int height, BaseGui baseGui) {
        int a = (baseGui.width - baseGui.getXSize()) / 2;
        int b = (baseGui.height - baseGui.getYSize()) / 2;
        baseGui.drawTexturedModalRect(x + a, y + b, sheetX, sheetY, width, height);
    }

    public void draw(int x, int y, BaseGui baseGui) {
        draw(x, y, width, height, baseGui);
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSheetX() {
        return sheetX;
    }

    public int getSheetY() {
        return sheetY;
    }
}
