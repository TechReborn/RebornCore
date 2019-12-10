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


//package reborncore.api.praescriptum.recipes;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//
//import net.minecraftforge.fluids.FluidStack;
//
//import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
//import reborncore.common.util.ItemUtils;
//
//import java.util.Arrays;
//
///**
// * @author estebes
// */
//public class Recipe2dot0 {
//    public Recipe2dot0(Recipe2dot0Handler handler) {
//        this.handler = handler;
//    }
//
//    public Recipe2dot0 withInput(ItemStack itemStack) {
//        if (ItemUtils.isEmpty(itemStack)) throw new IllegalArgumentException("Input cannot be empty");
//
//        itemInputs = Arrays.copyOf(itemInputs, itemInputs.length + 1);
//        itemInputs[itemInputs.length - 1] = itemStack;
//        return this;
//    }
//
//    public Recipe2dot0 withInput(String oreDict) {
//        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict));
//        return this;
//    }
//
//    public Recipe2dot0 withInput(String oreDict, int amount) {
//        if (amount <= 0) throw new IllegalArgumentException("Input cannot be empty");
//
//        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount));
//        return this;
//    }
//
//    public Recipe2dot0 withInput(String oreDict, int amount, Integer meta) {
//        if (amount <= 0) throw new IllegalArgumentException("Input cannot be empty");
//
//        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount, meta));
//        return this;
//    }
//
//    public Recipe2dot0 withInput(FluidStack fluidStack) {
//        if (fluidStack.amount <= 0) throw new IllegalArgumentException("Input cannot be empty");
//
//        fluidInputs = Arrays.copyOf(fluidInputs, fluidInputs.length + 1);
//        fluidInputs[fluidInputs.length - 1] = fluidStack;
//        return this;
//    }
//
//
//    // Outputs >>
//    public Recipe2dot0 withOutput(ItemStack itemStack) {
//        if (ItemUtils.isEmpty(itemStack)) throw new IllegalArgumentException("Output cannot be empty");
//
//        itemOutputs = Arrays.copyOf(itemOutputs, itemOutputs.length + 1);
//        itemOutputs[itemOutputs.length - 1] = itemStack;
//        return this;
//    }
//
//    public Recipe2dot0 withOutput(FluidStack fluidStack) {
//        if (fluidStack.amount <= 0) throw new IllegalArgumentException("Output cannot be empty");
//
//        fluidOutputs = Arrays.copyOf(fluidOutputs, fluidOutputs.length + 1);
//        fluidOutputs[fluidOutputs.length - 1] = fluidStack;
//        return this;
//    }
//    // << Outputs
//
//    // Extra information >>
//    public Recipe2dot0 withEnergyCostPerTick(int energyCostPerTick) {
//        if (energyCostPerTick < 0) throw new IllegalArgumentException("Energy cost per tick cannot be less than 0");
//
//        if (metadata == null) metadata = new NBTTagCompound();
//        metadata.setInteger("energyCostPerTick", energyCostPerTick);
//        return this;
//    }
//
//    public Recipe2dot0 withOperationDuration(int operationDuration) {
//        if (operationDuration < 0) throw new IllegalArgumentException("Operation duration cannot be less than 0");
//
//        if (metadata == null) metadata = new NBTTagCompound();
//        metadata.setInteger("operationDuration", operationDuration);
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(NBTTagCompound metadata) {
//        this.metadata = metadata;
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(String key, int value) {
//        if (metadata == null) metadata = new NBTTagCompound();
//
//        metadata.setInteger(key, value);
//
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(String key, short value) {
//        if (metadata == null) metadata = new NBTTagCompound();
//
//        metadata.setShort(key, value);
//
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(String key, byte value) {
//        if (metadata == null) metadata = new NBTTagCompound();
//
//        metadata.setByte(key, value);
//
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(String key, float value) {
//        if (metadata == null) metadata = new NBTTagCompound();
//
//        metadata.setFloat(key, value);
//
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(String key, double value) {
//        if (metadata == null) metadata = new NBTTagCompound();
//
//        metadata.setDouble(key, value);
//
//        return this;
//    }
//
//    public Recipe2dot0 withMetadata(String key, boolean value) {
//        if (metadata == null) metadata = new NBTTagCompound();
//
//        metadata.setBoolean(key, value);
//
//        return this;
//    }
//    // << Extra information
//
//    public void register() {
//        register(false);
//    }
//
//    public void register(boolean replace) {
//        boolean success = handler.addRecipe(this, replace);
//        if (!success) handler.logger.warn("Registration failed for input " + this);
//    }
//
//    // For internal use >>
//    protected Recipe2dot0 withInput(ItemStack[] itemInputs) {
//        this.itemInputs = itemInputs;
//        return this;
//    }
//
//    protected Recipe2dot0 withInput(FluidStack[] fluidInputs) {
//        this.fluidInputs = fluidInputs;
//        return this;
//    }
//
//    protected Recipe2dot0 withInput(ItemStack[] itemInputs, FluidStack[] fluidInputs) {
//        this.itemInputs = itemInputs;
//        this.fluidOutputs = fluidInputs;
//        return this;
//    }
//
//    protected Recipe2dot0 withOutput(ItemStack[] itemOutputs) {
//        this.itemOutputs = itemOutputs;
//        return this;
//    }
//
//    protected Recipe2dot0 withOutput(FluidStack[] fluidOutputs) {
//        this.fluidOutputs = fluidOutputs;
//        return this;
//    }
//
//    protected Recipe2dot0 withOutput(ItemStack[] itemOutputs, FluidStack[] fluidOutputs) {
//        this.itemOutputs = itemOutputs;
//        this.fluidOutputs = fluidOutputs;
//        return this;
//    }
//    // << For internal use
//
//    // Getters >>
//    public RecipeHandler getHandler() {
//        return handler;
//    }
//
//    public ItemStack[] getItemInputs() {
//        return itemInputs;
//    }
//
//    public FluidStack[] getFluidInputs() {
//        return fluidInputs;
//    }
//
//    public ItemStack[] getItemOutputs() {
//        return itemOutputs;
//    }
//
//    public FluidStack[] getFluidOutputs() {
//        return fluidOutputs;
//    }
//
//    public int getEnergyCostPerTick() {
//        if (metadata == null) return 0;
//
//        return metadata.hasKey("energyCostPerTick") ? metadata.getInteger("energyCostPerTick") : 0;
//    }
//
//    public int getOperationDuration() {
//        if (metadata == null) return 0;
//
//        return metadata.hasKey("operationDuration") ? metadata.getInteger("operationDuration") : 0;
//    }
//
//    public NBTTagCompound getMetadata() {
//        return metadata;
//    }
//    // << Getters
//
//    // Fields >>
//    private final Recipe2dot0Handler handler;
//
//    // Inputs
//    private ItemStack[] itemInputs = new ItemStack[0];
//    private FluidStack[] fluidInputs = new FluidStack[0];
//
//    // Outputs
//    private ItemStack[] itemOutputs = new ItemStack[0];
//    private FluidStack[] fluidOutputs = new FluidStack[0];
//
//    // Extra information
//    private NBTTagCompound metadata;
//    // << Fields
//}
