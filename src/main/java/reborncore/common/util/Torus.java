package reborncore.common.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Torus {

	public static List<BlockPos> generate(BlockPos orgin, int radius){
		List<BlockPos> posLists = new ArrayList<>();
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					if(Math.pow(radius /2 - Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)), 2) + Math.pow(z, 2) < Math.pow(radius * 0.05, 2)){
						posLists.add(orgin.add(x, z, y));
					}
				}
			}
		}
		return posLists;
	}

}
