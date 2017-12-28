/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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

package reborncore.common.util;

import net.minecraft.util.EnumFacing;
import reborncore.common.tile.TileLegacyMachineBase;

public enum  MachineFacing {
	FRONT,
	BACK,
	UP,
	DOWN,
	LEFT,
	RIGHT;

	public EnumFacing getFacing(TileLegacyMachineBase machineBase){
		if(this == FRONT){
			return machineBase.getFacing();
		}
		if(this == BACK){
			return machineBase.getFacing().getOpposite();
		}
		if(this == RIGHT){
			//North -> West
			int i = machineBase.getFacing().getOpposite().getHorizontalIndex() +1;
			if(i > 3){
				i = 0;
			}
			if(i < 0){
				i = 3;
			}
			return EnumFacing.HORIZONTALS[i];
		}
		if(this == LEFT){
			//North -> East
			int i = machineBase.getFacing().getOpposite().getHorizontalIndex() - 1;
			if(i > 3){
				i = 0;
			}
			if(i < 0){
				i = 3;
			}
			return EnumFacing.HORIZONTALS[i];
		}
		if(this == UP){
			return EnumFacing.UP;
		}
		if(this == DOWN){
			return EnumFacing.DOWN;
		}

		return EnumFacing.NORTH;
	}

}
