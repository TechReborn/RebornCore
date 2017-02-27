package teamreborn.reborncore.container;

import net.minecraft.entity.player.EntityPlayer;

public interface IContainerProvider
{

	/**
	 * Method called to create a new instance of a BuiltContainer linked to the specified EntityPlayer.
	 * Used along the ContainerBuilder.
	 *
	 * @param player
	 * @return
	 */
	public BuiltContainer createContainer(EntityPlayer player);
}
