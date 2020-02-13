package sky_bai.bukkit.baiteam.config.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import sky_bai.bukkit.baiteam.config.BTConfig;

public class BTCAction implements BTConfig.Config {
	private File configFile = new File(BTConfigGui.getGuiConfigPath(), "action.yml");
	private FileConfiguration config = new YamlConfiguration();

	public BTCAction() {
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
		
		setConfig("创建队伍", "[创建队伍]", "/baiteam create", "创建你的队伍");
		setConfig("加入队伍", "[加入队伍]", "/baiteam opengui TeamLis", "寻找可以加入的队伍");
		setConfig("队伍人数", "[%baiteam_team_members_amount%/5]", null, "%baiteam_team_members%");
		setConfig("邀请玩家true", "[邀请玩家]", "/baiteam OpenGui PlayerList", "点击查看未加入队伍的玩家");
		setConfig("邀请玩家false", "[§7邀请玩家§r]", null, "只有队长可以邀请玩家");
		setConfig("公开招募true", "[公开招募]", "/baiteam guipromotional %baiteam_team_name%", "发布队伍招募公告");
		setConfig("公开招募false", "[§7公开招募§r]", null, "只有队长可以发布招募公告");
		setConfig("离开队伍true", "[解散队伍]", "/baiteam Leave", "点击解散队伍");
		setConfig("离开队伍false", "[离开队伍]", "/baiteam Leave", "点击离开队伍");
		setConfig("踢出true", "[踢出]", "/baiteam GuiKick %baiteam_player_name%", "将玩家踢出队伍");
		setConfig("踢出false", "[§7踢出§r]", null, null);
		setConfig("转让true", "[转让]", "/baiteam GuiTransfer %baiteam_player_name%", "把队伍转让给玩家");
		setConfig("转让false", "[§7转让§r]", null, null);
		setConfig("申请加入", "[申请加入]", "/baiteam applyto %baiteam_teams_&<?>%", "点击申请加入队伍");
		setConfig("TeamList上一页", "[上一页]", "/baiteam opengui TeamList &<p->", null);
		setConfig("TeamList下一页", "[下一页]", "/baiteam opengui TeamList &<p+>", null);
		setConfig("邀请", "[邀请]", "/baiteam InviteTo %baiteam_players_&<?>%", "点击邀请玩家加入");
		setConfig("PlayerList上一页", "[上一页]", "/baiteam opengui PlayerList &<p->", null);
		setConfig("PlayerList下一页", "[下一页]", "/baiteam opengui PlayerList &<p+>", null);
		
		config.save(configFile);
	}
	
	private void setConfig(String name,String text,String command,String showtext) {
		config.set(name + ".Text", text);
		if (command != null) {
			config.set(name + ".Command", command);
		}
		if (showtext != null) {
			config.set(name + ".ShowText", showtext);
		}
	}
}
