package reborncore.common.util;

@FunctionalInterface
public interface BooleanFunction<T> {

	boolean get(T type);
}
