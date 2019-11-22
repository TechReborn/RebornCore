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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.RebornMachineTile;

import java.util.function.Predicate;

/**
 * @author estebes
 */
public class RebornFluidTank extends FluidTank {
    public RebornFluidTank(String identifier, int capacity, TileEntity tile) {
        this(identifier, capacity, tile, fluid -> true);
    }

    public RebornFluidTank(String identifier, int capacity, TileEntity tile, Predicate<Fluid> acceptedFluids) {
        super(capacity);
        this.identifier = identifier;
        this.tile = tile;
        this.acceptedFluids = acceptedFluids;
    }

    // FluidTank >>
    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluid != null && acceptsFluid(fluid.getFluid());
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluid) {
        return fluid != null && acceptsFluid(fluid.getFluid());
    }
    // << FluidTank

    // RebornTank >>
    public boolean isEmpty() {
        return getFluid() == null || getFluid().amount <= 0;
    }

    public boolean isFull() {
        return getFluid() != null && getFluid().amount >= getCapacity();
    }

    public Fluid getFluidType() {
        return getFluid() != null ? getFluid().getFluid() : null;
    }

    public void setFluidAmount(int amount) {
        if (fluid != null) fluid.amount = amount;
    }

    public boolean acceptsFluid(Fluid fluid) {
        return acceptedFluids.test(fluid);
    }

    public boolean canFill(EnumFacing side) {
        if (tile instanceof RebornMachineTile) {
            RebornMachineTile machine = (RebornMachineTile) tile;

            if (machine.fluidConfiguration != null) {
                FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
                return fluidConfig == null ? super.canFill() : fluidConfig.getIoConfig().isInsert();
            }
        }

        return canFill();
    }

    public boolean canDrain(EnumFacing side) {
        if (tile instanceof RebornMachineTile) {
            RebornMachineTile machine = (RebornMachineTile) tile;

            if (machine.fluidConfiguration != null) {
                FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
                return fluidConfig == null ? super.canDrain() : fluidConfig.getIoConfig().isExtact();
            }
        }

        return canDrain();
    }

    public IFluidTankProperties getTankProperties(final EnumFacing side) {
        return new IFluidTankProperties() {
            @Override
            public FluidStack getContents() {
                return getFluid();
            }

            @Override
            public int getCapacity() {
                return capacity;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluidStack) {
                return fluidStack != null && fluidStack.amount > 0
                        && acceptsFluid(fluidStack.getFluid()) && (side == null || RebornFluidTank.this.canFill(side));
            }

            @Override
            public boolean canFill() {
                return RebornFluidTank.this.canFill(side);
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluidStack) {
                return fluidStack != null && fluidStack.amount > 0
                        && acceptsFluid(fluidStack.getFluid()) && (side == null || RebornFluidTank.this.canDrain(side));
            }

            @Override
            public boolean canDrain() {
                return RebornFluidTank.this.canDrain(side);
            }
        };
    }
    // << RebornTank

    // Fields >>
    protected final String identifier;
    protected final Predicate<Fluid> acceptedFluids;
    // << Fields
}
