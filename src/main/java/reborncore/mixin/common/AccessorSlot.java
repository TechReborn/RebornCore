package reborncore.mixin.common;

import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface AccessorSlot {

	@Accessor
	int getInvSlot();
}
