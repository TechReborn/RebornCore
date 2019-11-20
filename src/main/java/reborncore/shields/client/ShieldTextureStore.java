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


package reborncore.shields.client;

import reborncore.RebornCore;
import reborncore.shields.json.ShieldJsonFile;
import reborncore.shields.json.ShieldJsonLoader;
import reborncore.shields.json.ShieldUser;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Created by modmuss50 on 23/05/2016.
 */
public class ShieldTextureStore {

	static HashMap<String, ShieldTexture> textures = new HashMap<>();
	static HashMap<String, ShieldTexture> customTextures = new HashMap<>();

	public static void load() {
		ShieldJsonFile file = ShieldJsonLoader.shieldJsonFile;
		if (file != null) {
			for (ShieldUser user : file.userList) {
				boolean isCustomTexture = false;
				String name = user.username;
				if (!user.textureName.isEmpty()) {
					name = user.textureName;
					isCustomTexture = true;
				}
				if (isCustomTexture) {
					ShieldTexture texture;
					if (customTextures.containsKey(name)) {
						texture = customTextures.get(name);
					} else {
						texture = new ShieldTexture(RebornCore.WEB_URL + "reborncore/textures/" + name + ".png");
						customTextures.put(name, texture);
					}
					textures.put(user.username, texture);
				} else {
					textures.put(user.username, new ShieldTexture(RebornCore.WEB_URL + "reborncore/textures/" + user.username + ".png"));
				}
			}
		}
	}

	public static
	@Nullable
	ShieldTexture getTexture(String name) {
		if (!textures.isEmpty()) {
			ShieldTexture texture = null;
			if (ShieldJsonLoader.customTextureNameList.containsKey(name)) {
				name = ShieldJsonLoader.customTextureNameList.get(name);
				if (customTextures.containsKey(name)) {
					texture = customTextures.get(name);
				}
			} else {
				if (textures.containsKey(name)) {
					texture = textures.get(name);
				}
			}
			if (texture != null && texture.getState() == DownloadState.AVAILABLE) {
				texture.download();
			}
			return texture;

		}
		return null;
	}

}
