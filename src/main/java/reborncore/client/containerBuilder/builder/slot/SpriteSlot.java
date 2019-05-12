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

package reborncore.client.containerBuilder.builder.slot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

public class SpriteSlot extends FilteredSlot {

	private final String spriteName;
	int stacksize;

	public SpriteSlot(final IItemHandler inventory, final int index, final int xPosition, final int yPosition, final String sprite, final int stacksize) {
		super(inventory, index, xPosition, yPosition);
		this.spriteName = sprite;
		this.stacksize = stacksize;
	}

	public SpriteSlot(final IItemHandler inventory, final int index, final int xPosition, final int yPosition, final String sprite) {
		this(inventory, index, xPosition, yPosition, sprite, 64);
	}

	@Override
	public int getSlotStackLimit() {
		return this.stacksize;
	}

	@Override
	@Nullable
	@Environment(EnvType.CLIENT)
	public String getSlotTexture() {
		return this.spriteName;
	}
}
