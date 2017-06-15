/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.client.models;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;
import reborncore.common.misc.vecmath.Vecs3dCube;

import java.util.ArrayList;

/**
 * Created by modmuss50 on 09/05/2016.
 */
public class BakedModelUtils {

	public static void addCubeToList(Vecs3dCube cube, ArrayList<BakedQuad> list, BlockPartFace face,
	                                 ModelRotation modelRotation, TextureAtlasSprite cubeTexture, EnumFacing dir, FaceBakery faceBakery) {
		BlockFaceUV uv = new BlockFaceUV(new float[] { (float) cube.getMinX(), (float) cube.getMinY(),
			(float) cube.getMaxX(), (float) cube.getMaxY() }, 0);
		face = new BlockPartFace(dir, 0, "", uv);
		list.add(faceBakery.makeBakedQuad(
			new Vector3f((float) cube.getMinX(), (float) cube.getMinY(), (float) cube.getMinZ()),
			new Vector3f((float) cube.getMaxX(), (float) cube.getMinY(), (float) cube.getMaxZ()), face, cubeTexture,
			EnumFacing.DOWN, modelRotation, null, true, true));// down
		list.add(faceBakery.makeBakedQuad(
			new Vector3f((float) cube.getMinX(), (float) cube.getMaxY(), (float) cube.getMinZ()),
			new Vector3f((float) cube.getMaxX(), (float) cube.getMaxY(), (float) cube.getMaxZ()), face, cubeTexture,
			EnumFacing.UP, modelRotation, null, true, true));// up
		if (dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH) {
			BlockFaceUV uv2 = new BlockFaceUV(new float[] { (float) cube.getMaxX() + 4, (float) cube.getMinY(),
				(float) 16, (float) cube.getMaxY() }, 0);
			list.add(faceBakery.makeBakedQuad(
				new Vector3f((float) cube.getMinX(), (float) cube.getMinY(), (float) cube.getMinZ()),
				new Vector3f((float) cube.getMaxX(), (float) cube.getMaxY(), (float) cube.getMaxZ()), new BlockPartFace(dir, 0, "", uv2), cubeTexture,
				EnumFacing.NORTH, modelRotation, null, true, true));// north
		} else {
			list.add(faceBakery.makeBakedQuad(
				new Vector3f((float) cube.getMinX(), (float) cube.getMinY(), (float) cube.getMinZ()),
				new Vector3f((float) cube.getMaxX(), (float) cube.getMaxY(), (float) cube.getMaxZ()), face, cubeTexture,
				EnumFacing.NORTH, modelRotation, null, true, true));// north
		}

		list.add(faceBakery.makeBakedQuad(
			new Vector3f((float) cube.getMinX(), (float) cube.getMinY(), (float) cube.getMaxZ()),
			new Vector3f((float) cube.getMaxX(), (float) cube.getMaxY(), (float) cube.getMaxZ()), face, cubeTexture,
			EnumFacing.SOUTH, modelRotation, null, true, true));// south
		list.add(faceBakery.makeBakedQuad(
			new Vector3f((float) cube.getMaxX(), (float) cube.getMinY(), (float) cube.getMinZ()),
			new Vector3f((float) cube.getMaxX(), (float) cube.getMaxY(), (float) cube.getMaxZ()), face, cubeTexture,
			EnumFacing.EAST, modelRotation, null, true, true));// east
		list.add(faceBakery.makeBakedQuad(
			new Vector3f((float) cube.getMinX(), (float) cube.getMinY(), (float) cube.getMinZ()),
			new Vector3f((float) cube.getMinX(), (float) cube.getMaxY(), (float) cube.getMaxZ()), face, cubeTexture,
			EnumFacing.WEST, modelRotation, null, true, true));// west
	}

}
