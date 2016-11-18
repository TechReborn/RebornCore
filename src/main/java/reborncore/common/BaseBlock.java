package reborncore.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import reborncore.RebornCore;

public abstract class BaseBlock extends Block {

	public BaseBlock(Material materialIn) {
		super(materialIn);
		RebornCore.jsonDestroyer.registerObject(this);
	}

	public int getRenderType() {
		return 3;
	}
}
