package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reborncore.RebornCore;
import reborncore.api.power.ExternalPowerManager;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class ExternalPowerSystems implements IRegistryFactory {

	public static List<ExternalPowerManager> externalPowerHandlerList = new ArrayList<>();


	public static boolean isPoweredItem(ItemStack stack){
		return externalPowerHandlerList.stream().anyMatch(externalPowerManager -> externalPowerManager.isPoweredItem(stack));
	}

	public static void dischargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack){
		externalPowerHandlerList.forEach(externalPowerManager -> externalPowerManager.dischargeItem(tilePowerAcceptor, stack));
	}

	public static void chargeItem(TilePowerAcceptor tilePowerAcceptor, ItemStack stack){
		externalPowerHandlerList.forEach(externalPowerManager -> externalPowerManager.chargeItem(tilePowerAcceptor, stack));
	}

	public static boolean isPoweredTile(TileEntity tileEntity){
		return externalPowerHandlerList.stream().anyMatch(externalPowerManager -> externalPowerManager.isPoweredTile(tileEntity));
	}

	@Override
	public void handleClass(Class clazz) {
		if (isPowerManager(clazz)) {
			try {
				ExternalPowerManager powerManager = (ExternalPowerManager) clazz.newInstance();
				externalPowerHandlerList.add(powerManager);
				RebornCore.logHelper.info("Loaded power manager from: " + powerManager.getClass().getSimpleName());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Failed to register compat module", e);
			}
		}
	}

	private boolean isPowerManager(Class clazz) {
		for (Class iface : clazz.getInterfaces()) {
			if (iface == ExternalPowerManager.class) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return RebornRegistry.class;
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
