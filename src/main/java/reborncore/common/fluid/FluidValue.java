package reborncore.common.fluid;

import com.google.common.base.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonHelper;

public final class FluidValue {

	public static final FluidValue EMPTY = new FluidValue(0);
	public static final FluidValue BUCKET = new FluidValue(1000);
	public static final FluidValue INFINITE = new FluidValue(Integer.MAX_VALUE);

	private final int rawValue;

	private FluidValue(final int rawValue) {
		this.rawValue = rawValue;
	}

	public FluidValue multiply(int value) {
		return fromRaw(rawValue * value);
	}

	public FluidValue add(FluidValue fluidValue) {
		return fromRaw(rawValue + fluidValue.rawValue);
	}

	public FluidValue subtract(FluidValue fluidValue) {
		return fromRaw(rawValue - fluidValue.rawValue);
	}

	public boolean isEmpty() {
		return rawValue == 0;
	}

	public boolean moreThan(FluidValue value) {
		return rawValue > value.rawValue;
	}

	public boolean equalOrMoreThan(FluidValue value) {
		return rawValue >= value.rawValue;
	}

	public boolean lessThan(FluidValue value) {
		return rawValue < value.rawValue;
	}

	public boolean lessThanOrEqual(FluidValue value) {
		return rawValue <= value.rawValue;
	}

	@Override
	public String toString() {
		return rawValue + " Mb";
	}

	//TODO move away from using this
	@Deprecated
	public int getRawValue() {
		return rawValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FluidValue that = (FluidValue) o;
		return rawValue == that.rawValue;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(rawValue);
	}

	@Deprecated
	public static FluidValue fromRaw(int rawValue) {
		if(rawValue < 0) {
			rawValue = 0;
		}
		return new FluidValue(rawValue);
	}

	public static FluidValue parseFluidValue(JsonElement jsonElement) {
		if (jsonElement.isJsonObject()) {
			final JsonObject jsonObject = jsonElement.getAsJsonObject();
			if(jsonObject.has("buckets")) {
				int buckets = JsonHelper.getInt(jsonObject, "buckets");
				return BUCKET.multiply(buckets);
			}
		} else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			//TODO add a warning here
			return fromRaw(jsonElement.getAsJsonPrimitive().getAsInt());
		}
		throw new JsonSyntaxException("Could not parse fluid value");
	}

}
