package reborncore.common.advanced;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.RebornCore;
import reborncore.RebornRegistry;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class AdvancedUtils
{
    //use in your GuiHandler
    public static Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof AdvancedTileEntity)
        {
            AdvancedTileEntity machine = (AdvancedTileEntity) world.getTileEntity(new BlockPos(x, y, z));
            return new AdvancedContainer(player, machine);
        }
        return null;
    }

    //use in your GuiHandler
    public static Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof AdvancedTileEntity)
        {
            AdvancedTileEntity machine = (AdvancedTileEntity) world.getTileEntity(new BlockPos(x, y, z));
            return new AdvancedGui(player, machine);
        }
        return null;
    }

    public static void registerAdvanced(Block block, AdvancedTileEntity advancedTileEntity)
    {
        RebornRegistry.registerBlock(block, advancedTileEntity.getName());
        GameRegistry.registerTileEntity(advancedTileEntity.getClass(), advancedTileEntity.getName());
    }

    public static void openGui(EntityPlayer player, AdvancedTileEntity machine)
    {
        if (!player.isSneaking())
        {
            player.openGui(RebornCore.INSTANCE, 0, machine.getWorld(), machine.getPos().getX(), machine.getPos().getY(), machine.getPos().getZ());
        }
    }
}
