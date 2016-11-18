package reborncore.common.powerSystem;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.RebornCoreConfig;
import reborncore.common.tile.TileLegacyMachineBase;

import java.util.Random;

/**
 * This is done in a different class so the updateEntity can be striped for ic2
 * and this one will still get called.
 */
public abstract class RFProviderTile extends TileLegacyMachineBase
	implements IEnergyReceiver, IEnergyProvider, IEnergyInterfaceTile {

	Random random = new Random();

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (world.isRemote) {
			return;
		}
		sendPower();
	}

	public void sendPower() {
		if (!RebornCoreConfig.getRebornPower().rf()) {
			return;
		}
		for (EnumFacing direction : EnumFacing.values()) {
			int extracted = getEnergyStored(direction);

			TileEntity tile = world.getTileEntity(new BlockPos(getPos().getX() + direction.getFrontOffsetX(),
				getPos().getY() + direction.getFrontOffsetY(), getPos().getZ() + direction.getFrontOffsetZ()));
			if (isPoweredTile(tile, direction)) {
				if (canProvideEnergy(direction)) {
					if (tile instanceof IEnergyReceiver) {
						IEnergyReceiver handler = (IEnergyReceiver) tile;
						int neededRF = handler.receiveEnergy(direction.getOpposite(), extracted, false);

						extractEnergy(direction.getOpposite(), neededRF, false);
					}
				}

			}
		}
	}

	public boolean isPoweredTile(TileEntity tile, EnumFacing side) {
		if (tile == null) {
			return false;
		} else if (tile instanceof IEnergyHandler || tile instanceof IEnergyReceiver) {
			return ((IEnergyConnection) tile).canConnectEnergy(side.getOpposite());
		} else {
			return false;
		}
	}
}
