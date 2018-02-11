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

package reborncore.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import reborncore.RebornCore;
import reborncore.common.IModInfo;
import reborncore.common.RebornCoreConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

@Deprecated
public class VersionChecker {

	public static final String apiAddress = RebornCore.WEB_URL + "api/v1/version.php";

	public String projectName;
	public IModInfo modInfo;

	ArrayList<ModifacationVersionInfo> versions;

	public boolean isChecking;

	public VersionChecker(String projectName, IModInfo modInfo) {
		this.projectName = projectName;
		this.modInfo = modInfo;
	}

	public void checkVersion() throws IOException {
		if (!RebornCoreConfig.versionCheck || true) //Disabled version check
		{
			return;
		}
		isChecking = true;
		URL url = new URL(apiAddress + "?project=" + projectName);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding).replaceAll("<br />", "");

		Gson gson = new Gson();
		versions = gson.fromJson(body, new TypeToken<ArrayList<ModifacationVersionInfo>>() {
		}.getType());
		isChecking = false;
	}

	public void checkVersionThreaded() {
		new Thread(new Runnable() {
			public void run() {
				try {
					checkVersion();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public boolean isLatestVersion() {
		if (versions == null || versions.isEmpty()) {
			return true;
		}
		return versions.get(0).version.equals(modInfo.MOD_VERSION());
	}

	public ModifacationVersionInfo getLatestVersion() {
		if (versions == null || versions.isEmpty()) {
			return null;
		}
		return versions.get(0);
	}

	public ArrayList<String> getChangeLogSinceCurrentVersion() {
		ArrayList<String> log = new ArrayList<String>();
		if (!isLatestVersion()) {
			for (ModifacationVersionInfo version : versions) {
				if (version.version.equals(modInfo.MOD_VERSION())) {
					break;
				}
				log.addAll(version.changeLog);
			}
		}
		return log;
	}

	static class ModifacationVersionInfo {
		public String version;

		public String minecraftVersion;

		public ArrayList<String> changeLog;

		public String releaseDate;

		public boolean recommended;

		public ModifacationVersionInfo(String version, String minecraftVersion, ArrayList<String> changeLog,
		                               String releaseDate, boolean recommended) {
			this.version = version;
			this.minecraftVersion = minecraftVersion;
			this.changeLog = changeLog;
			this.releaseDate = releaseDate;
			this.recommended = recommended;
		}

		public ModifacationVersionInfo() {
		}
	}

	// use this to make an example json file
	public static void main(String[] args) throws IOException {
		System.out.println("Generating example json file");
		ArrayList<ModifacationVersionInfo> infos = new ArrayList<ModifacationVersionInfo>();
		ArrayList<String> changelog = new ArrayList<String>();
		changelog.add("A change");
		changelog.add("Another change");

		infos.add(new ModifacationVersionInfo("1.1.1", "1.7.10", changelog, "12th July", true));
		infos.add(new ModifacationVersionInfo("1.2.0", "1.7.10", changelog, "28th July", true));

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(infos);
		try {
			FileWriter writer = new FileWriter(new File("master.json"));
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
