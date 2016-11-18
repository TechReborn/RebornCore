package reborncore.mcmultipart.microblock;

import net.minecraft.util.EnumFacing;
import reborncore.mcmultipart.multipart.IMultipart;

public interface ICenterConnectablePart extends IMultipart {

	public int getHoleRadius(EnumFacing side);

}
