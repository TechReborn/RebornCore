package reborncore.common.util;

@FunctionalInterface
public interface ObjectConsumer<T> {

	void accept(T value);
}
