/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package reborncore.client.multiblock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import java.util.HashMap;
import java.util.Map;

/**
 * A hook for rendering blocks in the multiblock display.
 */
public interface IMultiblockRenderHook {

    public static Map<Block, IMultiblockRenderHook> renderHooks = new HashMap();

    public void renderBlockForMultiblock(IBlockAccess world, Multiblock mb, Block block, int meta, BlockRendererDispatcher renderBlocks);

}
