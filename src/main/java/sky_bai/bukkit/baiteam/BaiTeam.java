package sky_bai.bukkit.baiteam;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.config.ConfigType;

public class BaiTeam {
	protected static BaiTeamMain BaiTeam;
	protected static Logger BaiTLogger;
	protected static Map<ConfigType, FileConfiguration> ConfigMap = new HashMap<ConfigType, FileConfiguration>();
	
	public static BaiTeamMain getBaiTeam() {
		return BaiTeam;
	}
	
	public static Logger getBaiTLogger() {
		return BaiTLogger;
	}
	
	public static FileConfiguration getConfig(ConfigType key) {
		if (ConfigMap.containsKey(key)) {
			return ConfigMap.get(key);
		}
		return new YamlConfiguration();
	}
}
