package reborncore.api.systems.conduit.functionalfaces;

import net.minecraft.item.Item;

public interface IConduitItemProvider {
	Item getItem(ConduitFunction function);

	ConduitFunction fromConduitFunction(Item item);
}
