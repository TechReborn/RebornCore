package reborncore.jsonDestroyers.block;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import reborncore.RebornCore;


public class CustomTexture extends TextureAtlasSprite {
    public CustomTexture(String spriteName) {
        super(spriteName);
    }

    public static String getDerivedName(String s2) {
        String s1 = "blocks";

        int ind = s2.indexOf(58);

        if (ind >= 0) {
            if (ind > 1) {
                s1 = s2.substring(0, ind);
            }

            s2 = s2.substring(ind + 1, s2.length());
        }

        s1 = s1.toLowerCase();

        return RebornCore.MOD_ID + ":" + s1 + "/" + s2;
    }
}
