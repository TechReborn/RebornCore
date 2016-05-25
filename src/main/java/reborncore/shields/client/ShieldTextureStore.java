package reborncore.shields.client;

import net.minecraft.util.ResourceLocation;
import reborncore.RebornCore;
import reborncore.shields.json.ShieldJsonFile;
import reborncore.shields.json.ShieldJsonLoader;
import reborncore.shields.json.ShieldUser;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Created by modmuss50 on 23/05/2016.
 */
public class ShieldTextureStore {

    static HashMap<String, ShieldTexture> textures = new HashMap<>();
    static HashMap<String, ShieldTexture> customTextures = new HashMap<>();

    public static void load() {
        ShieldJsonFile file = ShieldJsonLoader.shieldJsonFile;
        if (file != null) {
            for (ShieldUser user : file.userList) {
                boolean isCustomTexture = false;
                String name = user.username;
                if (!user.textureName.isEmpty()) {
                    name = user.textureName;
                    isCustomTexture = true;
                }
                if (isCustomTexture) {
                    ShieldTexture texture;
                    if (customTextures.containsKey(name)) {
                        texture = customTextures.get(name);
                    } else {
                        texture = new ShieldTexture(RebornCore.WEB_URL + "reborncore/textures/" + name + ".png");
                        customTextures.put(name, texture);
                    }
                    textures.put(user.username, texture);
                } else {
                    textures.put(user.username, new ShieldTexture(RebornCore.WEB_URL + "reborncore/textures/" + user.username + ".png"));
                }
            }
        }
    }


    public static
    @Nullable
    ShieldTexture getTexture(ResourceLocation location) {
        if (!textures.isEmpty()) {
            String name = location.getResourcePath();
            ShieldTexture texture = null;
            if (ShieldJsonLoader.customTextureNameList.containsKey(name)) {
                name = ShieldJsonLoader.customTextureNameList.get(name);
                if (customTextures.containsKey(name)) {
                    texture = customTextures.get(name);
                }
            } else {
                if (textures.containsKey(name)) {
                    texture = textures.get(name);
                }
            }
            if (texture != null && texture.getState() == DownloadState.AVAILABLE) {
                texture.download();
            }
            return texture;

        }
        return null;
    }


}
