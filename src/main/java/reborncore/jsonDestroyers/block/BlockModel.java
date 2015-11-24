package reborncore.jsonDestroyers.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class BlockModel implements IBakedModel {
    HashMap<EnumFacing, TextureAtlasSprite> textureAtlasSpriteHashMap;
    IBlockState state;

    protected static FaceBakery faceBakery = new FaceBakery();


    public BlockModel(HashMap<EnumFacing, TextureAtlasSprite> textureAtlasSpriteHashMap, IBlockState state) {
        this.textureAtlasSpriteHashMap = textureAtlasSpriteHashMap;
        this.state = state;
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
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 0.0F, 16.0F), face, textureAtlasSpriteHashMap.get(EnumFacing.DOWN), EnumFacing.DOWN, modelRot, null, scale, true));//down
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textureAtlasSpriteHashMap.get(EnumFacing.UP), EnumFacing.UP, modelRot, null, scale, true));//up
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 0.0F), face, textureAtlasSpriteHashMap.get(EnumFacing.NORTH), EnumFacing.NORTH, modelRot, null, scale, true));//north
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textureAtlasSpriteHashMap.get(EnumFacing.SOUTH), EnumFacing.SOUTH, modelRot, null, scale, true));//south
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textureAtlasSpriteHashMap.get(EnumFacing.EAST), EnumFacing.EAST, modelRot, null, scale, true));//east
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 16.0F, 16.0F), face, textureAtlasSpriteHashMap.get(EnumFacing.WEST), EnumFacing.WEST, modelRot, null, scale, true));//west


        return list;
    }


    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return textureAtlasSpriteHashMap.get(EnumFacing.DOWN);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }


}
