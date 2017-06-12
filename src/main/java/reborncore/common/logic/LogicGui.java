package reborncore.common.logic;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import reborncore.common.network.NetworkManager;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class LogicGui extends GuiContainer {
	@Nonnull
	EntityPlayer player;
	@Nonnull
	LogicController logicController;

	public LogicGui(EntityPlayer player, LogicController logicController) {
		super(new LogicContainer(player, logicController));
		this.player = player;
		this.logicController = logicController;
		buttonList.clear();
	}

	@Override
	public void initGui() {
		super.initGui();
		if (logicController.getButtons() != null) {
			for (GuiButton b : logicController.getButtons()) {
				buttonList.add(b);
			}
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		logicController.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY, guiLeft, guiTop, xSize, ySize, this);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		logicController.drawGuiContainerForegroundLayer(mouseX, mouseY, this, guiLeft, guiTop);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		NetworkManager.sendToServer(new PacketButtonID(logicController.getPos(), button.id));
	}
}
