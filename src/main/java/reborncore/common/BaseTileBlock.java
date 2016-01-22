package reborncore.common;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import reborncore.RebornCore;


public abstract class BaseTileBlock extends BlockContainer {
    protected BaseTileBlock(Material materialIn) {
        super(materialIn);
        RebornCore.jsonDestroyer.registerObject(this);
    }

    public int getRenderType() {
        return 3;
    }
}
