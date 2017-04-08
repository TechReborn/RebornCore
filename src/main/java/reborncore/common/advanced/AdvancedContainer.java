package reborncore.common.advanced;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import reborncore.common.container.RebornContainer;

import javax.annotation.Nonnull;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class AdvancedContainer extends RebornContainer
{
    @Nonnull AdvancedTileEntity advancedTileEntity;
    @Nonnull EntityPlayer player;

    public AdvancedContainer(EntityPlayer player, AdvancedTileEntity advancedTileEntity)
    {
        super();
        this.player = player;
        this.advancedTileEntity = advancedTileEntity;
        if(advancedTileEntity.getSlots() != null)
        {
            for(Slot s : advancedTileEntity.getSlots())
            {
                addSlotToContainer(s);
            }
        }
        drawPlayersInv(player, advancedTileEntity.inventoryOffsetX(), advancedTileEntity.inventoryOffsetY());
        drawPlayersHotBar(player, advancedTileEntity.inventoryOffsetX(), advancedTileEntity.inventoryOffsetY() + 58);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
}
