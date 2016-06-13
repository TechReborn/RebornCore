package reborncore.common.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.api.tile.IInventoryProvider;

import java.util.ArrayList;

/**
 * Created by Mark on 06/06/2016.
 */
public class InventoryCapabilityAttacher {


    public static InventoryCapabilityAttacher instace = new InventoryCapabilityAttacher();

    @SubscribeEvent
    public void onTELoad(AttachCapabilitiesEvent.TileEntity entity){
//        TileEntity tileEntity = entity.getTileEntity();
//        if(tileEntity instanceof IInventoryProvider){
//            entity.addCapability(new ResourceLocation("reborncore:invhax"), new InventoryCapabilityProvider(tileEntity, ((IInventoryProvider) tileEntity).getInventory()));
//        }
    }

}
