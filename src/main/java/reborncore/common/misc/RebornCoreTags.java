package reborncore.common.misc;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class RebornCoreTags {
	public static final Tag<Item> WATER_EXPLOSION_ITEM = TagRegistry.item(new Identifier("reborncore", "water_explosion"));
}
