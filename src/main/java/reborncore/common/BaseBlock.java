package reborncore.common;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.IBakedModel;

public abstract class BaseBlock extends BlockContainer {

    public IBakedModel[] models;
    public IBakedModel[] invmodels;

    public BaseBlock(Material materialIn) {
        super(materialIn);
    }

    public int getRenderType() {
        return 3;
    }
}
