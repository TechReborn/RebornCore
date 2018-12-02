package reborncore.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Objects;

public enum ObjectBufferUtils {

	STRING(String.class, (string, buffer) -> {
		buffer.writeInt(string.length());
		buffer.writeString(string);
	}, buffer -> {
		return buffer.readString(buffer.readInt());
	}),
	INT(Integer.class, (value, buffer) -> {
		buffer.writeInt(value);
	}, PacketBuffer::readInt),
	BLOCK_POS(BlockPos.class, (pos, buffer) -> {
		buffer.writeBlockPos(pos);
	}, PacketBuffer::readBlockPos);

	Class clazz;
	ObjectWriter writer;
	ObjectReader reader;

	<T> ObjectBufferUtils(Class<T> clazz, ObjectWriter<T> writer, ObjectReader<T> reader) {
		this.clazz = clazz;
		this.writer = writer;
		this.reader = reader;
	}

	public static void writeObject(Object object, PacketBuffer buffer){
		ObjectBufferUtils utils = Arrays.stream(values()).filter(objectBufferUtils -> objectBufferUtils.clazz == object.getClass()).findFirst().orElse(null);
		Objects.requireNonNull(utils, "No support found for " + object.getClass());
		buffer.writeInt(utils.ordinal());
		utils.writer.write(object, buffer);
	}

	public static Object readObject(PacketBuffer buffer){
		ObjectBufferUtils utils = values()[buffer.readInt()];
		Objects.requireNonNull(utils, "Could not find reader");
		return utils.reader.read(buffer);
	}

	private interface ObjectWriter<T> {
		void write(T object, PacketBuffer buffer);
	}

	private interface ObjectReader<T> {
		T read(PacketBuffer buffer);
	}

}
