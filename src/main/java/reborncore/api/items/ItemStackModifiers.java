package reborncore.api.items;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

public interface ItemStackModifiers {

	void getAttributeModifiers(EquipmentSlot slot, ItemStack stack, Multimap<String, EntityAttributeModifier> attributes);

}
