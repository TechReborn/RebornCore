package reborncore.jsonDestroyers.item;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;
import reborncore.api.IItemTexture;
import reborncore.api.TextureRegistry;
import reborncore.jsonDestroyers.block.CustomTexture;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;


public class ItemModelGenerator {
	public static ItemModelGenerator INSTANCE = new ItemModelGenerator();

	public static List<ItemIconInfo> icons = new ArrayList<ItemIconInfo>();

	public static void register() {
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}


	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event) {
		TextureMap textureMap = event.map;
		icons.clear();

		for (Item item : TextureRegistry.items) {
			if (item instanceof IItemTexture) {
				IItemTexture ItemTexture = (IItemTexture) item;
				for (int i = 0; i < ItemTexture.getMaxMeta(); i++) {
					String name = ItemTexture.getTextureName(i);
					TextureAtlasSprite texture = textureMap.getTextureExtry(name);
					if (texture == null) {
						texture = new CustomTexture(name);
						textureMap.setTextureEntry(name, texture);
					}
					ItemIconInfo info = new ItemIconInfo(item, i, texture, name);
					icons.add(info);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void bakeModels(ModelBakeEvent event) {
		ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		for (Item item : TextureRegistry.items) {
			if (item instanceof IItemTexture) {
				IItemTexture textureProvdier = (IItemTexture) item;
				for (int i = 0; i < textureProvdier.getMaxMeta(); i++) {
					TextureAtlasSprite texture = null;
					ItemIconInfo itemIconInfo = null;
					for (ItemIconInfo info : icons) {
						if (info.damage == i && info.getItem() == item) {
							texture = info.getSprite();
							itemIconInfo = info;
							break;
						}
					}
					if (texture == null) {
						break;
					}
					ModelResourceLocation inventory;
					if (textureProvdier.getMaxMeta() == 1) {
						inventory = TextureRegistry.getItemInventoryResourceLocation(item);
					} else {
						inventory = new ModelResourceLocation(textureProvdier.getModID() + ":" + item.getUnlocalizedName(new ItemStack(item, 1, i)).substring(5), "inventory");
					}

					final TextureAtlasSprite finalTexture = texture;
					Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
						public TextureAtlasSprite apply(ResourceLocation location) {
							return finalTexture;
						}
					};
					ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
					builder.add(new ResourceLocation(itemIconInfo.textureName));
					CustomModel itemLayerModel = new CustomModel(builder.build());
					IBakedModel model = itemLayerModel.bake(ItemLayerModel.instance.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);
					event.modelRegistry.putObject(inventory, model);
					itemModelMesher.register(item, i, inventory);

				}
			}
		}
	}

	public class CustomModel extends ItemLayerModel {

		ImmutableList<ResourceLocation> textures;

		public CustomModel(ImmutableList<ResourceLocation> textures) {
			super(textures);
			this.textures = textures;
		}

		@Override
		public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
			Optional<TRSRTransformation> transform = state.apply(Optional.<IModelPart>absent());
			for (int i = 0; i < textures.size(); i++) {
				TextureAtlasSprite sprite = bakedTextureGetter.apply(textures.get(i));
				builder.addAll(getQuadsForSprite(i, sprite, format, transform));
			}
			TextureAtlasSprite particle = bakedTextureGetter.apply(textures.isEmpty() ? new ResourceLocation("missingno") : textures.get(0));
			ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = IPerspectiveAwareModel.MapWrapper.getTransforms(state);
			IFlexibleBakedModel ret = new CustomBakedModel(builder.build(), particle, format, transforms);
			if (transforms.isEmpty()) {
				return ret;
			}
			return new IPerspectiveAwareModel.MapWrapper(ret, transforms);
		}
	}

	public class CustomBakedModel extends ItemLayerModel.BakedModel implements IPerspectiveAwareModel {

		private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

		public CustomBakedModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
			super(quads, particle, format);
			this.transforms = transforms;
		}

		@Override
		public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
			if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON)
				return Pair.of(IFlexibleBakedModel.class.cast(this), FIRST_PERSON_FIX);

			if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON) {
				return Pair.of(IFlexibleBakedModel.class.cast(this), THIRD_PERSON_2D);
			}
			return Pair.of(IFlexibleBakedModel.class.cast(this), null);
		}

		public final Matrix4f THIRD_PERSON_2D = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(4.8F, 0, 0F), new Vector3f(0, .07F, -0.2F), new Vector3f(0.55F, 0.55F, 0.55F)));
		public final Matrix4f FIRST_PERSON_FIX = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(0, 4, 0.5F), new Vector3f(-0.1F, 0.3F, 0.1F), new Vector3f(1.3F, 1.3F, 1.3F)));

	}

}
