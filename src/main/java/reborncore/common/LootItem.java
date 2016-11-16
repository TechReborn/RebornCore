package reborncore.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class LootItem
{
    ItemStack item;
    double chance;
    int minSize;
    int maxSize;
    ResourceLocation lootTableList;

    public LootItem(ItemStack item, double chance, ResourceLocation lootTableList)
    {
        this(item, chance, 1, 1, lootTableList);
    }

    public LootItem(ItemStack item, double chance, int minSize, int maxSize, ResourceLocation lootTableList)
    {
        this.item = item;
        this.chance = chance;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.lootTableList = lootTableList;
    }

    public ItemStack createStack(Random rnd)
    {
        int size = minSize;
        if (maxSize > minSize)
        {
            size += rnd.nextInt(maxSize - minSize + 1);
        }

        ItemStack result = item.copy();
	    result.func_190920_e(size);
        return result;
    }

    public ResourceLocation getLootTableList()
    {
        return this.lootTableList;
    }
}
