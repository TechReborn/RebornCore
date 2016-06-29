package reborncore.common.tile;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IContainerProvider;
import reborncore.api.tile.IInventoryProvider;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotInput;
import reborncore.client.gui.slots.SlotOutput;
import reborncore.common.container.RebornContainer;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.IInventoryUpdateable;
import reborncore.common.util.inventory.Inventory;
import reborncore.common.util.inventory.InventoryItemHandler;

import java.util.*;

/**
 * Created by Lordmau5 on 09.06.2016.
 */
public abstract class TileMachineInventory extends TileMachineBase implements IInventoryProvider, IContainerProvider, IInventoryUpdateable {

    private final Inventory inventoryBase;

    private Map<EnumFacing, IItemHandler> sidedHandlers = new HashMap<>();

    public TileMachineInventory(EnumPowerTier tier, int capacity, int costPerTick, int ticksNeeded,
                                String inventoryName, int inventorySize, int inventoryStackLimit) {
        super(tier, capacity, costPerTick, ticksNeeded);

        this.inventoryBase = new Inventory(inventoryName, inventorySize, inventoryStackLimit, this);

        for(EnumFacing facing : EnumFacing.VALUES) {
            this.sidedHandlers.put(facing, new InventoryItemHandler(getContainer(), facing));
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventoryBase;
    }

    @Override
    public void updateInventory() {
        markBlockForUpdate();
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        this.inventoryBase.readFromNBT(data);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        this.inventoryBase.writeToNBT(data);

        return super.writeToNBT(data);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return this.sidedHandlers.containsKey(facing) ? (T) this.sidedHandlers.get(facing) : super.getCapability(capability, facing);
    }

    protected Optional<RecipeCrafter> getCrafterForTile(){
        if(this instanceof IRecipeCrafterProvider){
            IRecipeCrafterProvider crafterProvider = (IRecipeCrafterProvider) this;
            if(crafterProvider.getRecipeCrafter() == null){
                return Optional.empty();
            }
            return Optional.of(crafterProvider.getRecipeCrafter());
        } else {
            return Optional.empty();
        }
    }
}
