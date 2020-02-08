package sky_bai.bukkit.baiteam.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BTCommandCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] strs) {
		if (strs.length > 1) {
			if (Bukkit.getPlayer(strs[0]) == null) {
				return false;
			}
			Player player = Bukkit.getPlayer(strs[0]);
			String[] args = new String[strs.length - 1];
			for (int i = 0; i < args.length; i++) {
				args[i] = strs[i+1];
			}
			switch (strs[0].toLowerCase()) {
			case "play":
				return BTCommand.play(player, args);
			case "opengui":
				return BTCommand.openGui(player, args);
			case "kickdun": 
				return BTCommand.kickTeamFoDungeon(player, args);
			}
		}
		return false;
	}

}
