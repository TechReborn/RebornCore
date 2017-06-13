package reborncore.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BaseBlock extends Block {

	public BaseBlock(Material materialIn) {
		super(materialIn);
	}

	public int getRenderType() {
		return 3;
	}
}
