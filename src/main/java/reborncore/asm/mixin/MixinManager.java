package reborncore.asm.mixin;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 30/12/2016.
 */
public class MixinManager {

	public static List<String> mixinClassList = new ArrayList<>();
	public static HashMap<String, String> mixinTargetMap = new HashMap<>();

	public static void registerMixin(String mixinName, String targetName) {
		mixinClassList.add(mixinName);
		mixinTargetMap.put(targetName, mixinName);
	}

	public static void loadMixinData() throws NotFoundException, ClassNotFoundException {
		ClassPool.getDefault().appendClassPath(new LoaderClassPath(Launch.classLoader));
	}
}
