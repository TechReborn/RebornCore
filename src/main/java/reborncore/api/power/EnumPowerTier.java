package reborncore.api.power;

/**
 * Created by modmuss50 on 08/03/2016.
 */
public enum EnumPowerTier {
	LOW(32, 32),
	MEDIUM(128, 128),
	HIGH(512, 512),
	EXTREME(2048, 2048);

	private final int maxInput;
	private final int maxOutput;

	EnumPowerTier(int maxInput, int maxOutput) {
		this.maxInput = maxInput;
		this.maxOutput = maxOutput;
	}

	public int getMaxInput() {
		return maxInput;
	}

	public int getMaxOutput() {
		return maxOutput;
	}
}
