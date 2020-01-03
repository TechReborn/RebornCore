/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.client.containerBuilder.builder;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import reborncore.common.network.ClientBoundPackets;
import reborncore.common.network.NetworkManager;

public interface IExtendedContainerListener {

	public default void sendObject(ContainerListener containerListener, Container containerIn, int var, Object value) {
		if (containerListener instanceof ServerPlayerEntity) {
			NetworkManager.sendToPlayer(ClientBoundPackets.createPacketSendObject(var, value, containerIn), (ServerPlayerEntity) containerListener);
		}
	}

	public default void handleObject(int var, Object value) {

	}
}
