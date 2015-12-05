package reborncore.jsonDestroyers.item;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;

public class ItemIconInfo {

    Item item;
    int damage;
    TextureAtlasSprite sprite;
    String textureName;

    public Item getItem() {
        return item;
    }

    public int getDamage() {
        return damage;
    }

    public TextureAtlasSprite getSprite() {
        return sprite;
    }

    public ItemIconInfo(Item item, int damage, TextureAtlasSprite sprite, String textureName) {
        this.item = item;
        this.damage = damage;
        this.sprite = sprite;
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
    }
}
