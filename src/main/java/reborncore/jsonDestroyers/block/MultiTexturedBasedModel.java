package reborncore.jsonDestroyers.block;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;

public class MultiTexturedBasedModel implements IBakedModel {
    protected final List<BakedQuad> generalQuads;
    protected final List<List<BakedQuad>> faceQuads;
    protected final boolean ambientOcclusion;
    protected final boolean gui3d;
    protected final HashMap<EnumFacing, TextureAtlasSprite> texture;
    protected final ItemCameraTransforms cameraTransforms;

    public MultiTexturedBasedModel(List<BakedQuad> generalQuads, List<List<BakedQuad>> faceQuads, boolean ambientOcclusion, boolean gui3d, HashMap<EnumFacing, TextureAtlasSprite> texture, ItemCameraTransforms cameraTransforms) {
        this.generalQuads = generalQuads;
        this.faceQuads = faceQuads;
        this.ambientOcclusion = ambientOcclusion;
        this.gui3d = gui3d;
        this.texture = texture;
        this.cameraTransforms = cameraTransforms;
    }

    public List<BakedQuad> getFaceQuads(EnumFacing side) {
        return (List) this.faceQuads.get(side.ordinal());
    }

    public List<BakedQuad> getGeneralQuads() {
        return this.generalQuads;
    }

    public boolean isAmbientOcclusion() {
        return this.ambientOcclusion;
    }

    public boolean isGui3d() {
        return this.gui3d;
    }

    public boolean isBuiltInRenderer() {
        return false;
    }

    public TextureAtlasSprite getParticleTexture() {
        return texture.get(EnumFacing.DOWN);
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return this.cameraTransforms;
    }

    @SideOnly(Side.CLIENT)
    public static class Builder {
        private final List<BakedQuad> builderGeneralQuads;
        private final List<List<BakedQuad>> builderFaceQuads;
        private final boolean builderAmbientOcclusion;
        private HashMap<EnumFacing, TextureAtlasSprite> builderTexture;
        private boolean builderGui3d;
        private ItemCameraTransforms builderCameraTransforms;

        public Builder(ModelBlock modelBlock) {
            this(modelBlock.isAmbientOcclusion(), modelBlock.isGui3d(), modelBlock.func_181682_g());
        }

        public Builder(IBakedModel iBakedModel, HashMap<EnumFacing, TextureAtlasSprite> builderTexture) {
            this(iBakedModel.isAmbientOcclusion(), iBakedModel.isGui3d(), iBakedModel.getItemCameraTransforms());
            this.builderTexture = builderTexture;

            for (EnumFacing enumfacing : EnumFacing.values()) {
                this.addFaceBreakingFours(iBakedModel, builderTexture.get(enumfacing), enumfacing);
            }

            this.addGeneralBreakingFours(iBakedModel, builderTexture.get(EnumFacing.DOWN));
        }

        private void addFaceBreakingFours(IBakedModel iBakedModel, TextureAtlasSprite textureAtlasSprite, EnumFacing facing) {
            for (BakedQuad bakedquad : iBakedModel.getFaceQuads(facing)) {
                this.addFaceQuad(facing, new BreakingFour(bakedquad, textureAtlasSprite));
            }
        }

        private void addGeneralBreakingFours(IBakedModel iBakedModel, TextureAtlasSprite textureAtlasSprite) {
            for (BakedQuad bakedquad : iBakedModel.getGeneralQuads()) {
                this.addGeneralQuad(new BreakingFour(bakedquad, textureAtlasSprite));
            }
        }

        private Builder(boolean builderAmbientOcclusion, boolean builderGui3d, ItemCameraTransforms builderCameraTransforms) {
            this.builderGeneralQuads = Lists.<BakedQuad>newArrayList();
            this.builderFaceQuads = Lists.<List<BakedQuad>>newArrayListWithCapacity(6);

            for (EnumFacing enumfacing : EnumFacing.values()) {
                this.builderFaceQuads.add(Lists.<BakedQuad>newArrayList());
            }

            this.builderAmbientOcclusion = builderAmbientOcclusion;
            this.builderGui3d = builderGui3d;
            this.builderCameraTransforms = builderCameraTransforms;
        }

        public MultiTexturedBasedModel.Builder addFaceQuad(EnumFacing enumFacing, BakedQuad bakedQuad) {
            ((List) this.builderFaceQuads.get(enumFacing.ordinal())).add(bakedQuad);
            return this;
        }

        public MultiTexturedBasedModel.Builder addGeneralQuad(BakedQuad bakedQuad) {
            this.builderGeneralQuads.add(bakedQuad);
            return this;
        }

        public MultiTexturedBasedModel.Builder setTexture(HashMap<EnumFacing, TextureAtlasSprite> textureAtlasSprite) {
            this.builderTexture = textureAtlasSprite;
            return this;
        }

        public IBakedModel makeBakedModel() {
            if (this.builderTexture == null) {
                throw new RuntimeException("Missing particle!");
            } else {
                return new MultiTexturedBasedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.builderGui3d, this.builderTexture, this.builderCameraTransforms);
            }
        }
    }
}
