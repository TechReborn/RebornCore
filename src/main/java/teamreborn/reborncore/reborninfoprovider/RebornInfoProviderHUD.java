package teamreborn.reborncore.reborninfoprovider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * File Created by Prospector.
 */
public class RebornInfoProviderHUD extends Gui {
	private static Minecraft mc = Minecraft.getMinecraft();
	private static List<RebornInfoElement> elements = new ArrayList<>();
	public boolean displayActive = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;

		if (mc.inGameHasFocus || (mc.currentScreen != null && mc.gameSettings.showDebugInfo)) {
			drawRebornInfoProviderHUD(event.getResolution());
		}
	}

	public static void addElement(RebornInfoElement element) {
		elements.add(element);
	}

	public static void removeElement(RebornInfoElement element) {
		elements.remove(element);
	}

	public static List<RebornInfoElement> getElements() {
		return elements;
	}

	public void drawRebornInfoProviderHUD(ScaledResolution res) {
		boolean active = false;
		int x = 5;
		int y = 5;
		int defaultWidth = 0;
		int defaultHeight = 0;
		int width = 0;
		int height = 0;
		int paddingX = 4;
		int paddingY = 4;
		int paddingBetweenElements = 2;
		int currentRenderY = y + paddingY;
		defaultWidth += paddingX * 2;
		defaultHeight += paddingY * 2;
		width = defaultWidth;
		height = defaultHeight;
		for (RebornInfoElement element : elements) {
			element.preRender(mc);
			if (element.isVisible()) {
				if (width < element.getWidth() + defaultWidth)
					width = element.getWidth() + defaultWidth;
				height += element.getHeight();
				//				if (element != (elements.get(elements.size() - 1)))
				height += paddingBetweenElements;
				active = true;
			}
		}
		if (active) {
			GuiUtils.drawGradientRect(0, x, y, x + width, y + height, 0xFF930000, 0xFF690000);
			GuiUtils.drawGradientRect(0, x - 1, y, x, y + height, 0xFF1B0202, 0xFF1B0202);
			GuiUtils.drawGradientRect(0, x, y - 1, x + width, y, 0xFF1B0202, 0xFF1B0202);
			GuiUtils.drawGradientRect(0, x + width, y, x + width + 1, y + height, 0xFF1B0202, 0xFF1B0202);
			GuiUtils.drawGradientRect(0, x, y + height, x + width, y + height + 1, 0xFF1B0202, 0xFF1B0202);
			GuiUtils.drawGradientRect(0, x + 1, y + 1, x + width - 1, y + height - 1, 0xFF1B0202, 0xFF1B0202);
			for (RebornInfoElement element : elements) {
				if (element.isVisible()) {
					element.render(x + paddingX, currentRenderY, this, mc.fontRendererObj);
					//					if (element != (elements.get(elements.size() - 1)))
					currentRenderY += element.getHeight() + paddingBetweenElements;
				}
			}
		}
		displayActive = active;
	}

	public void renderItemStack(ItemStack stack, int x, int y) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderHelper.enableGUIStandardItemLighting();

			RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
			itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);

			GlStateManager.disableLighting();
			GlStateManager.popMatrix();
		}
	}
}
