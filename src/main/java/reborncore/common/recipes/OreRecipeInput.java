package reborncore.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OreRecipeInput implements IRecipeInput {

	String oreDictName;
	int size = 1;

	public OreRecipeInput(String oreDictName, int size) {
		this.oreDictName = oreDictName;
		this.size = size;
		Validate.notEmpty(getAllStacks());
	}

	public OreRecipeInput(String oreDictName) {
		this.oreDictName = oreDictName;
		Validate.notEmpty(getAllStacks());
	}

	@Override
	public ItemStack getItemStack() {
		List<ItemStack> list = getAllStacks();
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return list.get(0);
	}

	@Override
	public List<ItemStack> getAllStacks() {
		if (!OreDictionary.doesOreNameExist(oreDictName)) {
			return NonNullList.create();
		}
		NonNullList<ItemStack> list = OreDictionary.getOres(oreDictName);
		return list.stream()
			.filter(Objects::nonNull)
			.map(stack -> {
				ItemStack clone = stack.copy();
				clone.setCount(size);
				return clone;
			})
			.filter(stack -> !stack.isEmpty())
			.collect(Collectors.toList());
	}
}
