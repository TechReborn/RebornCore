package reborncore.mixin.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mark on 02/01/2017.
 */
public class JsonUtil {

	public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static MixinConfiguration mixinConfigurationFromFile(File file) throws IOException {
		return mixinConfigurationFromString(FileUtils.readFileToString(file));
	}

	public static MixinConfiguration mixinConfigurationFromString(String json){
		return GSON.fromJson(json, MixinConfiguration.class);
	}

	public static void main(String[] args) throws IOException {
		MixinConfiguration configuration = new MixinConfiguration();
		configuration.mixinData = new ArrayList<>();
		configuration.mixinData.add(new MixinTargetData("mixinclass", "targetclass"));
		configuration.mixinData.add(new MixinTargetData("mixinclass2", "targetclass2"));
		String json = GSON.toJson(configuration);
		FileUtils.writeStringToFile(new File("mixinConfig.json"), json);
	}

}
