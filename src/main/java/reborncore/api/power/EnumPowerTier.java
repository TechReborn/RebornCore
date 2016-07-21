package reborncore.api.power;

/**
 * Created by modmuss50 on 08/03/2016.
 */
public enum EnumPowerTier {

	LOW(0, 32), MEDIUM(1, 128), HIGH(2, 512), EXTREME(3, 2048), INSANE(Integer.MAX_VALUE, 8192);

	public final int ic2SinkTier;
	public final int voltage;

	EnumPowerTier(int ic2SinkTier, int voltage) {
		this.ic2SinkTier = ic2SinkTier;
		this.voltage = voltage;
	}

}
