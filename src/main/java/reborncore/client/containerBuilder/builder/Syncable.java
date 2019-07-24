package reborncore.client.containerBuilder.builder;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Syncable {

	//TODO make this better
	void getSyncPair(List<Pair<Supplier, Consumer>> pairList);

}
