package reborncore.common.misc;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyString implements IUnlistedProperty<String> {
	private final String name;


	private UnlistedPropertyString(String n) {
		name = n;
	}

	public static UnlistedPropertyString create(String name) {
		return new UnlistedPropertyString(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid(String value) {
		return true;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public String valueToString(String value) {
		return value;
	}
}
