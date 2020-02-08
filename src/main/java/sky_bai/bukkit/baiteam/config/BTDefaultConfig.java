package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;

public class BTDefaultConfig implements BTConfig.Config {
	private File configFile = new File(BaiTeam.getInstance().getDataFolder(), "config.yml");
	private FileConfiguration config = new YamlConfiguration();

	public BTDefaultConfig() {
		try {
			reset();
		} catch (Exception e) {
		}

	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	public void reset() throws FileNotFoundException, IOException, InvalidConfigurationException {
		BaiTeam.getInstance().saveDefaultConfig();
		config.load(configFile);
	}
}
