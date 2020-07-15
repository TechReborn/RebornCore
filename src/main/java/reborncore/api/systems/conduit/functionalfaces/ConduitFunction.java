package reborncore.api.systems.conduit.functionalfaces;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum ConduitFunction {
	EXPORT (Items.BEACON,6),
	IMPORT (Items.HOPPER, 6),
	BLOCK (Items.BEDROCK, 6),
	ONE_WAY (Items.PISTON, 1);

	public int max;
	public Item requiredItem;

	ConduitFunction(Item item, int max){
		this.requiredItem = item;
		this.max = max;
	}

	public static ConduitFunction fromItem(Item item){
		for(ConduitFunction conduitFunction : ConduitFunction.values()){
			if(conduitFunction.requiredItem == item){
				return conduitFunction;
			}
		}

		return null;
	}
}
