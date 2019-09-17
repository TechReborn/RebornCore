package reborncore.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.FluidBlock;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.ThreadExecutor;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.fabric.mixin.extensions.ContainerExtensions;
import reborncore.fabric.mixin.extensions.FluidBlockExtensions;
import reborncore.fabric.mixin.extensions.RecipeManagerExtensions;
import reborncore.fabric.mixin.extensions.SlotExtensions;
import reborncore.modloader.Side;
import net.fabricmc.loader.api.FabricLoader;
import reborncore.modloader.ModLoaderHooks;
import reborncore.modloader.networking.PacketContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FabricHooks implements ModLoaderHooks {

	@Override
	public Side getSide() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER;
	}

	@Override
	public File getConfigDir() {
		return FabricLoader.getInstance().getConfigDirectory();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public int getSlotID(Slot slot) {
		return ((SlotExtensions)slot).getInvSlot();
	}

	@Override
	public List<ContainerListener> getListeners(Container container) {
		return ContainerExtensions.get(container).getListeners();
	}

	@Override
	public void registerServerBoundHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer) {
		ServerSidePacketRegistry.INSTANCE.register(identifier, (packetContext, packetByteBuf) -> consumer.accept(new ExtendedPacketBuffer(packetByteBuf), new FabricPacketContext(packetContext)));
	}

	@Override
	public void registerClientBoundHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer) {
		ClientSidePacketRegistry.INSTANCE.register(identifier, (packetContext, packetByteBuf) -> consumer.accept(new ExtendedPacketBuffer(packetByteBuf), new FabricPacketContext(packetContext)));
	}

	@Override
	public BaseFluid getFluidFromBlock(FluidBlock block) {
		return ((FluidBlockExtensions) block).getFluid();
	}

	@Override
	public <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllRecipeTypes(RecipeType<T> type, RecipeManager recipeManager) {
		RecipeManagerExtensions recipeManagerExtensions = (RecipeManagerExtensions) recipeManager;
		return recipeManagerExtensions.getAll(type);
	}

	private static class FabricPacketContext implements PacketContext {

		private final net.fabricmc.fabric.api.network.PacketContext parent;

		public FabricPacketContext(net.fabricmc.fabric.api.network.PacketContext parent) {
			this.parent = parent;
		}

		@Override
		public Side getSide() {
			return parent.getPacketEnvironment() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER;
		}

		@Override
		public PlayerEntity getPlayer() {
			return parent.getPlayer();
		}

		@Override
		public ThreadExecutor getTaskQueue() {
			return parent.getTaskQueue();
		}
	}
}
