package reborncore.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import reborncore.api.gui.IGuiComponent;
import reborncore.client.gui.componets.BaseTextures;

import java.util.ArrayList;
import java.util.List;

public class BaseGui extends GuiContainer {

    Container container;

    TileEntity tileEntity;

    List<IGuiComponent> componentList;

    BaseTextures baseTextures;

    private static final ResourceLocation baseTexture = new ResourceLocation(
            "reborncore", "textures/gui/base.png");

    public BaseGui(Container container, TileEntity tileEntity) {
        super(container);
        this.container = container;
        this.tileEntity = tileEntity;
        componentList = new ArrayList<IGuiComponent>();
        registerComponets();
        baseTextures = new BaseTextures();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        this.mc.renderEngine.bindTexture(baseTexture);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        baseTextures.background.draw(x, y, this);

        for(Object slotObj : container.inventorySlots){
            if(slotObj instanceof Slot){
                Slot slot = (Slot) slotObj;
                baseTextures.slot.draw(slot.xDisplayPosition + x -1, slot.yDisplayPosition + y-1, this);
            }
        }
    }

    public Container getContainer() {
        return container;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    /**
     * Override this and add components in here.
     */
    public void registerComponets(){

    }

    public void registerComponet(IGuiComponent component){
        componentList.add(component);
    }

    @Override
    public void initGui() {
        super.initGui();
        for(IGuiComponent component : componentList){
            component.initGui(this);
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        for(IGuiComponent component : componentList){
            component.drawScreen(this);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
        for(IGuiComponent component : componentList){
            component.drawGuiContainerBackgroundLayer(this);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        for(IGuiComponent component : componentList){
            component.onGuiClosed(this);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for(IGuiComponent component : componentList){
            component.updateScreen(this);
        }
    }
}
