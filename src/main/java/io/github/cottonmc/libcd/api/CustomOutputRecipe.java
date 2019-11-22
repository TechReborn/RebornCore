package io.github.cottonmc.libcd.api;

import net.minecraft.item.Item;

import java.util.Collection;

/**
 * A recipe that has output behavior that cannot be described by just the Recipe#getOutput() method.
 * Used for RecipeTweaker remove-by-output code.
 */
public interface CustomOutputRecipe {
	Collection<Item> getOutputItems();
}
