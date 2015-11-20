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


    public void draw(int x, int y, BaseGui baseGui){
        baseGui.drawTexturedModalRect(x, y, sheetX, sheetY, width, height);
    }

}
