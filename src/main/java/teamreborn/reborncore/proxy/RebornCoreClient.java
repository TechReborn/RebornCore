package teamreborn.reborncore.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.rip.RIPHud;
import teamreborn.techreborn.TRConstants;
import teamreborn.techreborn.TechReborn;
import teamreborn.techreborn.fluids.TechRebornFluids;

/**
 * Created by Prospector
 */
public class RebornCoreClient extends RebornCoreServer {

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RIPHud());
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
