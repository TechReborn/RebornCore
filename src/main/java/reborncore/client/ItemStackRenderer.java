package reborncore.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;

/**
 * Initially take from https://github.com/JamiesWhiteShirt/developer-mode/tree/experimental-item-render and then ported to 1.15
 * Thanks 2xsaiko for fixing the lighting + odd issues above
 */
public class ItemStackRenderer implements HudRenderCallback {

	@Override
	public void onHudRender(float v) {
		if (!ItemStackRenderManager.RENDER_QUEUE.isEmpty()) {

			MinecraftClient.getInstance().textRenderer.draw("Rendering " + ItemStackRenderManager.RENDER_QUEUE.size() + " items left", 5, 5, -1);

			ItemStack itemStack = ItemStackRenderManager.RENDER_QUEUE.poll();
			export(itemStack, 512, Registry.ITEM.getId(itemStack.getItem()));
		}
	}

	private void export(ItemStack stack, int size, Identifier identifier) {
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
		RenderSystem.ortho(-1, 1, 1, -1, -100.0, 100.0);
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();

		{
			minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			minecraft.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);

			RenderSystem.enableRescaleNormal();
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			MatrixStack matrixStack = new MatrixStack();

			matrixStack.scale(2F, -2F, 1F);

			boolean frontLit = !model.isSideLit();
			if (frontLit) {
				DiffuseLighting.disableGuiDepthLighting();
			}

			VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
			immediate.draw();

			RenderSystem.enableDepthTest();

			if (frontLit) {
				DiffuseLighting.enableGuiDepthLighting();
			}

			RenderSystem.disableAlphaTest();
			RenderSystem.disableRescaleNormal();
		}

		RenderSystem.popMatrix();
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
