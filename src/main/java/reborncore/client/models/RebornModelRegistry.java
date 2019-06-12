/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.client.models;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RebornModelRegistry {
	protected static List<ModelCompound> modelList = new ArrayList<>();

	public static void registerModel(ModelCompound modelCompound) {
		modelList.add(modelCompound);
	}

	public static void registerModels(String modid) {
//		for (ModelCompound compound : modelList) {
//			if (compound.getModid().equals(modid)) {
//				if (compound.isBlock()) {
//					if (compound.getFileName().equals("modelregistration.undefinedfilename")) {
//						registerItemModel(compound.getItem(), compound.getMeta(), compound.getBlockStatePath(), compound.getInventoryVariant());
//					} else {
//						registerItemModel(compound.getItem(), compound.getMeta(), compound.getFileName(), compound.getBlockStatePath(), compound.getInventoryVariant());
//					}
//				}
//				if (compound.isBlock()) {
//					if (compound.getFileName().equals("modelregistration.undefinedfilename")) {
//						setBlockStateMapper(compound.getBlock(), compound.getBlockStatePath(), compound.getIgnoreProperties());
//					} else {
//						setBlockStateMapper(compound.getBlock(), compound.getFileName(), compound.getBlockStatePath(), compound.getInventoryVariant(), compound.getIgnoreProperties());
//					}
//				}
//			}
//		}
	}

//	public static void registerItemModel(Item item) {
//		setMRL(item, 0, item.getRegistryName(), "inventory");
//	}
//
//	public static void registerItemModel(Item item, int meta) {
//		setMRL(item, meta, item.getRegistryName(), "inventory");
//	}
//
//	public static void registerItemModel(Item item, String fileName) {
//		Identifier loc = new Identifier(item.getRegistryName().getNamespace(), fileName);
//		setMRL(item, 0, loc, "inventory");
//	}
//
//	public static void registerItemModel(Item item, int meta, String path, String invVariant) {
//		String slash = "";
//		if (!path.isEmpty()) {
//			slash = "/";
//		}
//		Identifier loc = new Identifier(item.getRegistryName().getNamespace(), path + slash + item.getRegistryName().getPath());
//		setMRL(item, meta, loc, invVariant);
//	}
//
//	public static void registerItemModel(Item item, int meta, String fileName, String path, String invVariant) {
//		String slash = "";
//		if (!path.isEmpty()) {
//			slash = "/";
//		}
//		Identifier loc = new Identifier(item.getRegistryName().getNamespace(), path + slash + fileName);
//		setMRL(item, meta, loc, invVariant);
//	}
//
//	public static void registerBlockState(Item item, int meta, String path, String property, String variant) {
//		registerBlockState(item, meta, path, property + "=" + variant);
//	}
//
//	public static void registerBlockState(Item item, int meta, String path, String variant) {
//		Identifier loc = new Identifier(item.getRegistryName().getNamespace(), path + "/" + item.getRegistryName().getPath());
//		setMRL(item, meta, loc, variant);
//	}

	//TODO 1.13 ModelLoader stuff

	public static void setMRL(Item item, int meta, Identifier resourceLocation, String variant) {
		//ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(resourceLocation, variant));
	}

	//	public static void setCustomStateMapper(Block block, IStateMapper mapper) {
	//	//	ModelLoader.setCustomStateMapper(block, mapper);
	//	}

	public static void setBlockStateMapper(Block block, Property<?>... ignoredProperties) {
	//	setBlockStateMapper(block, block.getRegistryName().getPath(), ignoredProperties);
	}

	public static void setBlockStateMapper(Block block, String blockstatePath, Property<?>... ignoredProperties) {
	//	setBlockStateMapper(block, block.getRegistryName().getPath(), blockstatePath, ignoredProperties);
	}

	public static void setBlockStateMapper(Block block, String fileName, String path, Property<?>... ignoredProperties) {
		final String slash = !path.isEmpty() ? "/" : "";
		//		ModelLoader.setCustomStateMapper(block, new DefaultStateMapper() {
		//			@Override
		//			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		//				Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>>newLinkedHashMap(state.getProperties());
		//				for (IProperty<?> iproperty : ignoredProperties) {
		//					map.remove(iproperty);
		//				}
		//				return new ModelResourceLocation(new ResourceLocation(block.getRegistryName().getNamespace(), path + slash + fileName), this.getPropertyString(map));
		//			}
		//		});
	}

	public static void setBlockStateMapper(Block block, String fileName, String path, String invVariant, Property<?>... ignoredProperties) {
		final String slash = !path.isEmpty() ? "/" : "";
		//		ModelLoader.setCustomStateMapper(block, new DefaultStateMapper() {
		//			@Override
		//			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		//				Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>>newLinkedHashMap(state.getProperties());
		//				String propertyString = "";
		//				for (IProperty<?> iproperty : ignoredProperties) {
		//					map.remove(iproperty);
		//				}
		//				if (map.size() == 0) {
		//					propertyString = invVariant;
		//				} else {
		//					propertyString = this.getPropertyString(map) + invVariant;
		//				}
		//				return new ModelResourceLocation(new ResourceLocation(block.getRegistryName().getNamespace(), path + slash + fileName), propertyString);
		//			}
		//		});
	}
}
