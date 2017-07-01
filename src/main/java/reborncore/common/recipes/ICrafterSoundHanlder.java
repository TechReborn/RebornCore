package reborncore.common.recipes;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by Mark on 01/07/2017.
 */
public interface ICrafterSoundHanlder {

	public void playSound(boolean firstRun, TileEntity tileEntity);

}
