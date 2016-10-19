package reborncore.common.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import reborncore.api.power.EnumPowerTier;
import reborncore.common.IWrenchable;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.TileEnergyBase;
import reborncore.common.util.inventory.Inventory;

/**
 * Created by Lordmau5 on 08.06.2016.
 */
public abstract class TileMachineBase extends TileEnergyBase implements IWrenchable {

    /* Energy and Tick Setup */
    private int costPerTick;
    private int ticksNeeded;

    public int progress;
    /*-----------------------*/


    public TileMachineBase(EnumPowerTier tier, int capacity, int costPerTick, int ticksNeeded) {
        super(tier, capacity);

        this.costPerTick = costPerTick;
        this.ticksNeeded = ticksNeeded;
    }

    public boolean canWork() {
        return getEnergy() >= this.costPerTick;
    }

    public void updateCostAndDuration(int costPerTick, int ticksNeeded) {
        this.costPerTick = costPerTick == -1 ? this.costPerTick : costPerTick;
        this.ticksNeeded = ticksNeeded == -1 ? this.ticksNeeded : ticksNeeded;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (canWork()) {
            machineTick();
        }
        else {
            machineResetProgress();
        }
    }

    public void machineResetProgress() {
        boolean needsUpdate = this.progress != 0;
        this.progress = 0;
        if(needsUpdate) {
            markBlockForUpdate();
        }
    }

    public void machineTick() {
        useEnergy(this.costPerTick);
        if(this.progress++ > this.ticksNeeded) {
            this.progress = 0;

            machineFinish();
        }

        markBlockForUpdate();
    }

    public abstract void machineFinish();

    public int gaugeProgressScaled(int scale)
    {
        return (this.progress * scale) / this.ticksNeeded;
    }

    public int getEnergyScaled(int scale) {
        return (int) ((getEnergy() * scale / getMaxPower()));
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, EnumFacing side)
    {
        return false;
    }

    @Override
    public EnumFacing getFacing()
    {
        return getFacingEnum();
    }

    public void setFacing(EnumFacing enumFacing)
    {
        Block block = getWorld().getBlockState(getPos()).getBlock();
        if (block instanceof BlockMachineBase)
        {
            ((BlockMachineBase) block).setFacing(enumFacing, getWorld(), getPos());
        }
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return entityPlayer.isSneaking();
    }

    @Override
    public float getWrenchDropRate()
    {
        return 1.0F;
    }

    @Override
    public abstract ItemStack getWrenchDrop(EntityPlayer p0);

    @Override
    public boolean canAcceptEnergy(EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canProvideEnergy(EnumFacing direction) {
        return false;
    }

    @Override
    public double getMaxOutput() {
        return 0;
    }

    @Override
    public double getMaxInput() {
        return 0;
    }
}
