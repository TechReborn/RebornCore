package ic2.api.network;

import net.minecraft.entity.player.EntityPlayer;

public interface INetworkClientTileEntityEventListener
{
	void onNetworkEvent(EntityPlayer p0, int p1);
}
