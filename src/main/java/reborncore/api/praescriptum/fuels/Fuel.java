package reborncore.api.praescriptum.fuels;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;

import reborncore.api.praescriptum.Utils.LogUtils;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class Fuel {
    public Fuel(FuelHandler handler) {
        this.handler = handler;
    }

    public Fuel withInput(List<InputIngredient<?>> inputs) {
        inputIngredients.addAll(inputs);
        return this;
    }

    public Fuel withInput(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(ItemStackInputIngredient.of(itemStack));
        return this;
    }

    public Fuel withInput(String oreDict) {
        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict));
        return this;
    }

    public Fuel withInput(String oreDict, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount));
        return this;
    }

    public Fuel withInput(String oreDict, int amount, Integer meta) {
        if (amount <= 0) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(OreDictionaryInputIngredient.of(oreDict, amount, meta));
        return this;
    }

    public Fuel withInput(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) throw new IllegalArgumentException("Input cannot be empty");

        inputIngredients.add(FluidStackInputIngredient.of(fluidStack));
        return this;
    }

    public Fuel withOutput(double energyOutput) {
        this.energyOutput = energyOutput;
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
        if (!success) LogUtils.LOGGER.warn("Registration failed for input " + this);
    }

    // Getters >>
    public List<InputIngredient<?>> getInputIngredients() {
        return inputIngredients;
    }

    public double getEnergyOutput() {
        return energyOutput;
    }

    public NBTTagCompound getMetadata() {
        return metadata;
    }
    // << Getters

    // Fields >>
    private final FuelHandler handler;

    private List<InputIngredient<?>> inputIngredients = new ArrayList<>();
    private double energyOutput = 0.0D;
    private NBTTagCompound metadata;
    // << Fields
}
