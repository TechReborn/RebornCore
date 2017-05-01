package teamreborn.reborncore.rip;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * File Created by Prospector.
 */
public class RIPHud extends Gui {
	private static Minecraft mc = Minecraft.getMinecraft();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;

		if (mc.inGameHasFocus || (mc.currentScreen != null && mc.gameSettings.showDebugInfo)) {
			drawRebornInfoProviderHUD(event.getResolution());
		}
	}

	public void drawRebornInfoProviderHUD(ScaledResolution res) {
		EntityPlayer player = mc.player;
		int x = 20;
		int y = 10;
		int width = 80;
		int height = 50;
		GuiUtils.drawGradientRect(0, x, y, x + width, y + height, 0xFF930000, 0xFF690000);
		GuiUtils.drawGradientRect(0, x - 1, y, x, y + height, 0xFF1B0202, 0xFF1B0202);
		GuiUtils.drawGradientRect(0, x, y - 1, x + width, y, 0xFF1B0202, 0xFF1B0202);
		GuiUtils.drawGradientRect(0, x + width, y, x + width + 1, y + height, 0xFF1B0202, 0xFF1B0202);
		GuiUtils.drawGradientRect(0, x, y + height, x + width, y + height + 1, 0xFF1B0202, 0xFF1B0202);
		GuiUtils.drawGradientRect(0, x + 1, y + 1, x + width - 1, y + height - 1, 0xFF1B0202, 0xFF1B0202);

		drawString(mc.fontRendererObj, "Hey there!", x + 5, y + 5, 0xFFFFFFFF);
		drawString(mc.fontRendererObj, "Watts up?", x + 5, y + 14, 0xFFFFFFFF);
		//		GuiUtils.drawGradientRect(0, 10, 20, 30, 20, 0xFF930000, 0xFF690000);
		//		drawGradientRect(40, 50, 60, 70, 0xFF930000, 0xFF690000);
		/*y = yDef;
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
		}*/
	}
}
