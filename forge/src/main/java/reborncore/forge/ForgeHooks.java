package reborncore.forge;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.modloader.ModLoaderHooks;
import reborncore.modloader.Side;
import reborncore.modloader.networking.PacketContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ForgeHooks implements ModLoaderHooks {

	@Override
	public Side getSide() {
		return FMLEnvironment.dist.isClient() ? Side.CLIENT : Side.SERVER;
	}

	@Override
	public File getConfigDir() {
		return FMLPaths.CONFIGDIR.get().toFile();
	}

	@Override
	public boolean isModLoaded(String s) {
		return ModList.get().isLoaded(s);
	}

	@Override
	public int getSlotID(Slot slot) {
		return slot.getSlotIndex();
	}

	@Override
	public List<IContainerListener> getListeners(Container container) {
		return null;
	}

	@Override
	public void registerServerBoundHandler(ResourceLocation resourceLocation, BiConsumer<ExtendedPacketBuffer, PacketContext> biConsumer) {

	}

	@Override
	public void registerClientBoundHandler(ResourceLocation resourceLocation, BiConsumer<ExtendedPacketBuffer, PacketContext> biConsumer) {

	}

	@Override
	public FlowingFluid getFluidFromBlock(FlowingFluidBlock flowingFluidBlock) {
		return flowingFluidBlock.getFluid();
	}

	@Override
	public <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> getAllRecipeTypes(IRecipeType<T> iRecipeType, RecipeManager recipeManager) {
		return null;
	}
}
