package reborncore.modloader.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EventHandler<T> {

	private List<T> handlers = new ArrayList<>();

	public void register(T handler){
		handlers.add(handler);
	}

	public void post(Consumer<T> consumer){
		handlers.forEach(consumer);
	}

	public List<T> getHandlers() {
		return Collections.unmodifiableList(handlers);
	}
}
