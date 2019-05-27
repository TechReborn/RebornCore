/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.client.hud;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.api.power.ItemPowerManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.item.ItemStack.EMPTY;

/**
 * Created by Prospector
 */
public class StackInfoHUD {

	public static final StackInfoHUD instance = new StackInfoHUD();
	public static List<StackInfoElement> ELEMENTS = new ArrayList<>();
	private static MinecraftClient mc = MinecraftClient.getInstance();
	private int x = 2;
	private int y = 7;

	public static void registerElement(StackInfoElement element) {
		ELEMENTS.add(element);
	}

	//TODO needs porting
//	@Environment(EnvType.CLIENT)
//	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
//		if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
//			return;
//		}
//
//		if (mc.isWindowFocused() || (mc.currentScreen != null && mc.options.debugEnabled)) {
//			if (RebornCoreConfig.ShowStackInfoHUD) {
//				drawStackInfoHud(MinecraftClient.getInstance().window);
//			}
//		}
//	}

	public void drawStackInfoHud(Window res) {
		PlayerEntity player = mc.player;
		List<ItemStack> stacks = new ArrayList<>();
		for (ItemStack stack : player.getArmorItems()) {
			stacks.add(stack);
		}
		stacks.add(player.getOffHandStack());
		stacks.add(player.getMainHandStack());

		x = RebornCoreConfig.stackInfoX;

		if (RebornCoreConfig.stackInfoCorner == 2 || RebornCoreConfig.stackInfoCorner == 3) {
			stacks = Lists.reverse(stacks);
			// 20 for line height and additionally padding from configuration file
			y = res.getScaledHeight() - 20 - RebornCoreConfig.stackInfoY;
		} else {
			y = RebornCoreConfig.stackInfoY;
		}

		for (ItemStack stack : stacks) {
			addInfo(stack, res);
		}
	}

	public void renderItemStack(ItemStack stack, int x, int y) {
		if (stack != EMPTY) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GuiLighting.enableForItems();

			ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
			itemRenderer.renderGuiItem(stack, x, y);

			GL11.glDisable(GL11.GL_LIGHTING);
		}
	}

	private void renderStackForInfo(ItemStack stack) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826);
		GuiLighting.enable();
		GuiLighting.enableForItems();
		renderItemStack(stack, x, y - 5);
	}

	private void addInfo(ItemStack stack, Window res) {
		if (stack == ItemStack.EMPTY) {
			return;
		}

		String text = "";
		if (stack.getItem() instanceof IEnergyItemInfo) {
			ItemPowerManager capEnergy = new ItemPowerManager(stack);

			int maxCharge = capEnergy.getMaxEnergyStored();
			int currentCharge = capEnergy.getEnergyStored();

			ChatFormat color = ChatFormat.GREEN;
			double quarter = maxCharge / 4;
			double half = maxCharge / 2;
			if (currentCharge <= half) {
				color = ChatFormat.YELLOW;
			}
			if (currentCharge <= quarter) {
				color = ChatFormat.DARK_RED;
			}
			text = color + PowerSystem.getLocaliszedPowerFormattedNoSuffix(currentCharge / RebornCoreConfig.euPerFU)
				+ "/" + PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxCharge / RebornCoreConfig.euPerFU) + " "
				+ PowerSystem.getDisplayPower().abbreviation + ChatFormat.GRAY;
			if (stack.getTag() != null && stack.getTag().containsKey("isActive")) {
				if (stack.getTag().getBoolean("isActive")) {
					text = text + ChatFormat.GOLD + " (" + StringUtils.t("reborncore.message.active")
						+ ChatFormat.GOLD + ")" + ChatFormat.GRAY;
				} else {
					text = text + ChatFormat.GOLD + " (" + StringUtils.t("reborncore.message.inactive")
						+ ChatFormat.GOLD + ")" + ChatFormat.GRAY;
				}
			}

			if (RebornCoreConfig.stackInfoCorner == 1 || RebornCoreConfig.stackInfoCorner == 2) {
				int strWidth = mc.textRenderer.getStringWidth(text);
				// 18 for item icon and additionally padding from configuration file
				x = res.getScaledWidth() - strWidth - 18 - RebornCoreConfig.stackInfoX;
			}

			renderStackForInfo(stack);
			mc.textRenderer.drawWithShadow(text, x + 18, y, 0);

			if (RebornCoreConfig.stackInfoCorner == 0 || RebornCoreConfig.stackInfoCorner == 1) {
				y += 20;
			} else {
				y -= 20;
			}
		}

		for (StackInfoElement element : ELEMENTS) {
			if (!element.getText(stack).equals("")) {
				renderStackForInfo(stack);
				mc.textRenderer.drawWithShadow(element.getText(stack), x + 18, y, 0);
				y += 20;
			}
		}
	}
}
