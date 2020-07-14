package reborncore.common.systems.conduit;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;


public interface IConduit<T> {
	boolean canConnect(Direction face, IConduit<T> otherEntity);
	void addConduit(Direction direction, IConduit<T> conduit);
	void removeConduit(Direction direction);

	boolean addFunctionality(Direction face, ItemStack playerHolding);
	ItemStack removeFunctionality(Direction face);

	boolean transferItem(IConduitTransfer<T> transfer, Direction origin);


}
