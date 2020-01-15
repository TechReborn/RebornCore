package reborncore.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;

//Very broken, roughly ported from a 1.14 project, ive forgotten where the ordinal code came from (let me know and ill credit you or remove it)
public class ItemStackRenderer {

	public static void process() {
		if (!ItemStackRenderManager.RENDER_QUEUE.isEmpty()) {
			Identifier identifier = ItemStackRenderManager.RENDER_QUEUE.poll();
			Item item = Registry.ITEM.get(identifier);
			ItemStack itemStack = new ItemStack(item);
			export(itemStack, 512, identifier);
		}
	}

	private static void export(ItemStack stack, int size, Identifier identifier) {
		File dir = new File(FabricLoader.getInstance().getGameDirectory(), "item_renderer/" + identifier.getNamespace());
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, identifier.getPath() + ".png");

		if (file.exists()) {
			file.delete();
		}

		MinecraftClient minecraft = MinecraftClient.getInstance();

		if(minecraft.getItemRenderer() == null || minecraft.world == null) {
			return;
		}

		final Framebuffer framebuffer = new Framebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

		framebuffer.beginWrite(true);

		final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		final BakedModel model = itemRenderer.getHeldItemModel(stack, minecraft.world, minecraft.player);

		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();

		{
			minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			minecraft.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);

			RenderSystem.enableRescaleNormal();
			RenderSystem.enableAlphaTest();
			RenderSystem.alphaFunc(516, 0.1F);
			RenderSystem.enableBlend();

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			MatrixStack matrixStack = new MatrixStack();

			matrixStack.scale(2F, 2F, 1F);

			VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

			boolean disableDepth = !model.method_24304();
			if (disableDepth) {
				DiffuseLighting.disableGuiDepthLighting();
			}

			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180));

			itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
			immediate.draw();

			if (disableDepth) {
				DiffuseLighting.enableGuiDepthLighting();
			}

			RenderSystem.disableAlphaTest();
			RenderSystem.disableRescaleNormal();
		}
		RenderSystem.matrixMode(GL11.GL_PROJECTION);
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);

		framebuffer.endWrite();


		try (NativeImage nativeImage = new NativeImage(size, size, false)) {
			GlStateManager.bindTexture(framebuffer.colorAttachment);
			nativeImage.loadFromTextureImage(0, false);
			nativeImage.mirrorVertically();

			try {
				byte[] bytes = nativeImage.getBytes();
				FileUtils.writeByteArrayToFile(file, bytes);
				System.out.println("Wrote " + file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		framebuffer.delete();
	}

}
