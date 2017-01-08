package reborncore.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * Created by modmuss50 on 08/01/2017.
 */
public class RecipeTranslator {

	public static @Nullable
	ItemStack getStackFromObject(Object object){
		if(object instanceof ItemStack){
			return (ItemStack) object;
		} else if (object instanceof String){
			String oreName = (String) object;
			if(OreDictionary.doesOreNameExist(oreName)){
				NonNullList<ItemStack> list = OreDictionary.getOres(oreName);
				return list.get(0).copy(); //The first entry
			}

		}
		return null;
	}

}
