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
            sidedHandlers.put(facing, new InventoryItemHandler(facing));
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
        inventoryBase.writeToNBT(data);

        return super.writeToNBT(data);
    }

	@Override
	public void updateEntity() {
		super.updateEntity();
		getInventory().isDirty = false;
	}

	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return super.getCapability(capability, facing);
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

    private class InventoryItemHandler implements IItemHandler {

        private final EnumFacing facing;
        private Map<EnumFacing, List<BaseSlot>> slotMap = new HashMap<>();

        InventoryItemHandler(EnumFacing facing) {
            this.facing = facing;

            RebornContainer container = getContainer();
            for(EnumFacing _facing : EnumFacing.VALUES) {
                List<BaseSlot> slotList = new ArrayList<>();
                for(Map.Entry<Integer, BaseSlot> entry : container.slotMap.entrySet()) {
                    BaseSlot baseSlot = entry.getValue();
                    if(_facing == EnumFacing.UP && baseSlot instanceof SlotInput) {
                        slotList.add(baseSlot);
                    }
                    else if(_facing == EnumFacing.DOWN && baseSlot instanceof SlotOutput) {
                        slotList.add(baseSlot);
                    }
                    else {
                        slotList.add(baseSlot);
                    }
                }
                slotMap.put(_facing, slotList);
            }
        }

        @Override
        public int getSlots() {
            return slotMap.get(facing).size();
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            return slotMap.get(facing).get(slotIndex).getStack();
        }

        @Override
        public ItemStack insertItem(int slotIndex, ItemStack stack, boolean simulate) {
            if(stack == null || stack.stackSize == 0)
                return null;

            Slot slot = slotMap.get(facing).get(slotIndex);
            if(!slot.getHasStack()) {
                slot.putStack(stack);
                return null;
            }

            ItemStack existing = slot.getStack();
            int limit = slot.getSlotStackLimit();

            if (existing != null)
            {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.stackSize;
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.stackSize > limit;

            if (!simulate)
            {
                if (existing == null)
                {
                    slot.putStack(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                }
                else
                {
                    existing.stackSize += reachedLimit ? limit : stack.stackSize;
                }
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
        }

        @Override
        public ItemStack extractItem(int slotIndex, int amount, boolean simulate) {
            if (amount == 0)
                return null;

            Slot slot = slotMap.get(facing).get(slotIndex);
            if(slot.getStack() == null) {
                return null;
            }

            ItemStack existing = slot.getStack();
            if (existing == null)
                return null;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.stackSize <= toExtract)
            {
                if (!simulate)
                {
                    slot.putStack(null);
                }
                return existing;
            }
            else
            {
                if (!simulate)
                {
                    slot.putStack(ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }
    }

//    @Override
//    public int[] getSlotsForFace(EnumFacing side) {
//        if(getContainerForTile().isPresent()) {
//            RebornContainer container = getContainerForTile().get();
//            ArrayList<Integer> intList = new ArrayList<>();
//            for (int i = 0; i < container.slotMap.size(); i++) {
//                intList.add(i);
//            }
//
//            return ArrayUtils.toPrimitive(intList.toArray(new Integer[intList.size()]));
//        }
//        return new int[0];
//    }
//
//    @Override
//    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
//        if(getContainerForTile().isPresent()) {
//            RebornContainer container = getContainerForTile().get();
//            if(container.slotMap.containsKey(index)) {
//                Slot slot = container.slotMap.get(index);
//                if(slot.isItemValid(itemStackIn)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
//        if(getContainerForTile().isPresent()) {
//            RebornContainer container = getContainerForTile().get();
//            if(container.slotMap.containsKey(index)) {
//                BaseSlot slot = container.slotMap.get(index);
//                if(slot.canWorldBlockRemove()) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
