package reborncore.mixin;


import org.apache.logging.log4j.Logger;
import reborncore.mixin.json.MixinConfiguration;
import reborncore.mixin.json.MixinTargetData;
import reborncore.mixin.transformer.IMixinRemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MixinManager {

	public static List<String> mixinClassList = new ArrayList<>();
	public static HashMap<String, String> mixinTargetMap = new HashMap<>();

	//The remapper instance
	public static IMixinRemap mixinRemaper;
	//The logger
	public static Logger logger;

	public static void registerMixin(MixinTargetData data) {
		mixinClassList.add(data.mixinClass);
		mixinTargetMap.put(data.targetClass, data.mixinClass);
	}

	public static void registerMixinConfig(MixinConfiguration configuration){
		for(MixinTargetData mixindata : configuration.mixinData){
			registerMixin(mixindata);
		}
	}
}
