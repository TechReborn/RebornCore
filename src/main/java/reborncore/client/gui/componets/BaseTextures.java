package reborncore.client.gui.componets;

import net.minecraft.util.ResourceLocation;
import reborncore.api.gui.GuiTexture;

public class BaseTextures {

    private static final ResourceLocation baseTexture = new ResourceLocation(
            "reborncore", "textures/gui/base.png");


    public GuiTexture background;
    public GuiTexture slot;
    public GuiTexture burnBase;
    public GuiTexture burnOverlay;
    public GuiTexture powerBase;
    public GuiTexture powerOverlay;
    public GuiTexture progressBase;
    public GuiTexture progressOverlay;
    public GuiTexture tank;
    public GuiTexture tankBase;
    public GuiTexture tankScale;
    public GuiTexture powerBaseOld;
    public GuiTexture powerOverlayOld;


    public BaseTextures() {
        background = new GuiTexture(baseTexture, 176, 166, 0, 0);
        slot = new GuiTexture(baseTexture, 18, 18, 176, 31);
        burnBase = new GuiTexture(baseTexture, 14, 14, 176, 0);
        burnOverlay = new GuiTexture(baseTexture, 13, 13, 176, 50);
        powerBase = new GuiTexture(baseTexture, 7, 13, 190, 0);
        powerOverlay = new GuiTexture(baseTexture, 7, 13, 197, 0);
        progressBase = new GuiTexture(baseTexture, 22, 15, 200, 14);
        progressOverlay = new GuiTexture(baseTexture, 22, 16, 177, 14);
        tank = new GuiTexture(baseTexture, 20, 55, 176, 63);
        tankBase = new GuiTexture(baseTexture, 20, 55, 196, 63);
        tankScale = new GuiTexture(baseTexture, 20, 55, 216, 63);
        powerBaseOld = new GuiTexture(baseTexture, 32, 17, 224, 13);
        powerOverlayOld = new GuiTexture(baseTexture, 32, 17, 224, 30);
    }
}
