package sky_bai.bukkit.baiteam;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import sky_bai.bukkit.baiteam.bookgui.BookGui;
import sky_bai.bukkit.baiteam.bookgui.nms.BookGuiOpenBook_v1_11_R1;
import sky_bai.bukkit.baiteam.bookgui.nms.BookGuiOpenBook_v1_12_R1;
import sky_bai.bukkit.baiteam.bookgui.nms.BookGuiOpenBook_v1_13_R1;
import sky_bai.bukkit.baiteam.bookgui.nms.BookGuiOpenBook_v1_13_R2;
import sky_bai.bukkit.baiteam.command.BaiTeamCommand;
import sky_bai.bukkit.baiteam.config.ConfigType;
import sky_bai.bukkit.baiteam.config.DefaultConfig;
import sky_bai.bukkit.baiteam.config.MessageConfig;
import sky_bai.bukkit.baiteam.listener.BaiListener;

public class BaiTeamMain extends JavaPlugin {

	@Override
	public void onEnable() {
		Boolean onPluginEnable = setBookGuiOpenBook();
		if (onPluginEnable == false) {
			getLogger().config("本插件不兼容当前版本版本");
			return;
		}
		getServer().getPluginManager().registerEvents(new BaiListener(), this);
		BaiTeamCommand command = new BaiTeamCommand();
		getCommand("BaiTeam").setExecutor(command);
		getCommand("BaiTeam").setTabCompleter(command);
		reload();
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onDisable() {

	}

	public String getMCVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	private void reload() {
		BaiTeam.BaiTeam = this;
		BaiTeam.BaiTLogger = getLogger();
		Map<ConfigType, FileConfiguration> configMap = BaiTeam.ConfigMap;
		configMap.clear();
		configMap.put(ConfigType.Config, new DefaultConfig().getConfig());
		configMap.put(ConfigType.Message, new MessageConfig().getConfig());
	}

	private Boolean setBookGuiOpenBook() {
		switch (getMCVersion()) {
		case "v1_11_R1":
			BookGui.setBookGuiOpenBookNMS(new BookGuiOpenBook_v1_11_R1());
			return true;
		case "v1_12_R1":
			BookGui.setBookGuiOpenBookNMS(new BookGuiOpenBook_v1_12_R1());
			return true;
		case "v1_13_R1":
			BookGui.setBookGuiOpenBookNMS(new BookGuiOpenBook_v1_13_R1());
			return true;
		case "v1_13_R2":
			BookGui.setBookGuiOpenBookNMS(new BookGuiOpenBook_v1_13_R2());
			return true;
		default:
			return false;
		}
	}

}
