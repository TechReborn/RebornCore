package reborncore.common.logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.registration.impl.PacketRegistry;

import java.io.IOException;

/**
 * Created by Gigabit101 on 16/04/2017.
 */
@PacketRegistry(proccessingSide = Side.SERVER)
public class PacketButtonID implements INetworkPacket<PacketButtonID> {
	private int ID;
	private BlockPos pos;

	public PacketButtonID(BlockPos pos, int id) {
		this.pos = pos;
		this.ID = id;
	}

	public PacketButtonID() {}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeInt(ID);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		ID = buffer.readInt();
	}

	@Override
	public void processData(PacketButtonID message, MessageContext context) {
		World world = context.getServerHandler().playerEntity.world;
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof LogicController) {
			LogicController controller = (LogicController) world.getTileEntity(pos);
			controller.actionPerformed(ID);
		}
	}
}
