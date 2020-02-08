package sky_bai.bukkit.baiteam;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import sky_bai.bukkit.baiteam.command.BTCommandCMD;
import sky_bai.bukkit.baiteam.command.BTCommandPlayer;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.config.BTDefaultConfig;
import sky_bai.bukkit.baiteam.config.BTMessageConfig;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.TeamManager;
import sky_bai.bukkit.baiteam.team.TeamTeleport;
import sky_bai.bukkit.baiteam.util.BTPlaceholderAPI;

public final class BaiTeam extends JavaPlugin {

	private static BaiTeam baiTeam;

	private static TeamManager teamManager;

	private static ProtocolManager pm;

	@Override
	public void onEnable() {
		baiTeam = this;
		pm = ProtocolLibrary.getProtocolManager();

		BTConfig.setConfig(new BTDefaultConfig());
		BTConfig.setMessage(new BTMessageConfig());

		teamManager = new TeamManager();

		getCommand("BaiTeam").setExecutor(new BTCommandPlayer());
		getCommand("BaiTeamCmd").setExecutor(new BTCommandCMD());

		BTMessage.setMesPrefix(BTConfig.getConfig().getConfig().getString("MesPrefix", "[BaiTeam] "));

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("检测到 PAPI ,注册PAPI变量中...");
			new BTPlaceholderAPI().register();
			getLogger().info("注册完成!!");
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Long time : TeamTeleport.UuidTime.keySet()) {
					if (System.currentTimeMillis() - time >= BTConfig.getConfig().getConfig().getLong("ExpiredTime", 20000)) {
						String uuid = TeamTeleport.UuidTime.get(time);
						TeamTeleport.LocationMap.remove(uuid);
						TeamTeleport.TeleportPlayer.remove(uuid);
						TeamTeleport.UuidTime.remove(time);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 10, 10);
	}

	public static BaiTeam getInstance() {
		return baiTeam;
	}

	public static TeamManager getTeamManager() {
		return teamManager;
	}

	public static ProtocolManager getProtocolManager() {
		return pm;
	}
}
