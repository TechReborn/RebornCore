package reborncore.client.guibuilder;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Gigabit101 on 19/02/2017.
 */
public interface IGuiTile
{
    @SideOnly(Side.CLIENT)
    void drawGuiContainerForegroundLayer(GuiScreen gui, int guiLeft, int guiTop, int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    void drawGuiContainerBackgroundLayer(GuiScreen gui, int guiLeft, int guiTop, int xSize, int ySize, float partialTicks, int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    GuiContainer getGui(EntityPlayer player);

    void opengui(EntityPlayer player, Object mod, World world, BlockPos pos);

    Container getContainer(EntityPlayer player);

    @Nullable
    List<Slot> getSlots();
}
