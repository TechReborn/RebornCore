package reborncore.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.RebornCore;
import reborncore.RebornRegistry;

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

        InnerPool lp = RebornRegistry.lp;

        if (!lp.isEmpty())
        {
            table.addPool(lp);
        }
    }

    public static LootItem createLootEntry(Item item, double chance, ResourceLocation loottablelist)
    {
        return new LootItem(new ItemStack(item), chance, 1, 1, loottablelist);
    }

    public static LootItem createLootEntry(Item item, int minSize, int maxSize, double chance, ResourceLocation loottablelist)
    {
        return new LootItem(new ItemStack(item), chance, minSize, maxSize, loottablelist);
    }

    public static LootItem createLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, double chance, ResourceLocation loottablelist)
    {
        return new LootItem(new ItemStack(item, 1, ordinal), chance, minStackSize, maxStackSize, loottablelist);
    }

    public static class InnerPool extends LootPool
    {
        public final List<LootItem> items = new ArrayList<LootItem>();

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
//                       System.out.println("LootManager.InnerPool.generateLoot: Added " + stack.getDisplayName() + " " + stack);
                        stacks.add(stack);
                    }
                }
            }
        }
    }
}
