package reborncore.common.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public interface DataAttachmentProvider {

	static DataAttachmentProvider get(World world){
		Validate.isInstanceOf(ServerWorld.class, world, "world must be server!");
		return (DataAttachmentProvider) ((ServerWorld)world).getSaveHandler();
	}

	static <T extends DataAttachment> T get(World world, Class<T> clazz){
		return get(world).getAttachment(clazz);
	}

	<T extends DataAttachment> T getAttachment(Class<T> clazz);

}
