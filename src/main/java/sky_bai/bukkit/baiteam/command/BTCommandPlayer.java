package sky_bai.bukkit.baiteam.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiTeam;

public class BTCommandPlayer implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player == false) {
			BaiTeam.getInstance().getLogger().info("这些命令只能玩家执行");
			return false;
		}
		Player player = (Player) sender;
		if (args.length > 0) {
			String str1 = args[0].toLowerCase();
			switch (str1) {
			case "teleport": {
				return BTCommand.teleport(player, args);
			}
			case "play": {
				return BTCommand.play(player, args);
			}
			case "opengui": {
				return BTCommand.openGui(player, args);
			}
			case "create": {
				return BTCommand.create(player, args);
			}
			case "leave": {
				return BTCommand.leave(player, args);
			}
			case "applyto": {
				return BTCommand.applyto(player, args);
			}
			case "inviteto": {
				return BTCommand.inviteto(player, args);
			}
			case "transfer": {
				return BTCommand.transfer(player, args);
			}
			case "guitransfer": {
				return BTCommand.guitransfer(player, args);
			}
			case "kick": {
				return BTCommand.kick(player, args);
			}
			case "guikick": {
				return BTCommand.guikick(player, args);
			}
			case "apply": {
				return BTCommand.apply(player, args);
			}
			case "invite": {
				return BTCommand.invite(player, args);
			}
			case "promotional": {
				return BTCommand.promotional(player, args);
			}
			case "guipromotional": {
				return BTCommand.guipromotional(player, args);
			}
			}
		}
		return false;
	}

}
