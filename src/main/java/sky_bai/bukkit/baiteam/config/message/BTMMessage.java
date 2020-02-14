package sky_bai.bukkit.baiteam.config.message;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.config.BTConfig;

public class BTMMessage implements BTConfig.Config {
	public final File configFile = new File(BTConfigMes.getMessageconfigpath(), "message.yml");
	private FileConfiguration config = new YamlConfiguration();

	public BTMMessage() {
		try {
			reset();
		} catch (IOException | InvalidConfigurationException e) {
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public File getConfigFile() {
		return configFile;
	}

	public void reset() throws IOException, InvalidConfigurationException {
		BTConfigMes.getMessageconfigpath().mkdirs();
		configFile.createNewFile();
		config.load(configFile);

		if (config.getKeys(false).size() > 0) {
			return;
		}
		
	}
}
