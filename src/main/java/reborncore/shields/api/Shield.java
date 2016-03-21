package reborncore.shields.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
/**
 * Created by Mark on 21/03/2016.
 */
public abstract class Shield {

    public String name;

    public Shield(String name) {
        this.name = name;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getShieldTexture(){
        return new ResourceLocation("null");
    }

}
