package reborncore.common.util;

import net.minecraft.util.NonNullList;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

//Taken from https://github.com/The-Acronym-Coders/BASE/blob/develop/1.12.0/src/main/java/com/teamacronymcoders/base/util/collections/NonnullListCollector.java, thanks for this ;)
public class NonNullListCollector<T> implements Collector<T, NonNullList<T>, NonNullList<T>> {

	private final Set<Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

	public static <T> NonNullListCollector<T> toList() {
		return new NonNullListCollector<>();
	}

	@Override
	public Supplier<NonNullList<T>> supplier() {
		return NonNullList::create;
	}

	@Override
	public BiConsumer<NonNullList<T>, T> accumulator() {
		return NonNullList::add;
	}

	@Override
	public BinaryOperator<NonNullList<T>> combiner() {
		return (left, right) -> {
			left.addAll(right);
			return left;
		};
	}

	@Override
	public Function<NonNullList<T>, NonNullList<T>> finisher() {
		return i -> (NonNullList<T>) i;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return CH_ID;
	}
}