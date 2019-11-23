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

package reborncore.api.praescriptum.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;

import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.api.praescriptum.ingredients.output.FluidStackOutputIngredient;
import reborncore.api.praescriptum.ingredients.output.ItemStackOutputIngredient;
import reborncore.api.praescriptum.ingredients.output.OutputIngredient;
import reborncore.common.util.ItemUtils;

import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author estebes
 */
public class Recipe implements Serializable {
    public Recipe(RecipeHandler handler) {
        this.handler = handler;
    }

    public Recipe withInput(Collection<InputIngredient<?>> inputs) {
        inputIngredients.addAll(inputs);
        return this;
    }

    public Recipe withInput(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(ItemStackInputIngredient.of(itemStack));
        return this;
    }

    public Recipe withInput(String oreDict) {
        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict));
        return this;
    }

    public Recipe withInput(String oreDict, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount));
        return this;
    }

    public Recipe withInput(String oreDict, int amount, Integer meta) {
        if (amount <= 0) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount, meta));
        return this;
    }

    public Recipe withInput(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(FluidStackInputIngredient.of(fluidStack));
        return this;
    }

    public Recipe withOutput(Collection<OutputIngredient<?>> outputs) {
        outputIngredients.addAll(outputs);
        return this;
    }

    public Recipe withOutput(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) throw new IllegalArgumentException("Output cannot be empty");

        outputIngredients.add(ItemStackOutputIngredient.of(itemStack));
        return this;
    }

    public Recipe withOutput(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) throw new IllegalArgumentException("Output cannot be empty");

        outputIngredients.add(FluidStackOutputIngredient.of(fluidStack));
        return this;
    }

    public Recipe withEnergyCostPerTick(int energyCostPerTick) {
        if (energyCostPerTick < 0) throw new IllegalArgumentException("Energy cost per tick cannot be less than 0");

        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setInteger("energyCostPerTick", energyCostPerTick);

        return this;
    }

    public Recipe withOperationDuration(int operationDuration) {
        if (operationDuration < 0) throw new IllegalArgumentException("Operation duration cannot be less than 0");

        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setInteger("operationDuration", operationDuration);

        return this;
    }

    public Recipe withMetadata(NBTTagCompound metadata) {
        this.metadata = metadata;
        return this;
    }

    public Recipe withMetadata(String key, int value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setInteger(key, value);

        return this;
    }

    public Recipe withMetadata(String key, short value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setShort(key, value);

        return this;
    }

    public Recipe withMetadata(String key, byte value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setByte(key, value);

        return this;
    }

    public Recipe withMetadata(String key, float value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setFloat(key, value);

        return this;
    }

    public Recipe withMetadata(String key, double value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setDouble(key, value);

        return this;
    }

    public Recipe withMetadata(String key, boolean value) {
        if (metadata == null) metadata = new NBTTagCompound();

        metadata.setBoolean(key, value);

        return this;
    }

    public void register() {
        register(false);
    }

    public void register(boolean replace) {
        boolean success = handler.addRecipe(this, replace);
        if (!success) handler.logger.warn("Registration failed for input " + this);
    }

    // Getters >>
    public List<InputIngredient<?>> getInputIngredients() {
        return inputIngredients;
    }

    public List<OutputIngredient<?>> getOutputIngredients() {
        return outputIngredients;
    }

    public int getEnergyCostPerTick() {
        if (metadata == null) return 0;

        return metadata.hasKey("energyCostPerTick") ? metadata.getInteger("energyCostPerTick") : 0;
    }

    public int getOperationDuration() {
        if (metadata == null) return 0;

        return metadata.hasKey("operationDuration") ? metadata.getInteger("operationDuration") : 0;
    }

    public NBTTagCompound getMetadata() {
        return metadata;
    }
    // << Getters

    // Fields >>
    private final RecipeHandler handler;

    private List<InputIngredient<?>> inputIngredients = new ArrayList<>();
    private List<OutputIngredient<?>> outputIngredients = new ArrayList<>();
    private NBTTagCompound metadata;
    // << Fields
}