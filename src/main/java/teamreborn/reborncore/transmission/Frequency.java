package teamreborn.reborncore.transmission;

/**
 * Created by Prospector
 */
public class Frequency {
	public final String name;
	public final String unlocalizedName;
	public final EnumFrequency enumFrequency;
	private boolean powered;

	public Frequency(String name, EnumFrequency enumFrequency, boolean powered) {
		this.name = name;
		this.unlocalizedName = "frequency.transmission:" + name + ".name";
		this.enumFrequency = enumFrequency;
		this.powered = powered;
	}

	public Frequency(String name, EnumFrequency enumFrequency) {
		this(name, enumFrequency, false);
	}

	public boolean isPowered() {
		return powered;
	}

	/**
	 * DO NOT USE THIS METHOD. USE ITransmissionReciever.setPowered() INSTEAD!!!
	 */
	@Deprecated
	public void setPowered(boolean powered) {
		this.powered = powered;
	}
}