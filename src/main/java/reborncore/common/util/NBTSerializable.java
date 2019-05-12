package reborncore.common.util;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {

	CompoundTag toTag();

	void fromTag(CompoundTag nbt);

}
