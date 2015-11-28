package reborncore.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import reborncore.api.TextureRegistry;

public abstract class BaseBlock extends Block {

    public BaseBlock(Material materialIn) {
        super(materialIn);
        TextureRegistry.registerBlock(this);
    }

    public int getRenderType() {
        return 3;
    }
}
