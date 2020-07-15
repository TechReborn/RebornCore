package reborncore.api.systems.conduit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public interface IConduitTransfer<T> {
    void progress();

    // Helper functions
    boolean isFinished();

    void restartProgress();

    float getProgressPercent();

    boolean isEmpty();

    // Getter/Setters
    T getStored();

    void setStored(T stored);

    Direction getOriginDirection();

    void setOriginDirection(Direction origin);

    Direction getTargetDirection();

	void setTargetDirection(Direction target);

	// NBT
	void fromTag(CompoundTag tag);

	CompoundTag toTag(CompoundTag tag);
}
