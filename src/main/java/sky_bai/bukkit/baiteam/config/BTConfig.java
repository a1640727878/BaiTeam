package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class BTConfig {

	private static Config config;

	private static Config message;

	private static Config gui;

	private static Config action;

	private static Config module;

	public static Config getConfig() {
		return config;
	}

	public static Config getMessage() {
		return message;
	}

	public static Config getGui() {
		return gui;
	}

	public static Config getAction() {
		return action;
	}

	public static Config getModule() {
		return module;
	}

	public static void setConfig(Config config) {
		BTConfig.config = config;
	}

	public static void setMessage(Config message) {
		BTConfig.message = message;
	}

	public static void setGui(Config gui) {
		BTConfig.gui = gui;
	}

	public static void setAction(Config action) {
		BTConfig.action = action;
	}

	public static void setModule(Config module) {
		BTConfig.module = module;
	}

	public static void reload() {
		try {
			config.getConfig().load(config.getConfigFile());
			message.getConfig().load(message.getConfigFile());
			gui.getConfig().load(gui.getConfigFile());
			action.getConfig().load(action.getConfigFile());
			module.getConfig().load(module.getConfigFile());
		} catch (IOException | InvalidConfigurationException e) {
		}
	}
	
	public interface Config {
		public FileConfiguration getConfig();
		public File getConfigFile();
	}
}
