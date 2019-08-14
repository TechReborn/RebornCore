package reborncore.common.world;

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataAttachmentRegistry {

	private static HashMap<Class<? extends DataAttachment>, Supplier<? extends DataAttachment>> dataAttachments = new HashMap<>();

	private static boolean locked = false;

	public <T extends DataAttachment> void register(Class<T> clazz, Supplier<T> supplier){
		Validate.isTrue(!locked, "Data attachment registry is locked!");
		if(dataAttachments.containsKey(clazz)){
			throw new UnsupportedOperationException("Data attachment already registered for " + clazz.getName());
		}
		dataAttachments.put(clazz, supplier);
	}

	public HashMap<Class<? extends DataAttachment>, Supplier<? extends DataAttachment>> getAllDataAttachments(){
		Validate.isTrue(locked, "Data attachment registry is not locked!");
		return dataAttachments;
	}

	public void lock(){
		locked = true;
	}

}
