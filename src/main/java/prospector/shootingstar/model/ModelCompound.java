package prospector.shootingstar.model;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;

public class ModelCompound {
    private String inventoryVariant = "inventory";
    private String blockstatePath;
    private IProperty[] ignoreProperties = null;
    private String modid;
    private Block block = null;
    private Item item = null;
    private int meta;

    public ModelCompound(String modid, Block block, int meta, String blockstatePath, IProperty... ignoreProperties) {
        this.modid = modid;
        this.block = block;
        this.blockstatePath = blockstatePath;
        this.ignoreProperties = ignoreProperties;
        this.meta = meta;
    }

    public ModelCompound(String modid, Block block, int meta, IProperty... ignoreProperties) {
        this(modid, block, meta, "", ignoreProperties);
    }

    public ModelCompound(String modid, Block block, IProperty... ignoreProperties) {
        this(modid, block, 0, "", ignoreProperties);
    }

    public ModelCompound(String modid, Block block, String blockstatePath, IProperty... ignoreProperties) {
        this(modid, block, 0, blockstatePath, ignoreProperties);
    }

    public ModelCompound(String modid, Item item, int meta, String blockstatePath) {
        this.modid = modid;
        this.item = item;
        this.blockstatePath = blockstatePath;
        this.meta = meta;
    }

    public ModelCompound(String modid, Item item, int meta) {
        this(modid, item, meta, "");
    }

    public ModelCompound(String modid, Item item) {
        this(modid, item, 0, "");
    }

    public ModelCompound(String modid, Item item, String blockstatePath) {
        this(modid, item, 0, blockstatePath);
    }

    public String getInventoryVariant() {
        return inventoryVariant;
    }

    public ModelCompound setInvVariant(String variant) {
        inventoryVariant = variant;
        return this;
    }

    public String getModid() {
        return modid;
    }

    public Block getBlock() {
        return block;
    }

    public Item getItem() {
        if (isBlock())
            return Item.getItemFromBlock(block);
        return item;
    }

    public boolean isBlock() {
        if (block != null)
            return true;
        return false;
    }

    public boolean isItem() {
        if (item != null)
            return true;
        return false;
    }

    public String getBlockStatePath() {
        return blockstatePath;
    }

    public IProperty[] getIgnoreProperties() {
        return ignoreProperties;
    }

    public int getMeta() {
        return meta;
    }
}