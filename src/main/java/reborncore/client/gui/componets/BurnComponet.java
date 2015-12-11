package reborncore.client.gui.componets;


import reborncore.client.gui.BaseGui;

public class BurnComponet extends BaseComponet {

    /**
     * Set this to true if you want the fire to render
     */
    boolean isBurning = false;

    /**
     * This needs to be scaled to 13
     */
    int burnTime = 0;

    public BurnComponet(int x, int y) {
        super(x, y);
    }

    @Override
    public void drawGuiContainerForegroundLayer(BaseGui gui) {
        super.drawGuiContainerForegroundLayer(gui);

    }

    @Override
    public void drawGuiContainerBackgroundLayer(BaseGui gui) {
        super.drawGuiContainerBackgroundLayer(gui);
        gui.getBaseTextures().burnBase.draw(x, y, gui);
        if (isBurning) {
            gui.getBaseTextures().burnOverlay.draw(x, y, gui.getBaseTextures().burnOverlay.getWidth(), burnTime, gui);
        }
    }

    public void setIsBurning(boolean isBurning) {
        this.isBurning = isBurning;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }
}
