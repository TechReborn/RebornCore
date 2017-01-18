package reborncore.client.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.item.ItemStack;
import reborncore.api.item.IColoredDamageBar;
import reborncore.client.RenderUtil;
import reborncore.mixin.api.Mixin;
import reborncore.mixin.api.Rewrite;

import javax.annotation.Nullable;

@Mixin(target = "net.minecraft.client.renderer.RenderItem")
public class MixinRenderItem {

	@Rewrite(target = "renderItemOverlayIntoGUI", targetSRG = "func_180453_a")
	public void renderItemOverlayIntoGUI(FontRenderer fr,
	                                     ItemStack stack,
	                                     int xPosition,
	                                     int yPosition,
	                                     @Nullable
		                                     String text) {
		if (stack != null) {
			if (stack.getItem() instanceof IColoredDamageBar) {
				if (((IColoredDamageBar) stack.getItem()).showRGBDurabilityBar(stack)) {
					//Basicy a copy/pasta from mc.
					double health = stack.getItem().getDurabilityForDisplay(stack);
					int rgbfordisplay = ((IColoredDamageBar) stack.getItem()).getRGBDurabilityForBar(stack);
					int j = (int) Math.round(13.0D - health * 13.0D);
					int i = (int) Math.round(255.0D - health * 255.0D);
					GlStateManager.disableLighting();
					GlStateManager.disableDepth();
					GlStateManager.disableTexture2D();
					GlStateManager.disableAlpha();
					GlStateManager.disableBlend();
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vertexbuffer = tessellator.getBuffer();
					RenderUtil.drawBar(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
					RenderUtil.drawBar(vertexbuffer, xPosition + 2, yPosition + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
					RenderUtil.drawBar(vertexbuffer, xPosition + 2, yPosition + 13, j, 1, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
					GlStateManager.enableBlend();
					GlStateManager.enableAlpha();
					GlStateManager.enableTexture2D();
					GlStateManager.enableLighting();
					GlStateManager.enableDepth();
				}
			}
		}
	}
}
