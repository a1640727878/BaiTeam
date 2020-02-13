package sky_bai.bukkit.baiteam.config.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.config.BTConfig;

public class BTCGui implements BTConfig.Config {
	private File configFile = new File(BTConfigGui.getGuiConfigPath(), "gui.yml");
	private FileConfiguration config = new YamlConfiguration();

	public BTCGui() {
		try {
			reset();
		} catch (IOException | InvalidConfigurationException e) {
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	public void reset() throws FileNotFoundException, IOException, InvalidConfigurationException {
		BTConfigGui.getGuiConfigPath().mkdirs();
		configFile.createNewFile();
		config.load(configFile);

		if (config.getKeys(false).size() > 0) {
			return;
		}

		List<String> mainList = Arrays.asList("[Text]§m============================\\n\\n\\n\\n\\n", "[Text]         ", "[Action]创建队伍", "[Text]\\n\\n", "[Text]         ", "[Action]加入队伍", "[Text]\\n\\n\\n\\n\\n§m============================");
		config.set("Main", mainList);

		List<String> teaminfolist_0 = Arrays.asList("[Text]         [队伍信息]         \\n", "[Text]队伍名称: %baiteam_team_name%", "[Text]\\n\\n队长名称: %baiteam_team_leader%", "[Text]\\n\\n队伍人数: ", "[Action]队伍人数", "[Text]\\n\\n§m                             \\n\\n", "[Text]         ", "[Action]邀请玩家%baiteam_team_onleader%", "[Text]         \\n", "[Text]         ", "[Action]公开招募%baiteam_team_onleader%", "[Text]         \\n", "[Text]         ", "[Action]离开队伍%baiteam_team_onleader%", "[Text]         \\n");
		config.set("Teaminfo.0", teaminfolist_0);

		List<String> teaminfolist_1 = Arrays.asList("[Module][5]Teaminfo%baiteam_team_onleader%");
		config.set("Teaminfo.1", teaminfolist_1);

		List<String> teamlistlist = Arrays.asList("[Module][6]TeamList", "[Text]§m============================\\n", "[Text]  ", "[Action]TeamList上一页", "[Text]   ", "[Text][&<p>]", "[Text]   ", "[Action]TeamList下一页");
		config.set("TeamList", teamlistlist);

		List<String> playerlistlist = Arrays.asList("[Module][12]PlayerList", "[Text]§m============================\\n", "[Text]  ", "[Action]TeamList上一页", "[Text]   ", "[Text][&<p>]", "[Text]   ", "[Action]TeamList下一页");
		config.set("PlayerList", playerlistlist);
		
		config.save(configFile);
	}
}
