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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemUtils;
import reborncore.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.item.ItemStack.EMPTY;

/**
 * Created by Prospector
 */
public class StackInfoHUD {
	public static final StackInfoHUD instance = new StackInfoHUD();
	public static boolean showHud = true;
	public static boolean bottom = true;
	public static List<StackInfoElement> ELEMENTS = new ArrayList<>();
	private static Minecraft mc = Minecraft.getMinecraft();
	private final int yDef = 7;
	private int x = 2;
	private int y = 7;

	public static void registerElement(StackInfoElement element) {
		ELEMENTS.add(element);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;

		if (mc.inGameHasFocus || (mc.currentScreen != null && mc.gameSettings.showDebugInfo)) {
			if (RebornCoreConfig.ShowStackInfoHUD)
				drawStackInfoHud(event.getResolution());
		}
	}

	public void drawStackInfoHud(ScaledResolution res) {
		EntityPlayer player = mc.player;
		y = yDef;
		if (showHud) {
			List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack stack : player.getArmorInventoryList()) {
				stacks.add(stack);
			}
			stacks.add(player.getHeldItemOffhand());
			stacks.add(player.getHeldItemMainhand());

			if (bottom) {
				stacks = Lists.reverse(stacks);
			}
			for (ItemStack stack : stacks)
				addInfo(stack);
		}
	}

	public void renderItemStack(ItemStack stack, int x, int y) {
		if (stack != EMPTY) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderHelper.enableGUIStandardItemLighting();

			RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
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

	private void addInfo(ItemStack stack) {
		if (stack != EMPTY) {
			String text = "";
			if (stack.getItem() instanceof IEnergyItemInfo) {
				int maxCharge = stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
				int currentCharge = stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
				TextFormatting color = TextFormatting.GREEN;
				double quarter = maxCharge / 4;
				double half = maxCharge / 2;
				renderStackForInfo(stack);
				if (currentCharge <= half) {
					color = TextFormatting.YELLOW;
				}
				if (currentCharge <= quarter) {
					color = TextFormatting.DARK_RED;
				}
				text = color + PowerSystem.getLocaliszedPowerFormattedNoSuffix(currentCharge) + "/"
						+ PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxCharge) + " "
						+ PowerSystem.getDisplayPower().abbreviation + TextFormatting.GRAY;
				if (ItemUtils.isActive(stack)) {
					text = text + TextFormatting.GOLD + " (" + StringUtils.t("reborncore.message.active") + ")" + TextFormatting.GRAY;
				} else {
					text = text + TextFormatting.GOLD + " (" + StringUtils.t("reborncore.message.inactive") + ")" + TextFormatting.GRAY;
				}
				mc.fontRenderer.drawStringWithShadow(text, x + 18, y, 0);
				y += 20;
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
}
