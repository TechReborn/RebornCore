package reborncore.common.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import reborncore.common.registration.impl.ConfigRegistryFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ConfigGuiFactory implements IModGuiFactory {

	public abstract String getModID();

	public abstract String getModName();

	public static class ConfigGui extends GuiConfig {

		public ConfigGui(GuiScreen parentScreen, String modid, String modName) {
			super(parentScreen, getElemets(modid), modid, modName, false, false, modName + " Configuration");
		}

		public static List<IConfigElement> getElemets(String modid){
			return ConfigRegistryFactory.getConfigs(modid).stream()
				.map(pair -> new DummyConfigElement.DummyCategoryElement(pair.getRight(), pair.getRight(), getChildren(pair.getLeft())))
				.collect(Collectors.toList());
		}

		public static List<IConfigElement> getChildren(Configuration configuration){
			List<IConfigElement> elements = new ArrayList<>();
			for(String categoryName : configuration.getCategoryNames()){
				ConfigCategory category = configuration.getCategory(categoryName);
				for(Property property : category.values()){
					elements.add(new ConfigElement(property){
						@Override
						public String getName() {
							return categoryName + "." + super.getName();
						}
					});
				}
			}
			return elements;
		}

	}

	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ConfigGui(parentScreen, getModID(), getModName());
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}
}
