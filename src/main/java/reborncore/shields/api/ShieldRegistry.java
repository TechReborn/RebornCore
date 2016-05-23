package reborncore.shields.api;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 21/03/2016.
 */
public class ShieldRegistry {
    public static List<Shield> shieldList = new ArrayList<>();
    public static HashMap<String, Shield> shieldHashMap = new HashMap<>();
    public static HashMap<Shield, ResourceLocation> shieldTextureHashMap = new HashMap<>();

    public static void registerShield(Shield shield) {
        shieldList.add(shield);
        shieldHashMap.put(shield.name, shield);
        shieldTextureHashMap.put(shield, shield.getShieldTexture());
    }

}
