package reborncore.common.recipe.ingredients;

import net.minecraft.util.ResourceLocation;

public class OreIngredient extends BaseIngredient<String> {

	String oreDict;

	public OreIngredient(ResourceLocation type, String oreDict) {
		super(type);
		this.oreDict = oreDict;
	}

	@Override
	public String get() {
		return oreDict;
	}

	@Override
	public boolean matches(Object obj) {
		//TODO obj could be a string, or an item stack
		return false;
	}

	@Override
	public Class getHeldClass() {
		return String.class;
	}
}
