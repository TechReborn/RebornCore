package reborncore.common.util;

import net.minecraft.util.EnumFacing;
import reborncore.common.tile.TileLegacyMachineBase;

public enum  MachineFacing {
	FRONT,
	BACK,
	UP,
	DOWN,
	LEFT,
	RIGHT;

	public EnumFacing getFacing(TileLegacyMachineBase machineBase){
		if(this == FRONT){
			return machineBase.getFacing();
		}
		if(this == BACK){
			return machineBase.getFacing().getOpposite();
		}
		if(this == RIGHT){
			//North -> West
			int i = machineBase.getFacing().getOpposite().getHorizontalIndex() +1;
			if(i > 3){
				i = 0;
			}
			if(i < 0){
				i = 3;
			}
			return EnumFacing.HORIZONTALS[i];
		}
		if(this == LEFT){
			//North -> East
			int i = machineBase.getFacing().getOpposite().getHorizontalIndex() - 1;
			if(i > 3){
				i = 0;
			}
			if(i < 0){
				i = 3;
			}
			return EnumFacing.HORIZONTALS[i];
		}
		if(this == UP){
			return EnumFacing.UP;
		}
		if(this == DOWN){
			return EnumFacing.DOWN;
		}

		return EnumFacing.NORTH;
	}

}
