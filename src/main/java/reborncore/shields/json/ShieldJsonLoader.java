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

package reborncore.shields.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;
import reborncore.RebornCore;
import reborncore.shields.FaceShield;
import reborncore.shields.api.ShieldRegistry;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mark on 25/03/2016.
 */
public class ShieldJsonLoader {

	public static boolean hasValidJsonFile;
	@Nonnull
	public static ShieldJsonFile shieldJsonFile;
	public static HashMap<String, String> customTextureNameList = new HashMap<>();

	/**
	 * This is used to make the json file
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ShieldJsonFile jsonFile = new ShieldJsonFile();
		jsonFile.userList = new ArrayList<>();
		File folder = new File("src/main/resources/assets/reborncore/textures/shields/people/");
		for (File person : folder.listFiles()) {
			jsonFile.userList.add(new ShieldUser(person.getName().replace(".png", "")));
		}
		File output = new File("shields.json");
		System.out.println(output.getAbsolutePath());

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(jsonFile);
		try {
			FileWriter writer = new FileWriter(output);
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load(FMLPreInitializationEvent event) {
		new Thread(() ->
		{
			try {
				File file = new File(event.getModConfigurationDirectory(), "reborncore/shields.json");
				if (file.exists()) {
					file.delete();
				}

				URLConnection con = new URL(RebornCore.WEB_URL + "reborncore/shields2.json").openConnection();
				InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				String body = IOUtils.toString(in, encoding);

				Gson gson = new Gson();
				BufferedReader reader = new BufferedReader(new StringReader(body));
				Type typeOfHashMap = new TypeToken<ShieldJsonFile>() {
				}.getType();
				shieldJsonFile = gson.fromJson(reader, typeOfHashMap);
				hasValidJsonFile = true;

				if (shieldJsonFile != null) {
					for (ShieldUser user : shieldJsonFile.userList) {
						ShieldRegistry.registerShield(new FaceShield(user.username));
						if (user.textureName == null) {
							user.textureName = "";
						}
						if (!user.textureName.isEmpty()) {
							customTextureNameList.put(user.username, user.textureName);
						}
					}
					RebornCore.proxy.loadShieldTextures();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}).start();

	}

	public static String getMD5(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}

}
