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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class Progress implements INBTSerializable<NBTTagCompound> {
	int progress;
	int maxProgress;

	public Progress(int maxProgress) {
		this.progress = 0;
		this.maxProgress = maxProgress;
	}

	public Progress(int progress, int maxProgress) {
		this.progress = progress;
		this.maxProgress = maxProgress;
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void addProgress(int amount) {
		this.progress += amount;
	}

	public void setProgress(int amount) {
		this.progress = amount;
	}

	public void resetProgress() {
		this.progress = 0;
	}

	public void incrementProgress() {
		++this.progress;
	}

	public boolean isProgressCompleate() {
		if (progress >= getMaxProgress()) {
			return true;
		}
		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagCompound dataTag = new NBTTagCompound();
		dataTag.setInteger("progress", this.progress);
		dataTag.setInteger("maxProgress", this.maxProgress);
		return dataTag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.progress = nbt.getInteger("progress");
		this.maxProgress = nbt.getInteger("maxProgress");

		if (this.progress > this.getMaxProgress()) {
			this.progress = this.getMaxProgress();
		}
	}
}
