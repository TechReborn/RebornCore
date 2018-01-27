package reborncore.common.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import reborncore.RebornCore;
import reborncore.api.newRecipe.IRecipe;
import reborncore.api.newRecipe.IRecipeFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class RecipeLoader {

	static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Loads or reloads all of the recipes
	 */
	public static void load(){
		//Clears all the recipes
		RecipeFactoryManager.getFactorys().forEach(IRecipeFactory::clear);

		Loader.instance().getActiveModList().forEach(RecipeLoader::loadModRecipes);
		loadConfigRecipes();
	}

	private static void loadModRecipes(ModContainer mod){
		CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/rc_recipes", null, (root, file) -> loadRecipe(root, file, mod.getModId()) != null, false, true);
	}

	/**
	 * Loads the recipes from the config
	 */
	//TODO check if needed in 1.13 with content packs
	private static void loadConfigRecipes(){
		File configDir = new File(RebornCore.configDir, "recipes");
		if(!configDir.exists()){
			configDir.mkdir();
		}
		if(configDir.listFiles() == null){
			return;
		}
		//TODO check recipes to disable
		scanDir(configDir);
	}

	private static void scanDir(File root){
		if(root.listFiles() == null){
			return;
		}
		Arrays.stream(root.listFiles()).forEach(file -> {
			if(file.isDirectory()){
				scanDir(file);
			} else {
				loadRecipe(root.toPath(), file.toPath(), "rc_config");
			}
		});
	}

	private static IRecipe loadRecipe(Path root, Path file, String modid){
		String filename = root.relativize(file).toString();
		if(!filename.endsWith(".json") || filename.startsWith("_")){
			return null;
		}
		try {
			BufferedReader reader = Files.newBufferedReader(file);
			JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
			String factoryName = jsonObject.getAsJsonPrimitive("factory").getAsString();
			IRecipeFactory recipeFactory = RecipeFactoryManager.getRecipeFactory(new ResourceLocation(factoryName));
			if(recipeFactory == null){
				throw new RuntimeException("Failed to find factory with name " + factoryName);
			}
			return recipeFactory.load(jsonObject.getAsJsonObject("recipe"), new ResourceLocation(modid, filename));
		} catch (IOException e) {
			e.printStackTrace();
			//TODO better error handling
			throw new RuntimeException("Failed to load recipe", e);
		}
	}
}
