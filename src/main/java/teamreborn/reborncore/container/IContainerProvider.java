package teamreborn.reborncore.container;

import net.minecraft.entity.player.EntityPlayer;

public interface IContainerProvider
{
    public BuiltContainer createContainer(EntityPlayer player);
}
