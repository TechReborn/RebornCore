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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
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
	private static Minecraft mc = Minecraft.getInstance();
	private int x = 2;
	private int y = 7;

	public static void registerElement(StackInfoElement element) {
		ELEMENTS.add(element);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}

		if (mc.isGameFocused() || (mc.currentScreen != null && mc.gameSettings.showDebugInfo)) {
			if (RebornCoreConfig.ShowStackInfoHUD) {
				drawStackInfoHud(event.getResolution());
			}
		}
	}

	public void drawStackInfoHud(ScaledResolution res) {
		EntityPlayer player = mc.player;
		List<ItemStack> stacks = new ArrayList<>();
		for (ItemStack stack : player.getArmorInventoryList()) {
			stacks.add(stack);
		}
		stacks.add(player.getHeldItemOffhand());
		stacks.add(player.getHeldItemMainhand());

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
			RenderHelper.enableGUIStandardItemLighting();

			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);

			GL11.glDisable(GL11.GL_LIGHTING);
		}
	}

	private void renderStackForInfo(ItemStack stack) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826);
		RenderHelper.enableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		renderItemStack(stack, x, y - 5);
	}

	private void addInfo(ItemStack stack, ScaledResolution res) {
		if (stack == ItemStack.EMPTY) {
			return;
		}

		String text = "";
		if (stack.getItem() instanceof IEnergyItemInfo) {
			IEnergyStorage capEnergy = new ForgePowerItemManager(stack);

			int maxCharge = capEnergy.getMaxEnergyStored();
			int currentCharge = capEnergy.getEnergyStored();

			TextFormatting color = TextFormatting.GREEN;
			double quarter = maxCharge / 4;
			double half = maxCharge / 2;
			if (currentCharge <= half) {
				color = TextFormatting.YELLOW;
			}
			if (currentCharge <= quarter) {
				color = TextFormatting.DARK_RED;
			}
			text = color + PowerSystem.getLocaliszedPowerFormattedNoSuffix(currentCharge / RebornCoreConfig.euPerFU)
				+ "/" + PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxCharge / RebornCoreConfig.euPerFU) + " "
				+ PowerSystem.getDisplayPower().abbreviation + TextFormatting.GRAY;
			if (stack.getTag() != null && stack.getTag().hasKey("isActive")) {
				if (stack.getTag().getBoolean("isActive")) {
					text = text + TextFormatting.GOLD + " (" + StringUtils.t("reborncore.message.active")
						+ TextFormatting.GOLD + ")" + TextFormatting.GRAY;
				} else {
					text = text + TextFormatting.GOLD + " (" + StringUtils.t("reborncore.message.inactive")
						+ TextFormatting.GOLD + ")" + TextFormatting.GRAY;
				}
			}

			if (RebornCoreConfig.stackInfoCorner == 1 || RebornCoreConfig.stackInfoCorner == 2) {
				int strWidth = mc.fontRenderer.getStringWidth(text);
				// 18 for item icon and additionally padding from configuration file
				x = res.getScaledWidth() - strWidth - 18 - RebornCoreConfig.stackInfoX;
			}

			renderStackForInfo(stack);
			mc.fontRenderer.drawStringWithShadow(text, x + 18, y, 0);

			if (RebornCoreConfig.stackInfoCorner == 0 || RebornCoreConfig.stackInfoCorner == 1) {
				y += 20;
			} else {
				y -= 20;
			}
		}

		for (StackInfoElement element : ELEMENTS) {
			if (!element.getText(stack).equals("")) {
				renderStackForInfo(stack);
				mc.fontRenderer.drawStringWithShadow(element.getText(stack), x + 18, y, 0);
				y += 20;
			}
		}
	}
}
