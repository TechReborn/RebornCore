package teamreborn.reborncore.transmission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prospector
 */
public interface ITransmissionReciever {

	public default boolean isPowered(EnumFrequency frequency) {
		if (getValidEnumFrequencies().contains(frequency))
			for (Frequency f : getValidFrequencies()) {
				if (f.enumFrequency.equals(frequency) && f.isPowered()) {
					return true;
				}
			}
		return false;
	}

	public List<Frequency> getValidFrequencies();

	public default List<EnumFrequency> getValidEnumFrequencies() {
		List<EnumFrequency> enumFrequencies = new ArrayList<>();
		for (Frequency frequency : getValidFrequencies())
			enumFrequencies.add(frequency.enumFrequency);
		return enumFrequencies;
	}

	@SuppressWarnings("deprecation")
	public default void setPowered(EnumFrequency frequency, boolean power) {
		if (getValidEnumFrequencies().contains(frequency)) {
			for (Frequency f : getValidFrequencies()) {
				if (f.enumFrequency.equals(frequency)) {
					f.setPowered(power);
					onPowerUpdate();
				}
			}
		}
	}

	public void onPowerUpdate();
}
