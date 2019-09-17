package reborncore.fabric.mixin.common;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.checkerframework.checker.nullness.qual.Nullable;
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
					if(tag.containsKey(name)){
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
