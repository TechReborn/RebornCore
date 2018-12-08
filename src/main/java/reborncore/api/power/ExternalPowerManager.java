package reborncore.api.power;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reborncore.common.powerSystem.TilePowerAcceptor;

public interface ExternalPowerManager {

	public ExternalPowerHandler createPowerHandler(TilePowerAcceptor acceptor);

	public boolean isPoweredItem(ItemStack stack);

	public boolean isPoweredTile(TileEntity tileEntity);

	public void dischargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack);

	public void chargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack);

}
