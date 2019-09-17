package reborncore.common.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import reborncore.RebornCore;

import java.io.*;

public class ConfigurationWrapper {

	private final File file;
	private final HoconConfigurationLoader configurationLoader;
	private final CommentedConfigurationNode configurationNode;


	public ConfigurationWrapper(String path) {
		this.file = new File(RebornCore.hooks.getConfigDir(), path + ".hocon");
		configurationLoader = getLoader();
		try {
			configurationNode = configurationLoader.load();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load config " + path, e);
		}
	}

	private HoconConfigurationLoader getLoader(){
		return HoconConfigurationLoader.builder()
			.setSource(() -> new BufferedReader(new FileReader(file)))
			.setSink(() -> new BufferedWriter(new FileWriter(file))).build();
	}

	public CommentedConfigurationNode getRootNode(){
		return configurationNode;
	}

	public CommentedConfigurationNode getNode(Object... path){
		return configurationNode.getNode(path);
	}

	public void save() {
		try {
			configurationLoader.save(configurationNode);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save config", e);
		}
	}

}
