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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.RebornCore;
import reborncore.api.power.ExternalPowerManager;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RebornRegister;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class ExternalPowerSystems implements IRegistryFactory {

	public static List<ExternalPowerManager> externalPowerHandlerList = new ArrayList<>();

	public static boolean isPoweredItem(ItemStack stack) {
		return externalPowerHandlerList.stream().anyMatch(externalPowerManager -> externalPowerManager.isPoweredItem(stack));
	}

	public static void dischargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {
		externalPowerHandlerList.stream()
			.filter(externalPowerManager -> externalPowerManager.isPoweredItem(stack))
			.forEach(externalPowerManager -> externalPowerManager.dischargeItem(blockEntityPowerAcceptor, stack));
	}

	public static void chargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack) {
		externalPowerHandlerList.stream()
			.filter(externalPowerManager -> externalPowerManager.isPoweredItem(stack))
			.forEach(externalPowerManager -> externalPowerManager.chargeItem(blockEntityPowerAcceptor, stack));
	}

	public static boolean isPowered(BlockEntity blockEntity, Direction facing) {
		return externalPowerHandlerList.stream().anyMatch(externalPowerManager -> externalPowerManager.isPowered(blockEntity, facing));
	}

	public static void requestEnergyFromArmor(ItemPowerManager capEnergy, LivingEntity entityLiving) {

	}

	@Override
	public void handleClass(String modId, Class clazz) {
		if (isPowerManager(clazz)) {
			try {
				ExternalPowerManager powerManager = (ExternalPowerManager) clazz.newInstance();
				externalPowerHandlerList.add(powerManager);
				RebornCore.LOGGER.info("Loaded power manager from: " + powerManager.getClass().getSimpleName());
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
		return RebornRegister.class;
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
