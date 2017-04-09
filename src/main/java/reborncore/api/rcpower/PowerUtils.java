package reborncore.api.rcpower;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import reborncore.common.capabilitys.PowerCapabilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class PowerUtils
{
    public static boolean isPowerHolder(ICapabilityProvider provider, EnumFacing facing)
    {
        return provider.hasCapability(PowerCapabilities.CAPABILITY_HOLDER, facing);
    }

    public static boolean ispowerConsumer(ICapabilityProvider provider, EnumFacing facing)
    {
        return provider.hasCapability(PowerCapabilities.CAPABILITY_CONSUMER, facing);
    }

    public static boolean isPowerProducer(ICapabilityProvider provider, EnumFacing facing)
    {
        return provider.hasCapability(PowerCapabilities.CAPABILITY_PRODUCER, facing);
    }

    public static <T> List<T> getConnectedCapabilities (Capability<T> capability, World world, BlockPos pos)
    {
        final List<T> capabilities = new ArrayList<T>();

        for (final EnumFacing side : EnumFacing.values())
        {
            final TileEntity tile = world.getTileEntity(pos.offset(side));

            if (tile != null && !tile.isInvalid() && tile.hasCapability(capability, side.getOpposite()))
                capabilities.add(tile.getCapability(capability, side.getOpposite()));
        }
        return capabilities;
    }

    public static long distributePowerToAllFaces(World world, BlockPos pos, int amount, boolean simulated)
    {
        long consumedPower = 0L;
        for (final IPowerConsumer consumer : getConnectedCapabilities(PowerCapabilities.CAPABILITY_CONSUMER, world, pos))
            consumedPower += consumer.givePower(amount, simulated);
        return consumedPower;
    }
}
