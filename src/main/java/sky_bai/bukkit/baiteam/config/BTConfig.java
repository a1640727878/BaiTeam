package sky_bai.bukkit.baiteam.config;

import org.bukkit.configuration.file.FileConfiguration;

public class BTConfig {

	private static Config config;

	private static Config message;

	public static Config getConfig() {
		return config;
	}

	public static Config getMessage() {
		return message;
	}

	public static void setConfig(Config config) {
		BTConfig.config = config;
	}

	public static void setMessage(Config message) {
		BTConfig.message = message;
	}

	public interface Config {
		public FileConfiguration getConfig();
	}
}
