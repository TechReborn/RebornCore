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

package reborncore.mixin.common;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import reborncore.common.world.DataAttachment;
import reborncore.common.world.DataAttachmentProvider;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(WorldSaveHandler.class)
public class MixinWorldSaveHandler implements DataAttachmentProvider {

	@Shadow @Final
	private File worldDir;

	@Unique
	private final HashMap<Class<? extends DataAttachment>, DataAttachment> attachmentMap = new HashMap<>();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(File file, String worldName, @Nullable MinecraftServer minecraftServer, DataFixer dataFixer, CallbackInfo info){
		DataAttachment.REGISTRY.lock();

		for(Map.Entry<Class<? extends DataAttachment>, Supplier<? extends DataAttachment>> entry : DataAttachment.REGISTRY.getAllDataAttachments().entrySet()){
			attachmentMap.put(entry.getKey(), entry.getValue().get());
		}
	}

	@Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
	private void saveWorld(LevelProperties levelProperties, CompoundTag compoundTag, CallbackInfo info) {
		CompoundTag tag = new CompoundTag();

		for(Map.Entry<Class<? extends DataAttachment>, DataAttachment> entry : attachmentMap.entrySet()){
			tag.put(entry.getKey().getName(), entry.getValue().write());
		}

		try {
			FileOutputStream fio = new FileOutputStream(reborncore_getFile());
			NbtIo.writeCompressed(tag, fio);
			fio.close();
		} catch (IOException e){
			throw new RuntimeException("Failed to save reborncore world data!", e);
		}
	}

	@Inject(method = "readProperties", at = @At("HEAD"))
	private void readWorldProperties(CallbackInfoReturnable<LevelProperties> callbackInfo) throws IOException {
		File file = reborncore_getFile();
		if(file.exists()){
			try {
				FileInputStream fio = new FileInputStream(file);
				CompoundTag tag = NbtIo.readCompressed(fio);
				fio.close();

				for(Map.Entry<Class<? extends DataAttachment>, DataAttachment> entry : attachmentMap.entrySet()){
					String name = entry.getKey().getName();
					if(tag.contains(name)){
						entry.getValue().read(tag.getCompound(name));
					}
				}
			} catch (IOException e){
				throw new RuntimeException("Failed to read reborncore world data!", e);
			}
		}
	}

	@Unique
	private File reborncore_getFile(){
		return new File(new File(worldDir, "data"), "reborncore" + ".dat");
	}

	@Override
	public <T extends DataAttachment> T getAttachment(Class<T> identifier) {
		//noinspection unchecked
		return (T) attachmentMap.get(identifier);
	}
}
