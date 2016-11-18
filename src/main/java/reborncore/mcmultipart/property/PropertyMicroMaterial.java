package reborncore.mcmultipart.property;

import net.minecraftforge.common.property.IUnlistedProperty;
import reborncore.mcmultipart.microblock.IMicroMaterial;

public class PropertyMicroMaterial implements IUnlistedProperty<IMicroMaterial> {

	private final String name;

	public PropertyMicroMaterial(String name) {

		this.name = name;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public boolean isValid(IMicroMaterial value) {

		return value != null;
	}

	@Override
	public Class<IMicroMaterial> getType() {

		return IMicroMaterial.class;
	}

	@Override
	public String valueToString(IMicroMaterial value) {

		return value.getName();
	}

}
