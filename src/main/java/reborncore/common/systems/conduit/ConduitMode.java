package reborncore.common.systems.conduit;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum ConduitMode {
	OUTPUT(Items.BEACON),
	INPUT(Items.HOPPER),
	BLOCK(Items.BEDROCK),
	ONE_WAY(Items.PISTON, 1);

	// Item required to use get this mode
	public Item requiredItem;

	// How many a conduit can have of this type
	public int maxCount = 6;

	ConduitMode(Item item, int maxCount) {
		this(item);
		this.maxCount = maxCount;
	}

	ConduitMode(Item item) {
		this.requiredItem = item;
	}
}