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
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import reborncore.client.multiblock.component.MultiblockComponent;
import reborncore.common.multiblock.CoordTriplet;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a Mutiblock object. It's used to display a multiblock in
 * the pda and to show the player in a ghost-like look in the world.
 */
public class Multiblock {

	public List<MultiblockComponent> components = new ArrayList<>();

	public int minX, minY, minZ, maxX, maxY, maxZ, offX, offY, offZ;

	/**
	 * Adds a multiblock component to this multiblock. The component's x y z
	 * coords should be pivoted to the center of the structure.
	 */
	public void addComponent(MultiblockComponent component) {
		components.add(component);
		changeAxisForNewComponent(component.relPos.getX(), component.relPos.getY(), component.relPos.getZ());
	}

	/**
	 * Constructs and adds a multiblock component to this multiblock. The x y z
	 * coords should be pivoted to the center of the structure.
	 */
	public void addComponent(BlockPos pos, IBlockState state) {
		addComponent(new MultiblockComponent(pos, state));
	}

	private void changeAxisForNewComponent(int x, int y, int z) {
		if (x < minX)
			minX = x;
		else if (x > maxX)
			maxX = x;

		if (y < minY)
			minY = y;
		else if (y > maxY)
			maxY = y;

		if (z < minZ)
			minZ = z;
		else if (z > maxZ)
			maxZ = z;
	}
	public void setRenderOffset(int x, int y, int z) {
		offX = x;
		offY = y;
		offZ = z;
	}

	public List<MultiblockComponent> getComponents() {
		return components;
	}

	/**
	 * Rotates this multiblock by the angle passed in. For the best results, use
	 * only multiples of pi/2.
	 */
	public void rotate(double angle) {
		for (MultiblockComponent comp : getComponents())
			comp.rotate(angle);
	}

	public Multiblock copy() {
		Multiblock mb = new Multiblock();
		for (MultiblockComponent comp : getComponents())
			mb.addComponent(comp.copy());

		return mb;
	}

	/**
	 * Creates a length 4 array of all the rotations multiple of pi/2 required
	 * to render this multiblock in the world relevant to the 4 cardinal
	 * orientations.
	 */
	public Multiblock[] createRotations() {
		Multiblock[] blocks = new Multiblock[4];
		blocks[0] = this;
		blocks[1] = blocks[0].copy();
		blocks[1].rotate(Math.PI / 2);
		blocks[2] = blocks[1].copy();
		blocks[2].rotate(Math.PI / 2);
		blocks[3] = blocks[2].copy();
		blocks[3].rotate(Math.PI / 2);

		return blocks;
	}

	/**
	 * Makes a MultiblockSet from this Multiblock and its rotations using
	 * createRotations().
	 */
	public MultiblockSet makeSet() {
		return new MultiblockSet(this);
	}

	public int getXSize() {
		return Math.abs(minX) + Math.abs(maxX) + 1;
	}

	public int getYSize() {
		return Math.abs(minY) + Math.abs(maxY) + 1;
	}

	public int getZSize() {
		return Math.abs(minZ) + Math.abs(maxZ) + 1;
	}

}
