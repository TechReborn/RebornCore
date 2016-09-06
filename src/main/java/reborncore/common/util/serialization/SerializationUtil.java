package reborncore.common.util.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.ItemStack;

public class SerializationUtil {

	public static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.enableComplexMapKeySerialization()
		.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
		.create();
}
