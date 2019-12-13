package reborncore.common.util;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public interface NBTSerializable {

	@Nonnull
	CompoundTag write();

	void read(@Nonnull CompoundTag tag);

}
