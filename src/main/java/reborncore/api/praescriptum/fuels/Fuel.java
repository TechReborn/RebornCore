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

package reborncore.api.praescriptum.fuels;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author estebes
 */
public class Fuel {
    public Fuel(FuelHandler handler) {
        this.handler = handler;
    }

    public Fuel addSources(Collection<InputIngredient<?>> inputs) {
        inputIngredients.addAll(inputs);
        return this;
    }

    public Fuel addItemSource(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) throw new IllegalArgumentException("Source cannot be empty");

        inputIngredients.add(ItemStackInputIngredient.of(itemStack));
        return this;
    }

    public Fuel addItemSource(String oreDict) {
        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict));
        return this;
    }

    public Fuel addItemSource(String oreDict, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Source cannot be empty");

        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount));
        return this;
    }

    public Fuel addItemSource(String oreDict, int amount, Integer meta) {
        if (amount <= 0) throw new IllegalArgumentException("Source cannot be empty");

        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount, meta));
        return this;
    }

    public Fuel addFluidSource(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) throw new IllegalArgumentException("Source cannot be empty");

        inputIngredients.add(FluidStackInputIngredient.of(fluidStack));
        return this;
    }

    public Fuel addFluidSource(Fluid fluid) {
        return addFluidSource(fluid, 1);
    }

    public Fuel addFluidSource(Fluid fluid, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Source cannot be empty");

        inputIngredients.add(FluidStackInputIngredient.of(new FluidStack(fluid, amount)));
        return this;
    }

    public Fuel withEnergyOutput(double energyOutput) {
        if (energyOutput < 0) throw new IllegalArgumentException("Energy output cannot be less than 0.0D");

        this.energyOutput = energyOutput;
        return this;
    }

    public Fuel withEnergyPerTick(double energyPerTick) {
        if (energyPerTick < 0) throw new IllegalArgumentException("Energy per tick cannot be less than 0.0D");

        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setDouble("energyPerTick", energyPerTick);
        return this;
    }

    public Fuel withMetadata(NBTTagCompound metadata) {
        this.metadata = metadata;
        return this;
    }

    public Fuel withMetadata(String key, int value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setInteger(key, value);
        return this;
    }

    public Fuel withMetadata(String key, short value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setShort(key, value);
        return this;
    }

    public Fuel withMetadata(String key, byte value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setByte(key, value);
        return this;
    }

    public Fuel withMetadata(String key, float value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setFloat(key, value);
        return this;
    }

    public Fuel withMetadata(String key, double value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setDouble(key, value);
        return this;
    }

    public Fuel withMetadata(String key, boolean value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setBoolean(key, value);
        return this;
    }

    public void register() {
        register(false);
    }

    public void register(boolean replace) {
        boolean success = handler.addFuel(this, replace);
        if (!success) handler.logger.warn("Registration failed for input " + this);
    }

    // Getters >>
    public List<InputIngredient<?>> getInputIngredients() {
        return inputIngredients;
    }

    public double getEnergyOutput() {
        return energyOutput;
    }

    public double getEnergyPerTick() {
        if (metadata == null) return 0.0D;

        return metadata.hasKey("energyPerTick") ? metadata.getDouble("energyPerTick") : 0.0D;
    }

    public NBTTagCompound getMetadata() {
        return metadata;
    }
    // << Getters

    // Helpers >>
    public static Fuel readFromNBT(FuelHandler fuelHandler, NBTTagCompound tag) {
        Fuel ret = fuelHandler.addFuel();

        NBTTagList inputIngredients = tag.getTagList("inputIngredients", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < inputIngredients.tagCount(); index++) {
            NBTTagCompound inputIngredient = inputIngredients.getCompoundTagAt(index);
            byte type = inputIngredient.getByte("type");
            switch (type) {
                case 0:
                    ItemStack itemStack = new ItemStack(inputIngredient);
                    inputIngredient.getBoolean("consumable");
                    ret.addItemSource(itemStack);
                    break;
                case 1:
                    String oreDictionaryEntry = inputIngredient.getString("oreDictionaryEntry");
                    int amount = inputIngredient.getInteger("amount");
                    int meta = inputIngredient.getInteger("meta");
                    inputIngredient.getBoolean("consumable");
                    ret.addItemSource(oreDictionaryEntry, amount);
                    break;
                case 2:
                    FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(inputIngredient);
                    inputIngredient.getBoolean("consumable");
                    ret.addFluidSource(Objects.requireNonNull(fluidStack));
                    break;
            }
        }

        ret.withEnergyOutput(tag.getDouble("energyOutput"));

        if (tag.hasKey("metadata"))
            ret.withMetadata(tag.getCompoundTag("metadata"));

        return ret;
    }

    public static NBTTagCompound writeToNBT(Fuel fuel) {
        NBTTagCompound ret = new NBTTagCompound();

        NBTTagList inputIngredients = new NBTTagList();
        for (InputIngredient<?> inputIngredient : fuel.inputIngredients) {
            if (inputIngredient instanceof ItemStackInputIngredient) {
                NBTTagCompound temp = new NBTTagCompound();
                temp.setByte("type", (byte) 0);
                ((ItemStackInputIngredient) inputIngredient).ingredient.writeToNBT(temp);
                temp.setBoolean("consumable", inputIngredient.consumable);
                inputIngredients.appendTag(temp);
            } else if (inputIngredient instanceof OreDictionaryInputIngredient) {
                NBTTagCompound temp = new NBTTagCompound();
                temp.setByte("type", (byte) 1);
                temp.setString("oreDictionaryEntry", ((OreDictionaryInputIngredient) inputIngredient).ingredient);
                temp.setInteger("amount", ((OreDictionaryInputIngredient) inputIngredient).amount);
                temp.setInteger("meta", ((OreDictionaryInputIngredient) inputIngredient).meta);
                temp.setBoolean("consumable", inputIngredient.consumable);
                inputIngredients.appendTag(temp);
            } else if (inputIngredient instanceof FluidStackInputIngredient) {
                NBTTagCompound temp = new NBTTagCompound();
                temp.setByte("type", (byte) 2);
                ((FluidStackInputIngredient) inputIngredient).ingredient.writeToNBT(temp);
                temp.setBoolean("consumable", inputIngredient.consumable);
                inputIngredients.appendTag(temp);
            }
        }
        ret.setTag("inputIngredients", inputIngredients);

        ret.setDouble("energyOutput", fuel.energyOutput);

        if (fuel.metadata.isEmpty())
            ret.setTag("metadata", fuel.metadata);

        return ret;
    }
    // << Helpers

    // Fields >>
    private FuelHandler handler;

    private List<InputIngredient<?>> inputIngredients = new ArrayList<>();
    private double energyOutput = 0.0D;
    private NBTTagCompound metadata;
    // << Fields
}
