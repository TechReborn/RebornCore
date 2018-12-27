/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
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

package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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

	public static boolean isPoweredTile(TileEntity tileEntity, EnumFacing facing) {
		return externalPowerHandlerList.stream().anyMatch(externalPowerManager -> externalPowerManager.isPoweredTile(tileEntity, facing));
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
