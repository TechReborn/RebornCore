package reborncore.api.systems.conduit.functionalfaces;

import net.minecraft.item.Item;
import reborncore.api.systems.functionalface.FunctionalFace;

public class ConduitFunctionalFace extends FunctionalFace {

	public ConduitFunction conduitFunction;

	private final IConduitItemProvider provider;

	public ConduitFunctionalFace(ConduitFunction conduitFunction, IConduitItemProvider provider) {
		this.conduitFunction = conduitFunction;
		this.provider = provider;
	}

	public static ConduitFunctionalFace fromFunction(ConduitFunction conduitFunction, IConduitItemProvider provider) {
		return new ConduitFunctionalFace(conduitFunction, provider);
	}

	@Override
	public int getMaxCount() {
		return conduitFunction.max;
	}

	public Item getItem() {
		return provider.getItem(conduitFunction);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConduitFunctionalFace) {
			ConduitFunctionalFace functionalFace = (ConduitFunctionalFace) obj;

			if (functionalFace.conduitFunction == this.conduitFunction) {
				return true;
			}
		}

		return super.equals(obj);
	}
}
