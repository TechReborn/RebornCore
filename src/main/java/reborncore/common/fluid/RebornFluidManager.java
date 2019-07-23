package reborncore.common.fluid;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.stream.Stream;

public class RebornFluidManager {

	private static final HashMap<Identifier, RebornFluid> fluids = new HashMap<>();

	public static void register(RebornFluid rebornFluid, Identifier identifier){
		fluids.put(identifier, rebornFluid);
		Registry.register(Registry.FLUID, identifier, rebornFluid);
	}

	public static HashMap<Identifier, RebornFluid> getFluids() {
		return fluids;
	}

	public static Stream<RebornFluid> getFluidStream(){
		return fluids.values().stream();
	}
}
