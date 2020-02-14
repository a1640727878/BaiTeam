package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.message.BTMessage;

public class BTMessageConfig implements BTConfig.Config {
	public final File configFile = new File(BaiTeam.getInstance().getDataFolder(), "message.yml");
	private FileConfiguration config = new YamlConfiguration();

	public BTMessageConfig() {
		try {
			reset();
		} catch (Exception e) {
		}
	}

	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public File getConfigFile() {
		return configFile;
	}

	public void reset() throws IOException, InvalidConfigurationException {
		BaiTeam.getInstance().getDataFolder().mkdirs();
		configFile.createNewFile();
		config.load(configFile);
		BTMessage.Error[] btm1 = BTMessage.Error.values();
		for (BTMessage.Error mes : btm1) {
			if (config.contains(mes.getKey()) == false) {
				config.set(mes.getKey(), mes.getMes());
			} else {
				mes.setMes(config.getString(mes.getKey()));
			}
		}
		BTMessage.Team[] btm2 = BTMessage.Team.values();
		for (BTMessage.Team mes : btm2) {
			if (config.contains(mes.getKey()) == false) {
				config.set(mes.getKey(), mes.getMes());
			} else {
				mes.setMes(config.getString(mes.getKey()));
			}
		}
		BTMessage.Button[] btm3 = BTMessage.Button.values();
		for (BTMessage.Button mes : btm3) {
			if (config.contains(mes.getKey()) == false) {
				config.set(mes.getKey(), mes.getMes());
			} else {
				mes.setMes(config.getString(mes.getKey()));
			}
		}
		config.save(configFile);
	}

}
