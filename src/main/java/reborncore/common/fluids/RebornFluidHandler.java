/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.fluids;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import reborncore.common.tile.RebornMachineTile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author estebes
 */
public class RebornFluidHandler implements IFluidHandler {
    public RebornFluidHandler(RebornMachineTile tile, EnumFacing side) {
        this.tile = tile;
        this.side = side;
    }

    // IFluidHandler >>
    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<IFluidTankProperties> props = new ArrayList<>(1);

        RebornFluidTank tank = tile.getTank();
        if (tank != null) {
            if (tank.canFill(side) || tank.canDrain(side))
                props.add(tank.getTankProperties(side));
        }

        return props.toArray(new IFluidTankProperties[0]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) return 0;

        int total = 0;
        FluidStack missing = resource.copy();
        RebornFluidTank tank = tile.getTank();
        if (tank != null) {
            if (tank.canFill(side)) {
                total += tank.fill(missing, doFill);
                missing.amount = resource.amount - total;
            }
        }

        return total;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) return null;

        FluidStack ret = new FluidStack(resource.getFluid(), 0);
        RebornFluidTank tank = tile.getTank();
        if (tank != null) {
            if (tank.canDrain(side)) {
                FluidStack inTank = tank.getFluid();
                if (inTank != null && inTank.getFluid() == resource.getFluid()) {
                    FluidStack add = tank.drain(resource.amount - ret.amount, doDrain);
                    if (add != null) ret.amount += add.amount;
                }
            }
        }

        return ret.amount == 0 ? null : ret;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        RebornFluidTank tank = tile.getTank();
        if (tank != null) {
            if (!tank.canDrain(side)) return null;

            FluidStack stack = tank.drain(maxDrain, false);
            if (stack != null) {
                stack.amount = maxDrain;
                return drain(stack, doDrain);
            }
        }

        return null;
    }
    // << IFluidHandler

    // Fields >>
    private final RebornMachineTile tile;
    private final EnumFacing side;
    // << Fields
}