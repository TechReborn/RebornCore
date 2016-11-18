package reborncore.shields;

import net.minecraft.util.ResourceLocation;
import reborncore.shields.api.Shield;

/**
 * Created by Mark on 21/03/2016.
 */
public class FaceShield extends Shield {
	public FaceShield(String name) {
		super(name);
	}

	@Override
	public ResourceLocation getShieldTexture() {
		return new ResourceLocation("reborncore:textures/shields/people/" + name + ".png");
	}

	@Override
	public boolean showInItemLists() {
		return false;
	}
}
