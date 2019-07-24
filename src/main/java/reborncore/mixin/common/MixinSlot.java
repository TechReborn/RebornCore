package reborncore.mixin.common;

import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.mixin.extensions.SlotExtensions;

@Mixin(Slot.class)
public class MixinSlot implements SlotExtensions {

	@Shadow @Final private int invSlot;

	@Override
	public int getInvSlot() {
		return invSlot;
	}
}
