package reborncore.jsonDestroyers.item;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mark on 24/11/2015.
 */
public class ItemModel implements IBakedModel {
    TextureAtlasSprite textureAtlas;

    protected static FaceBakery faceBakery = new FaceBakery();

    public ItemModel(TextureAtlasSprite textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side) {
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace face = new BlockPartFace(null, 0, "", uv);

        ModelRotation modelRot = ModelRotation.X0_Y0;
        boolean scale = true;
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textureAtlas, EnumFacing.SOUTH, modelRot, null, scale, true));//south
        return list;
    }


    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return textureAtlas;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }


}