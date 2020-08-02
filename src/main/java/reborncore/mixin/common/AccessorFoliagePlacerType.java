package reborncore.mixin.common;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FoliagePlacerType.class)
public interface AccessorFoliagePlacerType {

	@Invoker("register")
	static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, Codec<P> codec) {
		throw new UnsupportedOperationException();
	}
}
