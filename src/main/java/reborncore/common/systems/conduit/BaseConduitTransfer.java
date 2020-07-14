package reborncore.common.systems.conduit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public abstract class BaseConduitTransfer<T> implements IConduitTransfer<T> {
	private T stored;

	private int progress = 0;
	private int duration;

	// Direction which the item came from
	private Direction origin;

	// Used for rendering, not important
	private Direction target;

	public BaseConduitTransfer(T stored, int duration, Direction origin){
		this.stored = stored;
		this.duration = duration;
		this.origin = origin;
	}

	protected BaseConduitTransfer() {
	}

	@Override
	public void progress(){
		if(!isFinished()) {
			progress++;
		}
	}

	// Helper functions
	@Override
	public boolean isFinished(){
		return progress >= duration;
	}

	@Override
	public void restartProgress(){
		progress = 0;
	}

	@Override
	public float getProgressPercent(){
		return ((float)progress / (float)duration);
	}

	@Override
	public boolean isEmpty(){
		return false;
	}


	// NBT
	protected void fromTagBase(CompoundTag tag) {
		this.progress = tag.getInt("tickProgress");
		this.duration = tag.getInt("tickFinish");

		this.origin = Direction.byId(tag.getInt("fromDirection"));
	}

	protected CompoundTag toTagBase(CompoundTag tag) {
		tag.putInt("tickProgress", progress);
		tag.putInt("tickFinish", duration);
		tag.putInt("fromDirection", origin.getId());

		return tag;
	}

	// Getter/Setters
	@Override
	public T getStored(){
		return this.stored;
	}

	@Override
	public void setStored(T stored) {
		this.stored = stored;
	}

	@Override
	public Direction getOriginDirection() {
		return origin;
	}

	@Override
	public void setOriginDirection(Direction origin) {
		this.origin = origin;
	}

	@Override
	public Direction getTargetDirection() {
		return target;
	}

	@Override
	public void setTargetDirection(Direction target) {
		this.target = target;
	}
}
