/*
 * This file is part of RebornCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 TeamReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.common.util.ItemDurabilityExtensions;

import org.jetbrains.annotations.Nullable;

//Not too happy with this, need to find and get a better solution into fabric soon
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow
	protected abstract void renderGuiQuad(BufferBuilder bufferBuilder_1, int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, int int_8);

	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
	private void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String string, CallbackInfo info) {
		if (stack.getItem() instanceof ItemDurabilityExtensions) {
			ItemDurabilityExtensions durabilityExtensions = (ItemDurabilityExtensions) stack.getItem();
			if (!durabilityExtensions.showDurability(stack)) {
				return;
			}
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			//RenderSystem.disableAlphaTest(); FIXME 1.17
			RenderSystem.disableBlend();

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();

			int durability = (int) (13 * (1 - Math.max(0.0F, durabilityExtensions.getDurability(stack))));
			int color = durabilityExtensions.getDurabilityColor(stack);

			this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
			this.renderGuiQuad(bufferBuilder, x + 2, y + 13, durability, 1, color >> 16 & 255, color >> 8 & 255, color & 255, 255);

			RenderSystem.enableBlend();
			//RenderSystem.enableAlphaTest();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}
	}

}
