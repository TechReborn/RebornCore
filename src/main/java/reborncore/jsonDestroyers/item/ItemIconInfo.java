package reborncore.jsonDestroyers.item;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;

public class ItemIconInfo {

    Item item;
    int damage;
    TextureAtlasSprite sprite;

    public ItemIconInfo(Item item, int damage, TextureAtlasSprite sprite) {
        this.item = item;
        this.damage = damage;
        this.sprite = sprite;
    }

    public Item getItem() {
        return item;
    }

    public int getDamage() {
        return damage;
    }

    public TextureAtlasSprite getSprite() {
        return sprite;
    }
}
