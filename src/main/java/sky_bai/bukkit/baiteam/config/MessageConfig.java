package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;

public class MessageConfig implements BTConfig.Config {
	private File configFile = new File(BaiTeam.getBaiTeam().getDataFolder(), "message.yml");
	private FileConfiguration messageConfig = new YamlConfiguration();

	public MessageConfig() {
		try {
			if (configFile.exists() == false) {
				reset();
			}
			messageConfig.load(configFile);
		} catch (Exception e) {

		}
	}

	@Override
	public FileConfiguration getConfig() {
		return messageConfig;
	}

	private void reset() throws IOException {
		InputStream a1 = BaiTeam.class.getResourceAsStream("/assets/message.yml");
		BaiTeam.getBaiTeam().copyInputStreamToFile(a1, configFile);
	}
}
