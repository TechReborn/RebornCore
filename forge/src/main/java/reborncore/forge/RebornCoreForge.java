package reborncore.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import reborncore.RebornCore;
import reborncore.RebornCoreClient;
import reborncore.modloader.events.ItemCraftEvent;

import java.util.function.Consumer;

@Mod("reborncore")
public class RebornCoreForge {

	public RebornCoreForge() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		RebornCore.hooks = new ForgeHooks();
		RebornCoreClient.hooks = new ForgeHooksClient();

		RebornCore.INSTANCE.onInitialize();
		RebornCoreClient.INSTANCE.onInitializeClient();

	}

	@SubscribeEvent
	public void craft(PlayerEvent.ItemCraftedEvent event){
		ItemCraftEvent.HANDLER.post(itemCraftEvent -> itemCraftEvent.onCraft(event.getCrafting(), event.getPlayer()));
	}
}
