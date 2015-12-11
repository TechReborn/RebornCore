package reborncore;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import reborncore.jsonDestroyers.block.ModelGenertator;
import reborncore.jsonDestroyers.fluid.FluidModelGenerator;
import reborncore.jsonDestroyers.item.ItemModelGenerator;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ModelGenertator.register();
        ItemModelGenerator.register();
        FluidModelGenerator.register();
    }
}
