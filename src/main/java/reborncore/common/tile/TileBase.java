package reborncore.common.tile;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by Mark on 19/10/2016.
 */
public class TileBase extends TileEntity {

	public void markBlockForUpdate() {
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
	}
}
