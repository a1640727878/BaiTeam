package sky_bai.bukkit.baiteam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import sky_bai.bukkit.baiteam.book.TeamGui;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.team.Team;

public class BaiCommandCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 1) {
			Player player = (Bukkit.getPlayer(args[0]) != null) ? Bukkit.getPlayer(args[1]) : null;
			switch (args[1].toLowerCase()) {
			case "play":
				return play(player, args);
			case "opengui":
				return openGui(player, args);
			}
		}
		return false;
	}

	private boolean play(Player player, String[] args) {
		DungeonsXL dungeonsXL = DungeonsXL.getInstance();
		if (args.length <= 2) {
			return false;
		}
		if (dungeonsXL.getDWorldCache().getResourceByName(args[2]) == null || BaiTeam.getTeamManager().getTeam(player, true) == null || dungeonsXL.getDPlayerCache().getByPlayer(player) instanceof DInstancePlayer) {
			return false;
		}
		DResourceWorld resource = dungeonsXL.getDWorldCache().getResourceByName(args[2]);
		Dungeon dungeon = new Dungeon(dungeonsXL, resource);
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		List<Player> members = new ArrayList<Player>(team.getMembers());
		members.remove(player);
		DGroup group = new DGroup(dungeonsXL, team.getTeamName(), player, members, dungeon);
		DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
		new Game(dungeonsXL, group, gameWorld);
		for (Player groupPlayer : group.getPlayers().getOnlinePlayers()) {
			new DGamePlayer(dungeonsXL, groupPlayer, group.getGameWorld());
		}
		return true;
	}
	
	private boolean openGui(Player player, String[] args) {
		if (args.length <= 2) {
			if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
				TeamGui.getGui().openMainGui(player);
			} else {
				TeamGui.getGui().openTeamInfoGui(player, BaiTeam.getTeamManager().getTeam(player, false));
			}
			return true;
		}
		switch (args[2].toLowerCase()) {
		case "main": {
			TeamGui.getGui().openMainGui(player);
			return true;
		}
		case "teaminfo": {
			if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
				return false;
			}
			TeamGui.getGui().openTeamInfoGui(player, BaiTeam.getTeamManager().getTeam(player, false));
			return true;
		}
		case "teamlist": {
			if (BaiTeam.getTeamManager().ifOnTeam(player)) {
				return false;
			}
			if (BaiTeam.getTeamManager().getTeams().isEmpty()) {
				TeamGui.getGui().openMainGui(player);
				return false;
			}
			int i1 = 0;
			if (args.length >= 5 && Integer.valueOf(args[4]) != null) {
				i1 = Integer.valueOf(args[4]);
			}
			List<Team> teams = new ArrayList<Team>();
			for (Team team : BaiTeam.getTeamManager().getTeams()) {
				if (team.getMembers().size() < BTConfig.getConfig().getConfig().getInt("TeamSize", 5)) {
					teams.add(team);
				}
			}
			if (teams.isEmpty()) {
				TeamGui.getGui().openMainGui(player);
				return false;
			}
			TeamGui.getGui().openTeamListGui(player, teams, i1);
			return true;
		}
		case "playerlist": {
			if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
				return false;
			}
			Team team = BaiTeam.getTeamManager().getTeam(player, true);
			if (team == null) {
				return false;
			}
			int i1 = 0;
			if (args.length >= 5 && Integer.valueOf(args[4]) != null) {
				i1 = Integer.valueOf(args[4]);
			}
			List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
			List<Player> players2 = new ArrayList<Player>();
			for (OfflinePlayer offlinePlayer : players) {
				if (offlinePlayer.isOnline() && BaiTeam.getTeamManager().ifOnTeam((Player) offlinePlayer) == false) {
					players2.add((Player) offlinePlayer);
				}
			}
			if (players2.isEmpty()) {
				TeamGui.getGui().openTeamInfoGui(player, team);
				return false;
			}
			TeamGui.getGui().openPlayerListGui(player, players2, i1);
			return true;
		}
		}
		return false;

	}
}
