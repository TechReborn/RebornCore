package reborncore.mcmultipart.capabilities;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import reborncore.mcmultipart.multipart.IMultipart;

public class PartAttachCapabilitiesEvent extends AttachCapabilitiesEvent {

	private final IMultipart part;

	public PartAttachCapabilitiesEvent(IMultipart part) {

		super(IMultipart.class, part);
		this.part = part;
	}

	public IMultipart getPart() {

		return part;
	}

}
