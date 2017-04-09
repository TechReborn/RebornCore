package reborncore.common.logic;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class LogicGui extends GuiContainer
{
    @Nonnull EntityPlayer player;
    @Nonnull
    LogicController logicController;

    public LogicGui(EntityPlayer player, LogicController logicController)
    {
        super(new LogicContainer(player, logicController));
        this.player = player;
        this.logicController = logicController;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        logicController.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY, guiLeft, guiTop, xSize, ySize, this);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        logicController.drawGuiContainerForegroundLayer(mouseX, mouseY, this, guiLeft, guiTop);
    }
}
