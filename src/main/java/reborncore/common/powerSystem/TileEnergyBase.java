package reborncore.common.powerSystem;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.*;
import ic2.api.info.Info;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import reborncore.api.IListInfoProvider;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.api.power.IPowerConfig;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.forge.ForgePowerManager;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.common.tile.TileBase;
import reborncore.common.util.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Lordmau5 on 08.06.2016.
 */
@Optional.InterfaceList(value = {@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2")})
public abstract class TileEnergyBase extends TileBase implements IEnergyInterfaceTile, IListInfoProvider, ITickable, IEnergyReceiver, IEnergyProvider, IEnergyTile, IEnergySink, IEnergySource {

    /* Power storage Setup */
    public EnumPowerTier tier;
    private double energy;
    private int capacity;
	private ForgePowerManager forgePowerManager;
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
//	    if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
//		    TeslaManager.manager.update(this);
//	    }
	    //TOOD re enable when fixing ic2 support
	    //onLoaded();
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

	public int getEnergyScaled(int scale) {
		return (int) ((energy * scale / getMaxPower()));
	}

	public IPowerConfig getPowerConfig() {
		return RebornCoreConfig.getRebornPower();
	}

//	@Override
//	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
//		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
//			if(TeslaManager.manager.hasCapability(capability, facing, this)){
//				return true;
//			}
//		}
//		if(getPowerConfig().forge()){
//			if(capability == CapabilityEnergy.ENERGY){
//				return true;
//			}
//		}
//		return super.hasCapability(capability, facing);
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
//		if (TeslaManager.isTeslaEnabled(getPowerConfig())) {
//			T teslaCap = TeslaManager.manager.getCapability(capability, facing, this);
//			if(capability != null){
//				return teslaCap;
//			}
//		}
//		if(getPowerConfig().forge()){
//			if(capability == CapabilityEnergy.ENERGY){
//				if(forgePowerManager == null){
//					forgePowerManager = new ForgePowerManager(this, facing);
//				}
//				forgePowerManager.setFacing(facing);
//				return (T) forgePowerManager;
//			}
//		}
//		return super.getCapability(capability, facing);
//	}


	// COFH
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if (!getPowerConfig().rf())
			return false;
		return canAcceptEnergy(from) || canProvideEnergy(from);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!getPowerConfig().rf())
			return 0;
		if (!canAcceptEnergy(from)) {
			return 0;
		}
		maxReceive *= RebornCoreConfig.euPerRF;
		int energyReceived = Math.min(getMaxEnergyStored(null) - getEnergyStored(null),
			Math.min((int) this.getMaxInput() * RebornCoreConfig.euPerRF, maxReceive));

		if (!simulate) {
			setEnergy(getEnergy() + energyReceived);
		}
		return energyReceived / RebornCoreConfig.euPerRF;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		if (!getPowerConfig().rf())
			return 0;
		return ((int) getEnergy() * RebornCoreConfig.euPerRF);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if (!getPowerConfig().rf())
			return 0;
		return ((int) getMaxPower() * RebornCoreConfig.euPerRF);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (!getPowerConfig().rf())
			return 0;
		if (!canProvideEnergy(from)) {
			return 0;
		}
		maxExtract *= RebornCoreConfig.euPerRF;
		int energyExtracted = Math.min(getEnergyStored(null), Math.min(maxExtract, maxExtract));

		if (!simulate) {
			setEnergy(energy - energyExtracted);
		}
		return energyExtracted * RebornCoreConfig.euPerRF;
	}
	// END COFH

	// IC2

	protected boolean addedToEnet;

	@Optional.Method(modid = "IC2")
	public void onLoaded() {
		if (getPowerConfig().eu() && !addedToEnet &&
			!FMLCommonHandler.instance().getEffectiveSide().isClient() &&
			Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));

			addedToEnet = true;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void onChunkUnload() {
		super.onChunkUnload();
		if (getPowerConfig().eu()) {
			if (addedToEnet && Info.isIc2Available()) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));

				addedToEnet = false;
			}
		}
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double getDemandedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return Math.min(getMaxPower() - getEnergy(), getMaxInput());
	}

	@Override
	@Optional.Method(modid = "IC2")
	public int getSinkTier() {
		return tier.ordinal();
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		setEnergy(getEnergy() + amount);
		return 0;
	}

	@Override
	@Optional.Method(modid = "IC2")
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canAcceptEnergy(enumFacing);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
		if (!RebornCoreConfig.getRebornPower().eu())
			return false;
		return canProvideEnergy(enumFacing);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double getOfferedEnergy() {
		if (!RebornCoreConfig.getRebornPower().eu())
			return 0;
		return Math.min(getEnergy(), getMaxOutput());
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void drawEnergy(double amount) {
		useEnergy((int) amount);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public int getSourceTier() {
		return tier.ordinal();
	}
	// END IC2

}
