package reborncore.api.systems.conduit.functionalfaces;

import net.minecraft.item.Item;
import reborncore.api.systems.functionalface.FunctionalFace;

public class ConduitFunctionalFace extends FunctionalFace {

	public ConduitFunction conduitFunction;

	public ConduitFunctionalFace(ConduitFunction conduitFunction){
		this.conduitFunction = conduitFunction;
	}

	public static ConduitFunctionalFace fromFunction(ConduitFunction conduitFunction){
		return new ConduitFunctionalFace(conduitFunction);
	}

	@Override
	public int getMaxCount() {
		return conduitFunction.max;
	}

	public Item getItem() {
		return conduitFunction.requiredItem;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConduitFunctionalFace){
			ConduitFunctionalFace functionalFace = (ConduitFunctionalFace)obj;

			if(functionalFace.conduitFunction == this.conduitFunction){
				return true;
			}
		}

		return super.equals(obj);
	}
}
