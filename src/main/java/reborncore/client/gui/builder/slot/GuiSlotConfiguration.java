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

package reborncore.client.gui.builder.slot;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Slot;
import net.minecraft.text.LiteralText;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.GuiUtil;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.slot.elements.ConfigSlotElement;
import reborncore.client.gui.builder.slot.elements.ElementBase;
import reborncore.client.gui.builder.slot.elements.SlotType;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.ServerBoundPackets;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.util.Color;
import reborncore.mixin.extensions.SlotExtensions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSlotConfiguration {

	static HashMap<Integer, ConfigSlotElement> slotElementMap = new HashMap<>();

	public static int selectedSlot = 0;

	public static void reset() {
		selectedSlot = -1;
	}

	public static void init(GuiBase<?> guiBase) {
		reset();
		slotElementMap.clear();

		BuiltContainer container = guiBase.container;
		for (Slot slot : container.slotList) {
			if (guiBase.blockEntity != slot.inventory) {
				continue;
			}
			SlotExtensions slotExtensions = (SlotExtensions) slot;
			ConfigSlotElement slotElement = new ConfigSlotElement(guiBase.getMachine().getOptionalInventory().get(), slotExtensions.getInvSlot(), SlotType.NORMAL, slot.xPosition - guiBase.getGuiLeft() + 50, slot.yPosition - guiBase.getGuiTop() - 25, guiBase);
			slotElementMap.put(slotExtensions.getInvSlot(), slotElement);
		}

	}

	public static void draw(GuiBase<?> guiBase, int mouseX, int mouseY) {
		BuiltContainer container = guiBase.container;
		for (Slot slot : container.slotList) {
			if (guiBase.blockEntity != slot.inventory) {
				continue;
			}
			GlStateManager.color3f(255, 0, 0);
			Color color = new Color(255, 0, 0, 128);
			GuiUtil.drawGradientRect(slot.xPosition - 1, slot.yPosition - 1, 18, 18, color.getColor(), color.getColor());
			GlStateManager.color3f(255, 255, 255);
		}

		if (selectedSlot != -1) {

			slotElementMap.get(selectedSlot).draw(guiBase);
		}
	}

	public static List<ConfigSlotElement> getVisibleElements() {
		if (selectedSlot == -1) {
			return Collections.emptyList();
		}
		return slotElementMap.values().stream()
			.filter(configSlotElement -> configSlotElement.getId() == selectedSlot)
			.collect(Collectors.toList());
	}

	//TODO fabric events
	//Allows closing of the widget with the escape key
//	@SubscribeEvent
//	public static void keyboardEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
//		if (!getVisibleElements().isEmpty() && event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE) {
//			selectedSlot = -1;
//			event.setCanceled(true);
//		}
//	}

	public static void copyToClipboard() {
		MachineBaseBlockEntity machine = getMachine();
		if (machine == null || machine.getSlotConfiguration() == null) {
			return;
		}
		String json = machine.getSlotConfiguration().toJson(machine.getClass().getCanonicalName());
		MinecraftClient.getInstance().keyboard.setClipboard(json);
		MinecraftClient.getInstance().player.sendMessage(new LiteralText("Slot configuration copyied to clipboard"));
	}

	public static void pasteFromClipboard() {
		MachineBaseBlockEntity machine = getMachine();
		if (machine == null || machine.getSlotConfiguration() == null) {
			return;
		}
		String json = MinecraftClient.getInstance().keyboard.getClipboard();
		try {
			machine.getSlotConfiguration().readJson(json, machine.getClass().getCanonicalName());
			NetworkManager.sendToServer(ServerBoundPackets.createPacketConfigSave(machine.getPos(), machine.getSlotConfiguration()));
			MinecraftClient.getInstance().player.sendMessage(new LiteralText("Slot configuration loaded from clipboard"));
		} catch (UnsupportedOperationException e) {
			MinecraftClient.getInstance().player.sendMessage(new LiteralText(e.getMessage()));
		}
	}

	@Nullable
	private static MachineBaseBlockEntity getMachine() {
		if (!(MinecraftClient.getInstance().currentScreen instanceof GuiBase)) {
			return null;
		}
		GuiBase<?> base = (GuiBase<?>) MinecraftClient.getInstance().currentScreen;
		if (!(base.blockEntity instanceof MachineBaseBlockEntity)) {
			return null;
		}
		MachineBaseBlockEntity machineBase = (MachineBaseBlockEntity) base.blockEntity;
		return machineBase;
	}

	public static boolean mouseClicked(double mouseX, double mouseY, int mouseButton, GuiBase<?> guiBase) {
		if (mouseButton == 0) {
			for (ConfigSlotElement configSlotElement : getVisibleElements()) {
				for (ElementBase element : configSlotElement.elements) {
					if (element.isInRect(guiBase, element.x, element.y, element.getWidth(guiBase.getMachine()), element.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
						element.isPressing = true;
						boolean action = element.onStartPress(guiBase.getMachine(), guiBase, mouseX, mouseY);
						for (ElementBase e : getVisibleElements()) {
							if (e != element) {
								e.isPressing = false;
							}
						}
						if (action) {
							break;
						}
					} else {
						element.isPressing = false;
					}
				}
			}
		}
		BuiltContainer container = guiBase.container;

		if (getVisibleElements().isEmpty()) {
			for (Slot slot : container.slotList) {
				if (guiBase.blockEntity != slot.inventory) {
					continue;
				}
				if (guiBase.isPointInRect(slot.xPosition, slot.yPosition, 18, 18, mouseX, mouseY)) {
					SlotExtensions slotExtensions = (SlotExtensions) slot;
					selectedSlot = slotExtensions.getInvSlot();
					return true;
				}
			}
		}
		return !getVisibleElements().isEmpty();
	}

	public static void mouseClickMove(double mouseX, double mouseY, int mouseButton, long timeSinceLastClick, GuiBase<?> guiBase) {
		if (mouseButton == 0) {
			for (ConfigSlotElement configSlotElement : getVisibleElements()) {
				for (ElementBase element : configSlotElement.elements) {
					if (element.isInRect(guiBase, element.x, element.y, element.getWidth(guiBase.getMachine()), element.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
						element.isDragging = true;
						boolean action = element.onDrag(guiBase.getMachine(), guiBase, mouseX, mouseY);
						for (ElementBase e : getVisibleElements()) {
							if (e != element) {
								e.isDragging = false;
							}
						}
						if (action) {
							break;
						}
					} else {
						element.isDragging = false;
					}
				}
			}
		}
	}

	public static boolean mouseReleased(double mouseX, double mouseY, int mouseButton, GuiBase<?> guiBase) {
		boolean clicked = false;
		if (mouseButton == 0) {
			for (ConfigSlotElement configSlotElement : getVisibleElements()) {
				if (configSlotElement.isInRect(guiBase, configSlotElement.x, configSlotElement.y, configSlotElement.getWidth(guiBase.getMachine()), configSlotElement.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
					clicked = true;
				}
				for (ElementBase element : Lists.reverse(configSlotElement.elements)) {
					if (element.isInRect(guiBase, element.x, element.y, element.getWidth(guiBase.getMachine()), element.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
						element.isReleasing = true;
						boolean action = element.onRelease(guiBase.getMachine(), guiBase, mouseX, mouseY);
						for (ElementBase e : getVisibleElements()) {
							if (e != element) {
								e.isReleasing = false;
							}
						}
						if (action) {
							clicked = true;
						}
						break;
					} else {
						element.isReleasing = false;
					}
				}
			}
		}
		return clicked;
	}

}
