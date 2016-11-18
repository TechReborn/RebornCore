package reborncore.mcmultipart.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import reborncore.mcmultipart.multipart.IMultipart;

public abstract class PartEvent extends Event {

	public final IMultipart part;

	public PartEvent(IMultipart part) {

		this.part = part;
	}

	public static class Add extends PartEvent {

		public Add(IMultipart part) {

			super(part);
		}

	}

	public static class Remove extends PartEvent {

		public Remove(IMultipart part) {

			super(part);
		}

	}

}
