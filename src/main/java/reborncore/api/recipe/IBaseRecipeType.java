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

package reborncore.api.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/**
 * This is the base recipe class implement this to make a recipe handler
 */
public interface IBaseRecipeType {

	/**
	 * Use this to get all of the inputs
	 *
	 * @return the List of inputs
	 */
	public List<Object> getInputs();

	/**
	 * This gets the output form the array list
	 *
	 * @param i get output form position in arraylist
	 * @return the output
	 */
	public ItemStack getOutput(int i);

	/**
	 * @return The ammount of outputs
	 */
	public int getOutputsSize();

	/**
	 * @return get outputs
	 */
	public List<ItemStack> getOutputs();

	/**
	 * This is the name to check that the recipe is the one that should be used
	 * in the tile entity that is set up to behavior this recipe.
	 *
	 * @return The recipeName
	 */
	public String getRecipeName();

	/**
	 * This should be a user friendly name
	 *
	 * @return The user friendly name of the recipe.
	 */
	public String getUserFreindlyName();

	/**
	 * This is how long the recipe needs to tick for the crafting operation to
	 * complete
	 *
	 * @return tick length
	 */
	public int tickTime();

	/**
	 * This is how much eu Per tick the machine should use
	 *
	 * @return the amount of eu to be used per tick.
	 */
	public int euPerTick();

	/**
	 * @param tile the tile that is doing the crafting
	 * @return if true the recipe will craft, if false it will not
	 */
	public boolean canCraft(TileEntity tile);

	/**
	 * @param tile the tile that is doing the crafting
	 * @return return true if fluid was taken and should craft
	 */
	public boolean onCraft(TileEntity tile);

	public Object clone() throws CloneNotSupportedException;

	public boolean useOreDic();
}
