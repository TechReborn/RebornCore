package reborncore.shields.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.init.Items;

/**
 * Created by Mark on 22/03/2016.
 */
@JEIPlugin
public class JeiShield extends BlankModPlugin
{

	@Override
	public void register(@Nonnull IModRegistry registry)
	{
		super.register(registry);
		registry.getJeiHelpers().getNbtIgnoreList().ignoreNbtTagNames(Items.shield, "type", "vanilla");
	}
}
