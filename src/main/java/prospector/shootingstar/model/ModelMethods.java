/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package prospector.shootingstar.model;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Map;

public class ModelMethods {
	public static void registerItemModel(Item item) {
		setMRL(item, 0, item.getRegistryName(), "inventory");
	}

	public static void registerItemModel(Item item, int meta) {
		setMRL(item, meta, item.getRegistryName(), "inventory");
	}

	public static void registerItemModel(Item item, String fileName, String path) {
		ResourceLocation loc = new ResourceLocation(item.getRegistryName().getResourceDomain(), path + "/" + item.getRegistryName().getResourcePath());
		setMRL(item, 0, loc, "inventory");
	}

	public static void registerItemModel(Item item, int meta, String path, String invVariant) {
		String slash = "";
		if (!path.isEmpty())
			slash = "/";
		ResourceLocation loc = new ResourceLocation(item.getRegistryName().getResourceDomain(), path + slash + item.getRegistryName().getResourcePath());
		setMRL(item, meta, loc, invVariant);
	}

	public static void registerItemModel(Item item, int meta, String fileName, String path, String invVariant) {
		String slash = "";
		if (!path.isEmpty())
			slash = "/";
		ResourceLocation loc = new ResourceLocation(item.getRegistryName().getResourceDomain(), path + slash + fileName);
		setMRL(item, meta, loc, invVariant);
	}

	public static void registerBlockState(Item item, int meta, String path, String property, String variant) {
		registerBlockState(item, meta, path, property + "=" + variant);
	}

	public static void registerBlockState(Item item, int meta, String path, String variant) {
		ResourceLocation loc = new ResourceLocation(item.getRegistryName().getResourceDomain(), path + "/" + item.getRegistryName().getResourcePath());
		setMRL(item, meta, loc, variant);
	}

	public static void setMRL(Item item, int meta, ResourceLocation resourceLocation, String variant) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(resourceLocation, variant));
	}

	public static void setCustomStateMapper(Block block, IStateMapper mapper) {
		ModelLoader.setCustomStateMapper(block, mapper);
	}

	public static void setBlockStateMapper(Block block, IProperty<?>[] ignoredProperties) {
		setBlockStateMapper(block, block.getRegistryName().getResourcePath(), ignoredProperties);
	}

	public static void setBlockStateMapper(Block block, String blockstatePath, IProperty<?>[] ignoredProperties) {
		setBlockStateMapper(block, block.getRegistryName().getResourcePath(), blockstatePath, ignoredProperties);
	}

	public static void setBlockStateMapper(Block block, String fileName, String path, IProperty<?>[] ignoredProperties) {
		final String slash = !path.isEmpty() ? "/" : "";
		ModelLoader.setCustomStateMapper(block, new DefaultStateMapper() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>>newLinkedHashMap(state.getProperties());
				for (IProperty<?> iproperty : ignoredProperties) {
					map.remove(iproperty);
				}
				return new ModelResourceLocation(new ResourceLocation(block.getRegistryName().getResourceDomain(), path + slash + fileName), this.getPropertyString(map));
			}
		});
	}
}
