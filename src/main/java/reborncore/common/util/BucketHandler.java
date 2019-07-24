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

package reborncore.common.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluid;

import java.util.HashMap;
import java.util.Map;

public class BucketHandler {

	public static BucketHandler INSTANCE = new BucketHandler();
	public Map<Fluid, Item> buckets = new HashMap<>();

	private BucketHandler() {

	}

	private ItemStack fillCustomBucket(World world, HitResult pos) {
		//TODO 1.13 see IBucketPickupHandler
		//		if (pos != null && pos.getBlockPos() != null && world.getBlockState(pos.getBlockPos()) != null) {
		//			Block block = world.getBlockState(pos.getBlockPos()).getBlock();
		//			if (block instanceof IBucketPickupHandler) {
		//				Fluid fluid = ((IBucketPickupHandler) block).getFluidInstance();
		//				if (buckets.containsKey(fluid)) {
		//					Item bucket = buckets.get(fluid);
		//					if (bucket != null) {
		//						world.setBlockToAir(pos.getBlockPos());
		//						return new ItemStack(bucket, 1);
		//					}
		//				}
		//			}
		//		}
		return ItemStack.EMPTY;
	}

}
