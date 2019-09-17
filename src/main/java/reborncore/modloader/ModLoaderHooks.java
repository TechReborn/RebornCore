package reborncore.modloader;

import net.minecraft.block.FluidBlock;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.modloader.networking.PacketContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ModLoaderHooks {

	Side getSide();

	File getConfigDir();

	boolean isModLoaded(String modId);

	int getSlotID(Slot slot);

	List<ContainerListener> getListeners(Container container);

	void registerServerBoundHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer);

	void registerClientBoundHandler(Identifier identifier, BiConsumer<ExtendedPacketBuffer, PacketContext> consumer);

	BaseFluid getFluidFromBlock(FluidBlock block);

	<C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllRecipeTypes(RecipeType<T> type, RecipeManager recipeManager);

}
