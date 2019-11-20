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

package reborncore.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import reborncore.RebornCore;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.GuiBase;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author estebes
 */
public class VariableMessage implements IMessage {
    public VariableMessage() {
        // NO-OP
    }

    public VariableMessage(Type type, int dimension, BlockPos pos, NBTTagCompound tag) {
        this.type = type;
        this.dimension = dimension;
        this.pos = pos;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            type = Type.values()[buf.readInt()];
            dimension = buf.readInt();
            pos = BlockPos.fromLong(buf.readLong());

            short length = buf.readShort();
            if (length < 0) {
                tag = new NBTTagCompound();
            } else {
                byte[] bytes = new byte[length];
                buf.readBytes(bytes);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
                tag = CompressedStreamTools.readCompressed(byteStream);
            }
        } catch (IOException exception) {
            RebornCore.logHelper.fatal(exception);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            buf.writeInt(type.ordinal());
            buf.writeInt(dimension);
            buf.writeLong(pos.toLong());

            if (tag == null || tag.getSize() == 0) {
                buf.writeShort(-1);
            } else {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                CompressedStreamTools.writeCompressed(tag, byteStream);
                byte[] bytes = byteStream.toByteArray();
                buf.writeShort((short) bytes.length);
                buf.writeBytes(bytes);
            }
        } catch (IOException exception) {
            RebornCore.logHelper.fatal(exception);
        }
    }

    // Fields >>
    private Type type;
    private int dimension;
    private BlockPos pos;
    private NBTTagCompound tag;
    // << Fields

    public enum Type {
        TILE,
        GUI
    }

    public static class VariableMessageHandler implements IMessageHandler<VariableMessage, VariableMessage> {
        @Override
        public VariableMessage onMessage(VariableMessage message, MessageContext context) {
            if (message == null) return null;

            if (message.type == null) return null;

            switch (message.type) {
                case TILE:
                    break;
                case GUI:
                    handleGuiMessage(message, context);
                    break;
            }

//            if (message.pos == null || message.tag == null) return null;
//
//            if (context.side.isClient()) {
//                TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(message.pos);
//                if (tile != null) {
//
//                }
//            } else {
//                WorldServer world = DimensionManager.getWorld(message.dimension);
//                if (world != null) {
//                    world.addScheduledTask(() -> {
//                        TileEntity tile = world.getTileEntity(message.pos);
//                        if (tile != null) {
//
//                        }
//                    });
//                }
//            }

            return null;
        }

        public VariableMessage handleGuiMessage(VariableMessage message, MessageContext context) {
            if (message.pos == null || message.tag == null) return null;

            if (context.side.isServer()) throw new RuntimeException("Message of Type GUI cannot be processed on the server");

            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiBase){
                BuiltContainer container = ((GuiBase) gui).container;
                if (container != null) container.handleGuiMessage(message.tag);
            }

            return null;
        }
    }
}
