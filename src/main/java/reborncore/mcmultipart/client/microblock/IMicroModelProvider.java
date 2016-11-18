package reborncore.mcmultipart.client.microblock;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import reborncore.mcmultipart.microblock.IMicroMaterial;

import java.util.EnumSet;

public interface IMicroModelProvider {

	public IBakedModel provideMicroModel(IMicroMaterial material, AxisAlignedBB bounds, EnumSet<EnumFacing> hiddenFaces);

}
