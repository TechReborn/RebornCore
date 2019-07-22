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

package reborncore.common.network;

import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by Gigabit101 on 21/01/2017.
 */
public final class VanillaPacketDispatcher {
	public static void dispatchTEToNearbyPlayers(BlockEntity blockEntity) {
		if (blockEntity.getWorld() instanceof ServerWorld) {
			ServerWorld ws = ((ServerWorld) blockEntity.getWorld());
			BlockEntityUpdateS2CPacket packet = blockEntity.toUpdatePacket();

			if (packet == null) {
				return;
			}

			for (PlayerEntity player : ws.getPlayers()) {
				ServerPlayerEntity playerMP = ((ServerPlayerEntity) player);

				if (playerMP.squaredDistanceTo(new Vec3d(blockEntity.getPos())) < 64 * 64 && PlayerStream.watching(blockEntity.getWorld(), blockEntity.getPos()).anyMatch(playerEntity -> playerEntity.getUuid().equals(playerMP.getUuid()))) {
					playerMP.networkHandler.sendPacket(packet);
				}
			}
		}
	}

	public static void dispatchTEToNearbyPlayers(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			dispatchTEToNearbyPlayers(blockEntity);
		}
	}

	public static float pointDistancePlane(double x1, double y1, double x2, double y2) {
		return (float) Math.hypot(x1 - x2, y1 - y2);
	}
}
