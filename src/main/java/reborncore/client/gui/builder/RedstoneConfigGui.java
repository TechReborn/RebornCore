package reborncore.client.gui.builder;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.network.Packet;
import reborncore.client.RenderUtil;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.common.blockentity.RedstoneConfiguration;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.ServerBoundPackets;
import reborncore.common.util.StringUtils;

import java.util.Locale;

public class RedstoneConfigGui {

	public static void draw(GuiBase<?> guiBase, int mouseX, int mouseY) {
		if (guiBase.getMachine() == null) return;
		RedstoneConfiguration configuration = guiBase.getMachine().getRedstoneConfiguration();
		GuiBuilder builder = guiBase.builder;
		ItemRenderer itemRenderer = guiBase.getMinecraft().getItemRenderer();

		int x = 10;
		int y = 100;

		int i = 0;
		int spread = configuration.getElements().size() == 3 ? 27 : 18;
		for (RedstoneConfiguration.Element element : configuration.getElements()) {
			itemRenderer.renderGuiItem(element.getIcon(), x - 3, y + (i * spread) - 5);

			guiBase.getTextRenderer().draw(StringUtils.t("reborncore.gui.fluidconfig." + element.getName()), x + 15, y + (i * spread), -1);

			if (withinBounds(guiBase, mouseX, mouseY, x + 92, y + (i * spread) -2, 63, 15)) {
				RenderUtil.drawGradientRect(0, x + 91, y + (i * spread) -2, x + 93 + 65, y + (i * spread) + 10, 0xFF8b8b8b, 0xFF8b8b8b);
			}

			String name = StringUtils.t("reborncore.gui.fluidconfig." + configuration.getState(element).name().toLowerCase(Locale.ROOT));
			guiBase.getTextRenderer().drawWithShadow(name, x + 92, y + (i * spread), -1);
			i ++;
		}

	}

	public static boolean mouseClicked(GuiBase<?> guiBase, double mouseX, double mouseY, int mouseButton) {
		if (guiBase.getMachine() == null) return false;
		RedstoneConfiguration configuration = guiBase.getMachine().getRedstoneConfiguration();

		int x = 10;
		int y = 100;

		int i = 0;
		int spread = configuration.getElements().size() == 3 ? 27 : 18;
		for (RedstoneConfiguration.Element element : configuration.getElements()) {
			if (withinBounds(guiBase, (int)mouseX, (int)mouseY, x + 91, y + (i * spread) -2, 63, 15)) {
				RedstoneConfiguration.State currentState = configuration.getState(element);
				int ns = currentState.ordinal() + 1;
				if (ns >= RedstoneConfiguration.State.values().length) {
					ns = 0;
				}
				RedstoneConfiguration.State nextState = RedstoneConfiguration.State.values()[ns];
				Packet<?> packet = ServerBoundPackets.createPacketSetRedstoneSate(guiBase.getMachine().getPos(), element, nextState);
				NetworkManager.sendToServer(packet);
				return true;
			}
			i ++;
		}
		return false;
	}

	private static boolean withinBounds(GuiBase<?> guiBase,int mouseX, int mouseY, int x, int y, int width, int height) {
		mouseX -= guiBase.getGuiLeft();
		mouseY -= guiBase.getGuiTop();
		return (mouseX > x && mouseX < x + width ) && (mouseY > y && mouseY < y + height);
	}

}
