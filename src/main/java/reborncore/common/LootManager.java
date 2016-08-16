package reborncore.common;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class LootManager
{
    public static LootManager INSTANCE = new LootManager();

    private LootManager(){}

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent evt)
    {
        LootTable table = evt.getTable();

        InnerPool lp = new InnerPool();

        if (evt.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH))
        {
//            lp.addItem(createLootEntry(Items.NETHER_STAR, 0.95));
        }
        if (!lp.isEmpty())
        {
            table.addPool(lp);
        }
    }

    private LootItem createLootEntry(Item item, double chance)
    {
        return new LootItem(new ItemStack(item), chance, 1, 1);
    }

    private LootItem createLootEntry(Item item, int minSize, int maxSize, double chance)
    {
        return new LootItem(new ItemStack(item), chance, minSize, maxSize);
    }

    private LootItem createLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, double chance)
    {
        return new LootItem(new ItemStack(item, 1, ordinal), chance, minStackSize, maxStackSize);
    }

    private static class LootItem
    {
        ItemStack item;
        double chance;
        int minSize;
        int maxSize;

        public LootItem(ItemStack item, double chance)
        {
            this(item, chance, 1, 1);
        }

        public LootItem(ItemStack item, double chance, int minSize, int maxSize)
        {
            this.item = item;
            this.chance = chance;
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public ItemStack createStack(Random rnd)
        {
            int size = minSize;
            if (maxSize > minSize)
            {
                size += rnd.nextInt(maxSize - minSize + 1);
            }

            ItemStack result = item.copy();
            result.stackSize = size;
            return result;
        }
    }

    private static class InnerPool extends LootPool
    {
        private final List<LootItem> items = new ArrayList<LootItem>();

        public InnerPool()
        {
            super(new LootEntry[0], new LootCondition[0], new RandomValueRange(0, 0), new RandomValueRange(0, 0), RebornCore.MOD_ID);
        }

        public boolean isEmpty()
        {
            return items.isEmpty();
        }

        public void addItem(LootItem entry)
        {
            if (entry != null)
            {
                items.add(entry);
            }
        }

        @Override
        public void generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context)
        {
            for (LootItem entry : items)
            {
                if (rand.nextDouble() < entry.chance)
                {
                    ItemStack stack = entry.createStack(rand);
                    if (stack != null)
                    {
                       System.out.println("LootManager.InnerPool.generateLoot: Added " + stack.getDisplayName() + " " + stack);
                        stacks.add(stack);
                    }
                }
            }
        }
    }
}
