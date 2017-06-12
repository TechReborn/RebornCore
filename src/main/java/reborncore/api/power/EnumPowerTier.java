package reborncore.api.power;

/**
 * Created by modmuss50 on 08/03/2016.
 */
public enum EnumPowerTier {
	MICRO(8, 8, 0),
	LOW(32, 32, 1),
	MEDIUM(128, 128, 2),
	HIGH(512, 512, 3),
	EXTREME(2048, 2048, 4),
	INSANE(8192, 8192, 5),
	INFINITE(Integer.MAX_VALUE, Integer.MAX_VALUE, 6);

	private final int maxInput;
	private final int maxOutput;
	private final int ic2Tier;

	EnumPowerTier(int maxInput, int maxOutput, int ic2Tier) {
		this.maxInput = maxInput;
		this.maxOutput = maxOutput;
		this.ic2Tier = ic2Tier;
	}

	public int getMaxInput() {
		return maxInput;
	}

	public int getMaxOutput() {
		return maxOutput;
	}

	public int getIC2Tier() {
		return ic2Tier;
	}

	public static int getIntTeir(int power) {
		for (EnumPowerTier tier : EnumPowerTier.values()) {
			if (tier.maxInput >= power) {
				return tier.getIC2Tier();
			}
		}
		return EnumPowerTier.INFINITE.getIC2Tier();
	}

	public static EnumPowerTier getTeir(int power) {
		for (EnumPowerTier tier : EnumPowerTier.values()) {
			if (tier.maxInput >= power) {
				return tier;
			}
		}
		return EnumPowerTier.INFINITE;
	}
}
