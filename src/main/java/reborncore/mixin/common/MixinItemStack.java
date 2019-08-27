package reborncore.mixin.common;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import reborncore.api.items.ItemStackModifiers;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

	@Shadow public abstract Item getItem();

	@Inject(method = "getAttributeModifiers", at = @At("RETURN"))
	private void getAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<String, EntityAttributeModifier>> info){
		if(getItem() instanceof ItemStackModifiers){
			ItemStackModifiers item = (ItemStackModifiers) getItem();
			item.getAttributeModifiers(equipmentSlot, (ItemStack)(Object)this, info.getReturnValue());
		}
	}

}
