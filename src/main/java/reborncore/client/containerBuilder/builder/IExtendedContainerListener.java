package reborncore.client.containerBuilder.builder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.PacketSendLong;
import reborncore.common.network.packet.PacketSendObject;

public interface IExtendedContainerListener  {

	public default void sendLong(IContainerListener containerListener, Container containerIn, int var, long value){
		if(containerListener instanceof EntityPlayerMP){
			NetworkManager.sendToPlayer(new PacketSendLong(var, value, containerIn), (EntityPlayerMP) containerListener);
		}
	}

	public default void sendObject(IContainerListener containerListener, Container containerIn, int var, Object value){
		if(containerListener instanceof EntityPlayerMP){
			NetworkManager.sendToPlayer(new PacketSendObject(var, value, containerIn), (EntityPlayerMP) containerListener);
		}
	}

	public default void handleLong(int var, long value){

	}

	public default void handleObject(int var, Object value){

	}
}
