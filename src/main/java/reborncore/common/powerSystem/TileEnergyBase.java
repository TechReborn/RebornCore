package reborncore.common.powerSystem;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Lordmau5 on 08.06.2016.
 */
public abstract class TileEnergyBase extends TileEntity implements IEnergyInterfaceTile, IListInfoProvider, ITickable {

    /* Power storage Setup */
    public EnumPowerTier tier;
    private double energy;
    private int capacity;
    /*-----------------------*/

    public TileEnergyBase(EnumPowerTier tier, int capacity) {
        this.tier = tier;
        this.capacity = capacity;
    }

    public void updateEntity() {

    }

    @Override
    public void update() {
        if(!getWorld().isRemote) {
            updateEntity();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.energy = compound.getDouble("energy");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setDouble("energy", this.energy);

        return super.writeToNBT(compound);
    }

    public void markBlockForUpdate() {
        getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    public EnumFacing getFacingEnum() {
        Block block = getWorld().getBlockState(getPos()).getBlock();
        if (block instanceof BlockMachineBase) {
            return ((BlockMachineBase) block).getFacing(getWorld().getBlockState(getPos()));
        }
        return null;
    }

    @Override
    public void addInfo(List<String> info, boolean isRealTile) {
        info.add(TextFormatting.LIGHT_PURPLE + "Energy buffer Size " + TextFormatting.GREEN
                + PowerSystem.getLocaliszedPower(getMaxPower()));
        if (getMaxInput() > 0) {
            info.add(TextFormatting.LIGHT_PURPLE + "Max Input " + TextFormatting.GREEN
                    + PowerSystem.getLocaliszedPower(getMaxInput()));
        }
        if (getMaxOutput() > 0) {
            info.add(TextFormatting.LIGHT_PURPLE + "Max Output " + TextFormatting.GREEN
                    + PowerSystem.getLocaliszedPower(getMaxOutput()));
        }
        info.add(TextFormatting.LIGHT_PURPLE + "Tier " + TextFormatting.GREEN + getTier());
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = Math.min(0, Math.max(this.capacity, energy));
    }

    @Override
    public double getMaxPower() {
        return this.capacity;
    }

    @Override
    public boolean canAddEnergy(double energy) {
        return this.energy + energy <= getMaxPower();
    }

    @Override
    public double addEnergy(double energy) {
        return addEnergy(energy, false);
    }

    @Override
    public double addEnergy(double energy, boolean simulate) {
        double _taken = energy - ((this.energy + energy > this.capacity) ? (this.energy + energy - this.capacity) : 0);
        if(!simulate) {
            this.energy = Math.min(this.energy + energy, this.capacity);
            if(this.energy > this.capacity) {
                this.energy = this.capacity;
            }
        }
        return _taken;
    }

    @Override
    public boolean canUseEnergy(double energy) {
        return energy <= this.energy;
    }

    @Override
    public double useEnergy(double energy) {
        return useEnergy(energy, false);
    }

    @Override
    public double useEnergy(double energy, boolean simulate) {
        double _used = energy - ((this.energy - energy < 0) ? (0 - this.energy - energy) : 0);
        if(!simulate) {
            this.energy = Math.max(0, this.energy - energy);
        }
        return _used;
    }

    @Override
    public double getMaxOutput() {
        return this.tier.getMaxOutput();
    }

    @Override
    public double getMaxInput() {
        return this.tier.getMaxInput();
    }

    @Override
    public EnumPowerTier getTier() {
        return this.tier;
    }
    
}
