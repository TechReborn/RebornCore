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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//TODO write this
public class NetworkManager {

	public static NetworkPacket createPacket(ResourceLocation resourceLocation, Consumer<ExtendedPacketBuffer> packetBufferConsumer){
		throw new UnsupportedOperationException("Needs coding :0");
	}


	public static void registerClientboundHandler(ResourceLocation resourceLocation, BiConsumer<ExtendedPacketBuffer, NetworkEvent.Context> consumer){

	}

	public static void registerServerboundHandler(ResourceLocation resourceLocation, BiConsumer<ExtendedPacketBuffer, NetworkEvent.Context> consumer){

	}

	public static void sendToServer(NetworkPacket packet) {
	}

	public static void sendToAllAround(NetworkPacket packet, PacketDistributor.TargetPoint point) {

	}

	public static void sendToAll(NetworkPacket packet) {

	}

	public static void sendToPlayer(NetworkPacket packet, EntityPlayerMP playerMP) {

	}

	public static void sendToWorld(NetworkPacket packet, World world) {

	}

	public static void sendToTracking(NetworkPacket packet, Chunk chunk){

	}

	public static void sendToTracking(NetworkPacket packet, World world, BlockPos pos){
		sendToTracking(packet, world.getChunk(pos));
	}

	public static void sendToTracking(NetworkPacket packet, TileEntity tileEntity){
		sendToTracking(packet, tileEntity.getWorld(), tileEntity.getPos());
	}

}
