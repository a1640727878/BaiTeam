package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.BaiTeamMain;

public class DefaultConfig {
	private File configFile = new File(BaiTeam.getBaiTeam().getDataFolder(), "config.yml");
	private FileConfiguration defaultConfig = new YamlConfiguration();
	
	public DefaultConfig(){
		try {
			if (configFile.exists() == false) {
				reset();
			}
			defaultConfig.load(configFile);
		}catch (Exception e) {

		}
	}

	public FileConfiguration getConfig() {
		return defaultConfig;
	}
	
	private void reset() throws IOException {
		InputStream a1 = BaiTeamMain.class.getResourceAsStream("/assets/config.yml");
		FileUtils.copyInputStreamToFile(a1, configFile);
	}
	
}
