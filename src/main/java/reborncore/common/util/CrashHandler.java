package reborncore.common.util;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Loader;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by modmuss50 on 01/02/2017.
 */
public class CrashHandler implements ICrashCallable {
	@Override
	public String getLabel() {
		return "RebornCore";
	}

	@Override
	public String call() throws Exception {
		StringBuilder builder = new StringBuilder();
		for(String str : getInfo()){
			builder.append("\n" +"\t\t" + str);
		}
		return builder.toString();
	}

	public List<String> getInfo(){
		List<String> str = new ArrayList<>();
		str.add("RenderEngine: " +  (FMLClientHandler.instance().hasOptifine()? "1" : "0"));
		str.add("PluginEngine: " +  (Loader.isModLoaded("sponge")? "1" : "0"));
		str.add("RebornCore Version: " +  RebornCore.MOD_VERSION);
		return str;
	}
}
