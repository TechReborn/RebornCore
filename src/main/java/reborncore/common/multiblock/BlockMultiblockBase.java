package reborncore.common.multiblock;

import me.modmuss50.jsonDestroyer.api.IOpaqueBlock;
import net.minecraft.block.material.Material;
import reborncore.common.BaseTileBlock;

/*
 * Base class for multiblock-capable blocks. This is only a reference implementation
 * and can be safely ignored.
 */
public abstract class BlockMultiblockBase extends BaseTileBlock {

	protected BlockMultiblockBase(Material material) {
		super(material);
	}
}
