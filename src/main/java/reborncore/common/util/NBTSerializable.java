package reborncore.common.util;

import net.minecraft.nbt.CompoundTag;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface NBTSerializable {

	@NonNull
	CompoundTag write();

	void read(@NonNull CompoundTag tag);

}
