package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;

public class DefaultConfig implements BTConfig.Config {

	private File configFile = new File(BaiTeam.getBaiTeam().getDataFolder(), "config.yml");
	private FileConfiguration defaultConfig = new YamlConfiguration();

	public DefaultConfig() {
		try {
			if (configFile.exists() == false) {
				reset();
			}
			defaultConfig.load(configFile);
		} catch (Exception e) {

		}
	}

	@Override
	public FileConfiguration getConfig() {
		return defaultConfig;
	}

	private void reset() throws IOException {
		InputStream a1 = BaiTeam.class.getResourceAsStream("/assets/config.yml");
		BaiTeam.getBaiTeam().copyInputStreamToFile(a1, configFile);
	}

}
