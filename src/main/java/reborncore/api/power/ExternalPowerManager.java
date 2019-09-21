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

package reborncore.api.power;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;

public interface ExternalPowerManager {

	public ExternalPowerHandler createPowerHandler(PowerAcceptorBlockEntity acceptor);

	public boolean isPoweredItem(ItemStack stack);

	public boolean isPowered(BlockEntity blockEntity, Direction side);

	public void dischargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack);

	public void chargeItem(PowerAcceptorBlockEntity blockEntityPowerAcceptor, ItemStack stack);

	public void chargeItem(ItemStack powerAcceptor, ItemStack stack);

	/**
	 * Requests that the specified power acceptor be charged from the armor slots in an entity's inventory.
	 *
	 * @param powerAcceptor The item requesting to be charged
	 * @param entity The entity whose inventory contains the power acceptor
	 */
	default void requestEnergyFromArmor(ItemStack powerAcceptor, LivingEntity entity) { }
}
