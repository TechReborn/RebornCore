package teamreborn.reborncore.manual;

import teamreborn.reborncore.RebornCore;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Created by Prospector.
 */
public class ManualData {
	public static List<File> configs = new ArrayList<>();
	public static File manifest;
	public static List<String> test = new ArrayList<>();
	public static List<String> strings = new ArrayList<>();

	public static String homeDirectory = null;
	public static List<String> directories = null;
	public static List<String> files = null;

	public static void reloadConfig() {
		test.clear();
		test.add("page1.json");
		test.add("page2.json");
		strings.clear();
		strings.add("page1");
		strings.add("page2");
		if (!manifest.exists()) {
			writeConfig(new Manifest());
		}
		if (manifest.exists()) {
			Manifest config = null;
			try (Reader reader = new FileReader(manifest)) {
				config = RebornCore.GSON.fromJson(reader, Manifest.class);
				homeDirectory = config.getHomeDirectory();
				directories = config.getDirectories();
				files = config.getFiles();
				System.out.println(homeDirectory);
				System.out.println(directories);
				System.out.println(files);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (config == null) {
				config = new Manifest();
				writeConfig(config);
			}
		}
	}

	public static void writeConfig(Manifest config) {
		try (Writer writer = new FileWriter(manifest)) {
			RebornCore.GSON.toJson(config, writer);
		} catch (Exception e) {

		}
		reloadConfig();
	}

	public static class Manifest {
		public String homeDirectory = "home.json";
		public List<String> directories = test;
		public List<String> files = strings;

		public String getHomeDirectory() {
			return this.homeDirectory;
		}

		public void setHomeDirectory(String homeDirectory) {
			this.homeDirectory = homeDirectory;
		}

		public List<String> getDirectories() {
			return this.directories;
		}

		public void setDirectories(List<String> directories) {
			this.directories = directories;
		}

		public List<String> getFiles() {
			return this.files;
		}

		public void setFiles(List<String> files) {
			this.files = files;
		}
	}
/*

	public static class Directory {
		List<ResourceLocation> directories;
		List<ResourceLocation> files;
	}

	public static class ManualPage {
		ResourceLocation nextPage;
	}
*/
}
