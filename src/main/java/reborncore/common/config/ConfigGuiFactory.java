//TODO 1.13 nope

///*
// * Copyright (c) 2018 modmuss50 and Gigabit101
// *
// *
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// *
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// *
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//
//package reborncore.common.config;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraftforge.fml.client.IModGuiFactory;
//import net.minecraftforge.fml.client.config.DummyConfigElement;
//import net.minecraftforge.fml.client.config.IConfigElement;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//public abstract class ConfigGuiFactory implements IModGuiFactory {
//
//	public abstract String getModID();
//
//	public abstract String getModName();
//
//	public static class ConfigGui extends GuiConfig {
//
//		public ConfigGui(GuiScreen parentScreen, String modid, String modName) {
//			super(parentScreen, getElemets(modid), modid, modName, false, false, modName + " Configuration");
//		}
//
//		public static List<IConfigElement> getElemets(String modid){
//			return ConfigRegistryFactory.getConfigs(modid).stream()
//				.map(pair -> new DummyConfigElement.DummyCategoryElement(pair.getRight(), pair.getRight(), getChildren(pair.getLeft())))
//				.collect(Collectors.toList());
//		}
//
//		public static List<IConfigElement> getChildren(Configuration configuration){
//			List<IConfigElement> elements = new ArrayList<>();
//			for(String categoryName : configuration.getCategoryNames()){
//				ConfigCategory category = configuration.getCategory(categoryName);
//				for(Property property : category.values()){
//					elements.add(new ConfigElement(property){
//						@Override
//						public String getName() {
//							return categoryName + "." + super.getName();
//						}
//					});
//				}
//			}
//			return elements;
//		}
//
//	}
//
//	@Override
//	public void initialize(Minecraft minecraftInstance) {
//
//	}
//
//	@Override
//	public boolean hasConfigGui() {
//		return true;
//	}
//
//	@Override
//	public GuiScreen createConfigGui(GuiScreen parentScreen) {
//		return new ConfigGui(parentScreen, getModID(), getModName());
//	}
//
//	@Override
//	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
//		return Collections.emptySet();
//	}
//}
