package reborncore.shields.api;

import net.minecraft.util.ResourceLocation;

/**
 * Created by Mark on 21/03/2016.
 */
public abstract class Shield
{

	public String name;

	public Shield(String name)
	{
		this.name = name;
	}

	public ResourceLocation getShieldTexture()
	{
		return new ResourceLocation("null");
	}

	public boolean showInItemLists()
	{
		return true;
	}

}
