package reborncore.common.advanced;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class AdvancedGui extends GuiContainer
{
    @Nonnull EntityPlayer player;
    @Nonnull AdvancedTileEntity advancedTileEntity;

    public AdvancedGui(EntityPlayer player, AdvancedTileEntity advancedTileEntity)
    {
        super(new AdvancedContainer(player, advancedTileEntity));
        this.player = player;
        this.advancedTileEntity = advancedTileEntity;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        advancedTileEntity.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY, guiLeft, guiTop, xSize, ySize, this);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        advancedTileEntity.drawGuiContainerForegroundLayer(mouseX, mouseY, this, guiLeft, guiTop);
    }
}
