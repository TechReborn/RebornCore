package reborncore.shields.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.util.ResourceLocation;
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

    public static void load(){
        ShieldJsonFile file = ShieldJsonLoader.shieldJsonFile;
        if(file != null){
            for(ShieldUser user : file.userList){
                textures.put(user.username, new ShieldTexture("http://modmuss50.me/reborncore/textures/" + user.username + ".png"));
            }
        }
    }


    public static @Nullable ShieldTexture getTexture(ResourceLocation location){
        if(!textures.isEmpty()){
            if(textures.containsKey(location.getResourcePath())){
                ShieldTexture texture = textures.get(location.getResourcePath());
                if(texture.getState() == DownloadState.AVAILABLE){
                    texture.download();
                }
                return texture;
            }
        }
        return null;
    }


}
