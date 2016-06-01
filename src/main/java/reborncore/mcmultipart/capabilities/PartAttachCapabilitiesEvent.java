package reborncore.mcmultipart.capabilities;

import reborncore.mcmultipart.multipart.IMultipart;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class PartAttachCapabilitiesEvent extends AttachCapabilitiesEvent {

    private final IMultipart part;

    public PartAttachCapabilitiesEvent(IMultipart part) {

        super(part);
        this.part = part;
    }

    public IMultipart getPart() {

        return part;
    }

}
