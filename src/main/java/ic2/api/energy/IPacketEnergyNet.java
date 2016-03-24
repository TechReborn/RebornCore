package ic2.api.energy;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

public interface IPacketEnergyNet extends IEnergyNet
{
	List<PacketStat> getSendedPackets(TileEntity p0);

	List<PacketStat> getTotalSendedPackets(TileEntity p0);
}
