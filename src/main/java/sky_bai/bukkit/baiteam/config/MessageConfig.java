package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.BaiTeamMain;

public class MessageConfig {
	private File configFile = new File(BaiTeam.getBaiTeam().getDataFolder(), "config.yml");
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

	public static String BTMessage(String key, String... string ) {
		String mesString = key;
		for (int i = 0; i < string.length; i++) {
			mesString = mesString.replaceAll("\\&\\{"+i+"\\}", string[i]);
		}
		return mesString;
	}
	
	public FileConfiguration getConfig() {
		return messageConfig;
	}

	private void reset() throws IOException {
		InputStream a1 = BaiTeamMain.class.getResourceAsStream("/assets/message.yml");
		FileUtils.copyInputStreamToFile(a1, configFile);
	}
}
