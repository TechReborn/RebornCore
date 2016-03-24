package ic2.api.energy.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.WorldEvent;
import ic2.api.energy.tile.IEnergyTile;

public class EnergyTileEvent extends WorldEvent
{
	public final IEnergyTile energyTile;

	public EnergyTileEvent(final IEnergyTile energyTile1)
	{
		super(((TileEntity) energyTile1).getWorld());
		if (this.world == null)
		{
			throw new NullPointerException("world is null");
		}
		this.energyTile = energyTile1;
	}
}
