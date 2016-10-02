package reborncore.client.guibuilder;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import java.io.IOException;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class GuiBulderTest extends GuiContainer
{
    GuiBuilder builder = new GuiBuilder(GuiBuilder.defaultTextureSheet);

    public GuiBulderTest(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        builder.drawDefaultBackground(this, guiLeft, guiLeft, xSize , ySize);

        builder.drawSlot(this, guiLeft + 40, guiTop + 30);
        builder.drawSlot(this, guiLeft + 120, guiTop + 30);

        builder.drawPlayerSlots(this, guiLeft + xSize / 2, guiTop + 80, true);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
    }
}
