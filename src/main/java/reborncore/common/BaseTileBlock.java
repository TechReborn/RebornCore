package reborncore.common;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import reborncore.api.TextureRegistry;


public abstract class BaseTileBlock extends BlockContainer {
    protected BaseTileBlock(Material materialIn) {
        super(materialIn);
        TextureRegistry.registerBlock(this);
    }

    public int getRenderType() {
        return 3;
    }
}
