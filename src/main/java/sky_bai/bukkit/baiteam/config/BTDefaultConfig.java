package sky_bai.bukkit.baiteam.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.BaiTeam;

public class BTDefaultConfig implements BTConfig.Config {
	public final File configFile = new File(BaiTeam.getInstance().getDataFolder(), "config.yml");
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

	@Override
	public File getConfigFile() {
		return configFile;
	}

	public void reset() throws FileNotFoundException, IOException, InvalidConfigurationException {
		BaiTeam.getInstance().getDataFolder().mkdirs();
		configFile.createNewFile();
		config.load(configFile);

		if (config.getKeys(false).size() > 0) {
			return;
		}

		config.set("MesPrefix", "[BaiTeam] ");
		config.set("Time.Teleport.Expired", 20000);
		config.set("Time.Promotional.CoolDown", 20000);
		config.set("TeamSize", 5);
		
		List<String> names = Arrays.asList("队伍名","小队名","小组名");
		config.set("TeamNames.Enable", true);
		config.set("TeamNames.List", names);
		
		config.save(configFile);
	}

}
