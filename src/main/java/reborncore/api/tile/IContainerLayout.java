package reborncore.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by modmuss50 on 12/04/2016.
 */
public interface IContainerLayout<T extends TileEntity> {

    public void addInventorySlots();

    public void addPlayerSlots();

    public void setTile(T tile);

    public @Nullable T getTile();

    public void setPlayer(EntityPlayer player);

    public @Nullable EntityPlayer getPlayer();

    public @Nullable List<Integer> getSlotsForSide(EnumFacing facing);
}
