package reborncore.common.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import reborncore.common.fluid.container.ItemFluidInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RebornFluidManager {

	private static final HashMap<Identifier, RebornFluid> fluids = new HashMap<>();

	private static Lazy<Map<Fluid, BucketItem>> bucketMap;

	public static void register(RebornFluid rebornFluid, Identifier identifier){
		fluids.put(identifier, rebornFluid);
		Registry.register(Registry.FLUID, identifier, rebornFluid);
	}

	public static void setupBucketMap(){
		bucketMap = new Lazy<>(() -> {
			Map<Fluid, BucketItem> map = new HashMap<>();
			Registry.ITEM.stream().filter(item -> item instanceof BucketItem).forEach(item -> {
				BucketItem bucketItem = (BucketItem) item;
				//We can be sure of this as we add this via a mixin
				ItemFluidInfo fluidInfo = (ItemFluidInfo) bucketItem;
				Fluid fluid = fluidInfo.getFluid(new ItemStack(item));
				if(!map.containsKey(fluid)){
					map.put(fluid, bucketItem);
				}
			});
			return map;
		});
	}

	public static Map<Fluid, BucketItem> getBucketMap(){
		return bucketMap.get();
	}

	public static HashMap<Identifier, RebornFluid> getFluids() {
		return fluids;
	}

	public static Stream<RebornFluid> getFluidStream(){
		return fluids.values().stream();
	}
}
