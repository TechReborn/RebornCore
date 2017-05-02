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
import teamreborn.reborncore.RebornCore;
import teamreborn.reborncore.reborninfoprovider.elements.IStackDisplayProvider;
import teamreborn.reborncore.reborninfoprovider.elements.StackInfoElement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Created by Prospector.
 */
public class RebornInfoProviderHUD extends Gui {
	public static File ripConfig;
	public static int x = 5;
	public static int y = 5;
	private static Minecraft mc = Minecraft.getMinecraft();
	private static List<RebornInfoElement> elements = new ArrayList<>();
	public boolean displayActive = false;

	public static void addElement(RebornInfoElement element) {
		elements.add(element);
	}

	public static void removeElement(RebornInfoElement element) {
		elements.remove(element);
	}

	public static void clearElements() {
		elements.clear();
	}

	public static List<RebornInfoElement> getElements() {
		return elements;
	}

	public static void reloadConfig() {
		if (!ripConfig.exists()) {
			writeConfig(new RIPConfig());
		}
		if (ripConfig.exists()) {
			RIPConfig config = null;
			try (Reader reader = new FileReader(ripConfig)) {
				config = RebornCore.GSON.fromJson(reader, RIPConfig.class);
				x = config.getX();
				y = config.getY();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (config == null) {
				config = new RIPConfig();
				writeConfig(config);
			}
		}
	}

	public static void writeConfig(RIPConfig config) {
		try (Writer writer = new FileWriter(ripConfig)) {
			RebornCore.GSON.toJson(config, writer);
		} catch (Exception e) {

		}
		reloadConfig();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;
		if (mc.gameSettings.showDebugInfo) {
			return;
		}
		if (mc.inGameHasFocus) {
			reloadConfig();
			drawRebornInfoProviderHUD(event.getResolution());
		}
	}

	public void addDefaultElements() {
		handleEquiptmentStacks("auto.mainhand", mc.player.getHeldItemMainhand());
		handleEquiptmentStacks("auto.offhand", mc.player.getHeldItemOffhand());
		int i = 0;
		for (ItemStack stack : mc.player.getArmorInventoryList()) {
			handleEquiptmentStacks("auto.armor." + i, stack);
			i++;
		}
	}

	public void handleEquiptmentStacks(String meta, ItemStack stack) {
		if (stack.getItem() instanceof IStackDisplayProvider) {
			StackInfoElement element = new StackInfoElement(stack, ((IStackDisplayProvider) stack.getItem()).getElementString(mc.player, stack));
			element.meta = meta;
			if (!elements.contains(element)) {
				removeHandElements(meta);
				addElement(element);
			}
		} else {
			removeHandElements(meta);
		}
	}

	public void removeHandElements(String meta) {
		for (RebornInfoElement e : elements) {
			if (e.meta.equals(meta)) {
				removeElement(e);
				break;
			}
		}
	}

	public void drawRebornInfoProviderHUD(ScaledResolution res) {
		addDefaultElements();
		boolean active = false;
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
				if (!isLastVisible(element))
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
					if (!isLastVisible(element))
						currentRenderY += element.getHeight() + paddingBetweenElements;
				}
			}
		}
		displayActive = active;
	}

	private boolean isLastVisible(RebornInfoElement element) {
		RebornInfoElement lastVisible = null;
		for (RebornInfoElement e : elements) {
			if (e.isVisible()) {
				lastVisible = e;
			}
		}
		if (lastVisible != null && lastVisible == element) {
			return true;
		}
		return false;
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

	public static class RIPConfig {
		public int x = 5;
		public int y = 5;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}
}
