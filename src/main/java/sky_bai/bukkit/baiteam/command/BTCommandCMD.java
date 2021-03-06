package sky_bai.bukkit.baiteam.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.message.BTMessage;

public class BTCommandCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] strs) {
		BaiTeam.getInstance().getLogger().info("命令执行");
		if (strs.length < 1) {
			return false;
		}
		if (Bukkit.getPlayer(strs[0]) == null) {
			BaiTeam.getInstance().getLogger().info("玩家" + strs[0] + "不存在");
			return false;
		}
		Player player = Bukkit.getPlayer(strs[0]);
		String[] args = new String[strs.length - 1];
		for (int i = 0; i < args.length; i++) {
			args[i] = strs[i + 1];
		}
		switch (args[0].toLowerCase()) {
		case "play": {
			return BTCommand.play(player, args);
		}
		case "opengui": {
			return BTCommand.openGui(player, args);
		}
		case "kickdun": {
			return BTCommand.kickTeamFoDungeon(player);
		}
		case "relaod": {
			BTConfig.reload();
			BTMessage.setMesPrefix(BTConfig.getConfig().getConfig().getString("MesPrefix", "[BaiTeam] "));
			return true;
		}
		}
		return false;
	}

}
