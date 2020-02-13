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

public class BTCModule implements BTConfig.Config {
	private File configFile = new File(BTConfigGui.getGuiConfigPath(), "module.yml");
	private FileConfiguration config = new YamlConfiguration();

	public BTCModule() {
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

		List<String> Teaminfotrue = Arrays.asList("[Centered][18]%baiteam_player_name%", "[Action]踢出true", "[Action]转让true", "[Text]\\n");
		config.set("Teaminfotrue.Type","Player");
		config.set("Teaminfotrue.Collection","MembersNoLeader");
		config.set("Teaminfotrue.List",Teaminfotrue);
		config.set("Teaminfotrue.Row",1);
		
		List<String> Teaminfofalse = Arrays.asList("[Centered][18]%baiteam_player_name%", "[Action]踢出false", "[Action]转让false", "[Text]\\n");
		config.set("Teaminfofalse.Type","Player");
		config.set("Teaminfofalse.Collection","MembersNoLeader");
		config.set("Teaminfofalse.List",Teaminfofalse);
		config.set("Teaminfofalse.Row",1);
		
		List<String> TeamList = Arrays.asList("[Centered][18]%baiteam_teams_&<?>%", "[Text][%baiteam_team_members_amount_{baiteam_teams_&<?>}%/5]\\n", "[Text]         ", "[Action]申请加入", "[Text]\\n");
		config.set("TeamList.Type","Custom");
		config.set("TeamList.Amount","%baiteam_teams_amount%");
		config.set("TeamList.List",TeamList);
		config.set("TeamList.Row",2);
		
		List<String> PlayerList = Arrays.asList("[Centered][22]%baiteam_players_&<?>%", "[Action]邀请", "[Text]\\n");
		config.set("PlayerList.Type","Custom");
		config.set("PlayerList.Amount","%baiteam_players_amount%");
		config.set("PlayerList.List",PlayerList);
		config.set("PlayerList.Row",1);

		config.save(configFile);
	}
}
