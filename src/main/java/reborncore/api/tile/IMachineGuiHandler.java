package reborncore.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMachineGuiHandler {

    void open(EntityPlayer player, BlockPos pos, World world);

}
