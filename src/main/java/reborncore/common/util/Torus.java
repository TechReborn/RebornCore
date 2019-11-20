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


package reborncore.common.util;

import net.minecraft.util.math.BlockPos;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Torus {

	public static Map<Integer, Integer> TORUS_SIZE_MAP = new HashMap<>();

	public static List<BlockPos> generate(BlockPos orgin, int radius){
		List<BlockPos> posLists = new ArrayList<>();
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					if(Math.pow(radius /2 - Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)), 2) + Math.pow(z, 2) < Math.pow(radius * 0.05, 2)){
						posLists.add(orgin.add(x, z, y));
					}
				}
			}
		}
		return posLists;
	}

	public static void genSizeMap(int maxRadius){
		if(!TORUS_SIZE_MAP.isEmpty()){
			//Lets not do this again
			return;
		}
		long start = System.currentTimeMillis();
		for (int radius = 0; radius < maxRadius + 10; radius++) { //10 is added as the control computer has a base of around 6 less
			int size = 0;
			for (int x = -radius; x < radius; x++) {
				for (int y = -radius; y < radius; y++) {
					for (int z = -radius; z < radius; z++) {
						if(Math.pow(radius /2 - Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)), 2) + Math.pow(z, 2) < Math.pow(radius * 0.05, 2)){
							size++;
						}
					}
				}
			}
			TORUS_SIZE_MAP.put(radius, size);
		}
		RebornCore.logHelper.info("Loaded torus size map in " + (System.currentTimeMillis() - start) + "ms");
	}

}
