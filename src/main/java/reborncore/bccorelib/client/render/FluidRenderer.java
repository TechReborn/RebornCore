/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package reborncore.bccorelib.client.render;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class FluidRenderer {
    public static final FluidRenderer INSTANCE = new FluidRenderer();

    public enum FluidType {
        FLOWING,
        STILL,
        FROZEN
    }

    public static final int DISPLAY_STAGES = 100;
    public static final Vec3d BLOCK_SIZE = new Vec3d(0.98, 0.98, 0.98);
    private final Map<Fluid, Map<Vec3d, int[]>> flowingRenderCache = Maps.newHashMap();
    private final Map<Fluid, Map<Vec3d, int[]>> stillRenderCache = Maps.newHashMap();
    private final Map<Fluid, Map<Vec3d, int[]>> frozenRenderCache = Maps.newHashMap();
    private final Map<FluidType, Map<Fluid, TextureAtlasSprite>> textureMap = Maps.newHashMap();

    private static TextureAtlasSprite missingIcon = null;

    /** Deactivate default constructor */
    private FluidRenderer() {}

    @SubscribeEvent
    public void modelBakeEvent(ModelBakeEvent event) {
        flowingRenderCache.clear();
        stillRenderCache.clear();
        frozenRenderCache.clear();
    }

    @SubscribeEvent
    public void textureStitchPost(TextureStitchEvent.Post event) {
        flowingRenderCache.clear();
        stillRenderCache.clear();
        frozenRenderCache.clear();
        TextureMap map = event.getMap();
        missingIcon = map.getMissingSprite();

        textureMap.clear();

        for (FluidType type : FluidType.values()) {
            textureMap.put(type, new HashMap<Fluid, TextureAtlasSprite>());
        }

        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            // TextureAtlasSprite toUse = null;

            if (fluid.getFlowing() != null) {
                String flow = fluid.getFlowing().toString();
                TextureAtlasSprite sprite;
                if (map.getTextureExtry(flow) != null) {
                    sprite = map.getTextureExtry(flow);
                } else {
                    sprite = map.registerSprite(fluid.getFlowing());
                }
                // toUse = sprite;
                textureMap.get(FluidType.FLOWING).put(fluid, sprite);
            }

            if (fluid.getStill() != null) {
                String still = fluid.getStill().toString();
                TextureAtlasSprite sprite;
                if (map.getTextureExtry(still) != null) {
                    sprite = map.getTextureExtry(still);
                } else {
                    sprite = map.registerSprite(fluid.getStill());
                }
                // toUse = sprite;
                textureMap.get(FluidType.STILL).put(fluid, sprite);
            }
            // if (toUse != null) {
            // textureMap.get(FluidType.FROZEN).put(fluid, toUse);
            // }
        }
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack stack, FluidType type) {
        if (stack == null) {
            return missingIcon;
        }
        return getFluidTexture(stack.getFluid(), type);
    }

    /** This will always return a texture object, but it will be the missing icon texture if the fluid is null or a
     * texture does not exist. */
    public static TextureAtlasSprite getFluidTexture(Fluid fluid, FluidType type) {
        if (fluid == null || type == null) {
            return missingIcon;
        }
        Map<Fluid, TextureAtlasSprite> map = INSTANCE.textureMap.get(type);
        return map.containsKey(fluid) ? map.get(fluid) : missingIcon;
    }

}
