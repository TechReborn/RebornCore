package ic2.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface INetworkItemEventListener
{
	void onNetworkEvent(ItemStack p0, EntityPlayer p1, int p2);
}
