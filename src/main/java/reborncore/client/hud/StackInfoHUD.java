package reborncore.client.hud;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.Color;

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
			boolean didShit = false;
			Color gold = Color.GOLD;
			Color grey = Color.GRAY;
			if (stack.getItem() instanceof IEnergyInterfaceItem) {
				double MaxCharge = ((IEnergyInterfaceItem) stack.getItem()).getMaxPower(stack);
				double CurrentCharge = ((IEnergyInterfaceItem) stack.getItem()).getEnergy(stack);
				Color color = Color.GREEN;
				double quarter = MaxCharge / 4;
				double half = MaxCharge / 2;
				renderStackForInfo(stack);
				if (CurrentCharge <= half) {
					color = Color.YELLOW;
				}
				if (CurrentCharge <= quarter) {
					color = Color.DARK_RED;
				}
				text = color + PowerSystem.getLocaliszedPowerFormattedNoSuffix(CurrentCharge) + "/" + PowerSystem.getLocaliszedPowerFormattedNoSuffix(MaxCharge) + " " + PowerSystem.getDisplayPower().abbreviation + grey;
				if (stack.getTagCompound() != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("isActive")) {
					if (stack.getTagCompound().getBoolean("isActive"))
						text = text + gold + " (" + I18n.translateToLocal("reborncore.message.active") + ")" + grey;
					else
						text = text + gold + " (" + I18n.translateToLocal("reborncore.message.inactive") + ")" + grey;
				}
				mc.fontRendererObj.drawStringWithShadow(text, x + 18, y, 0);
				y += 20;
			}

			for (StackInfoElement element : ELEMENTS) {
				if (!element.getText(stack).equals("")) {
					renderStackForInfo(stack);
					mc.fontRendererObj.drawStringWithShadow(element.getText(stack), x + 18, y, 0);
					y += 20;
				}
			}
		}
	}
}
