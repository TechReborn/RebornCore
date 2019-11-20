/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


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
