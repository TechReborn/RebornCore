package reborncore.common.util.inventory;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import reborncore.common.util.Inventory;

import javax.annotation.Nullable;

/**
 * Created by Mark on 06/06/2016.
 */
public class InventoryCapabilityProvider implements ICapabilityProvider {

    TileEntity entity;
    Inventory inventory;

    IItemHandler handler;

    public InventoryCapabilityProvider(TileEntity entity, Inventory inventory) {
        this.entity = entity;
        this.inventory = inventory;
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY != null)
        {
            if(inventory == null){
                return;
            }
            handler = new InventoryStackHandler(inventory);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T)handler;
        }
        return null;
    }
}
