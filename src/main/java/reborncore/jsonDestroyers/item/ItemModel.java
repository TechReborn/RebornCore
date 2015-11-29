package reborncore.jsonDestroyers.item;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mark on 24/11/2015.
 */
public class ItemModel implements IBakedModel, IPerspectiveAwareModel {
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
    public TextureAtlasSprite getParticleTexture() {
        return textureAtlas;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }


    @Override
    public Pair<IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON)
            return Pair.of(IBakedModel.class.cast(this), FIRST_PERSON_FIX);

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON) {
            return Pair.of(IBakedModel.class.cast(this), THIRD_PERSON_2D);
        }
        return Pair.of(IBakedModel.class.cast(this), null);
    }

    public static final Matrix4f THIRD_PERSON_2D = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(4.8F, 0, 0F), new Vector3f(0, .07F, -0.2F), new Vector3f(0.55F, 0.55F, 0.55F)));
    public static final Matrix4f FIRST_PERSON_FIX = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(0, 4, 0.5F), new Vector3f(-0.1F, 0.3F, 0.1F), new Vector3f(1.3F, 1.3F, 1.3F)));

}