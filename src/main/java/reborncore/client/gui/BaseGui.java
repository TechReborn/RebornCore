package reborncore.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import reborncore.api.IGuiComponent;

import java.util.ArrayList;
import java.util.List;

public class BaseGui extends GuiContainer {

    Container container;

    TileEntity tileEntity;

    List<IGuiComponent> componentList;

    public BaseGui(Container container, TileEntity tileEntity) {
        super(container);
        this.container = container;
        this.tileEntity = tileEntity;
        componentList = new ArrayList<IGuiComponent>();
        registerComponets();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        GuiUtil.drawColouredBox(198, 198, 198, 254, x, y, xSize, ySize, 0);
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
