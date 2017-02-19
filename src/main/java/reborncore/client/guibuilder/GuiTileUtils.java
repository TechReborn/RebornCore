package reborncore.client.guibuilder;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Gigabit101 on 19/02/2017.
 */
public class GuiTileUtils
{
    @SideOnly(Side.CLIENT)
    public static void drawSlotsFromList(List<Slot> slotList, GuiBuilder builder, GuiScreen guiContainer, int guiLeft, int guiTop)
    {
        if(slotList != null)
        {
            for (Slot s : slotList)
            {
                builder.drawSlot(guiContainer, guiLeft + s.xPos - 1, guiTop + s.yPos - 1);
            }
        }
    }
}
