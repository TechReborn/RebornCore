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

package reborncore;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import reborncore.client.multiblock.MultiblockRenderEvent;
import reborncore.client.shields.ShieldTextureStore;

public class ClientProxy extends CommonProxy {

	public static MultiblockRenderEvent multiblockRenderEvent;

	@Override
	public void setup() {
		super.setup();
		multiblockRenderEvent = new MultiblockRenderEvent();
		//ItemDynamicRenderer.INSTANCE = new RebornItemStackRenderer(ItemDynamicRenderer.INSTANCE);
	}

	@Override
	public void loadShieldTextures() {
		super.loadShieldTextures();
		ShieldTextureStore.load();
	}

	@Override
	public World getClientWorld() {
		return MinecraftClient.getInstance().world;
	}

	@Override
	public PlayerEntity getPlayer() {
		return MinecraftClient.getInstance().player;
	}
}
