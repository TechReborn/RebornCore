package ic2.api.energy.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

public interface IMetaDelegate extends IEnergyTile
{
	List<TileEntity> getSubTiles();
}
