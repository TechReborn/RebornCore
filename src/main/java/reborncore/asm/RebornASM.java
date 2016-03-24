package reborncore.asm;

import java.util.Map;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.apache.logging.log4j.Level;

/**
 * Created by Mark on 21/03/2016.
 */
// -Dfml.coreMods.load=reborncore.asm.RebornASM
@IFMLLoadingPlugin.Name(value = "Reborn Core ASM")
public class RebornASM implements IFMLLoadingPlugin, IFMLCallHook
{

	public RebornASM()
	{
		FMLLog.log("RebornCore", Level.INFO, String.valueOf("Loading RebornCore ASM"));
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { RebornClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{

	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

	@Override
	public Void call() throws Exception
	{
		return null;
	}
}
