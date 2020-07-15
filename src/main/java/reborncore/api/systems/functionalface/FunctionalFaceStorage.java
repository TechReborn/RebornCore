package reborncore.api.systems.functionalface;

import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FunctionalFaceStorage<F extends FunctionalFace> {

	private final Map<Direction, F> functionMap = new HashMap<>();

	public void addFunctionalFace(Direction direction, F function) {
		functionMap.put(direction, function);
	}

	public boolean canAddFunctionality(Direction direction, F function) {
		if (functionMap.containsKey(direction)) return false;

		int count = (int) functionMap.values().stream().filter(mapMode -> mapMode == function).count();

		return count < function.getMaxCount();
	}

	public boolean hasFunctionality(Direction face) {
		return functionMap.containsKey(face);
	}

	public boolean hasFunctionality(F function) {
		return functionMap.containsValue(function);
	}

	public void clearFunctionaries() {
		functionMap.clear();
	}

	public F getFunctionality(Direction face) {
		return functionMap.get(face);
	}

	public void removeFunctionality(Direction face) {
		functionMap.remove(face);
	}

	public boolean isEmpty() {
		return functionMap.isEmpty();
	}

	public int getSize() {
		return functionMap.size();
	}

	public Set<Map.Entry<Direction, F>> getEntrySet() {
		return functionMap.entrySet();
	}
}
