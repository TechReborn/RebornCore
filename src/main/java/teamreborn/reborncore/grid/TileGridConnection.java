package teamreborn.reborncore.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import teamreborn.reborncore.api.power.GridJoinEvent;
import teamreborn.reborncore.api.power.GridLeaveEvent;
import teamreborn.reborncore.api.power.IGridConnection;

import javax.annotation.Nullable;

/**
 * Created by modmuss50 on 24/04/2017.
 */
public class TileGridConnection extends TileEntity implements IGridConnection {

	PowerGrid grid;

	@Nullable
	@Override
	public PowerGrid getPowerGrid() {
		return grid;
	}

	@Override
	public void setPowerGrid(
		@Nullable
			PowerGrid powerGrid) {
		grid = powerGrid;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		MinecraftForge.EVENT_BUS.post(new GridJoinEvent(world, pos, this));
	}

	@Override
	public void invalidate() {
		super.invalidate();
		MinecraftForge.EVENT_BUS.post(new GridLeaveEvent(world, pos, this));
	}

}
