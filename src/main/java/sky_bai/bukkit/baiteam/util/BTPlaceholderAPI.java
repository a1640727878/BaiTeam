package sky_bai.bukkit.baiteam.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamManager;

public class BTPlaceholderAPI extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "sky_bai";
	}

	@Override
	public String getIdentifier() {
		return "baiteam";
	}

	@Override
	public String getVersion() {
		return "0.1.0";
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		TeamManager tManager = BaiTeam.getTeamManager();
		Team team = null;
		if (BaiTeam.getTeamManager().ifOnTeam(player)) {
			team = tManager.getTeam(player, false);
		}
		if (identifier.contains("{") && identifier.contains("}")) {
			int i = identifier.indexOf("{");
			String str1 = identifier.substring(i);
			str1 = str1.replace("{", "%").replace("}", "%");
			str1 = PlaceholderAPI.setPlaceholders(player, str1);
			identifier = identifier.substring(0, i) + str1;
		}
		if (identifier.startsWith("players")) {
			List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
			List<String> playersName = new ArrayList<String>();
			for (Player player2 : players) {
				if (BaiTeam.getTeamManager().ifOnTeam(player2) == false) {
					playersName.add(player2.getName());
				}
			}
			if (identifier.startsWith("players_amount")) {
				return String.valueOf(playersName.size());
			}
			String str1 = identifier.replace("players", "");
			if (str1.isEmpty() == false) {
				int i = Integer.valueOf(str1.substring(1));
				return playersName.get(i);
			}
			return playersName.toString();
		} else if (identifier.startsWith("player_name")) {
			return player.getName();
		} else if (identifier.startsWith("onteam")) {
			return team == null ? "false" : "true";
		} else if (identifier.startsWith("teams_amount")) {
			return String.valueOf(tManager.getTeamNames().size());
		} else if (identifier.startsWith("teams")) {
			String str1 = identifier.replace("teams", "");
			if (str1.isEmpty() == false) {
				int i = Integer.valueOf(str1.substring(1));
				return tManager.getTeamNames().get(i);
			}
			return tManager.getTeamNames().toString();
		} else if (identifier.startsWith("team_name")) {
			String str1 = identifier.replace("team_name", "");
			if (str1.isEmpty() == false) {
				return tManager.getTeam(str1.substring(1)).getTeamName();
			}
			return team.getTeamName();
		} else if (identifier.startsWith("team_leader")) {
			String str1 = identifier.replace("team_leader", "");
			if (str1.isEmpty() == false) {
				return tManager.getTeam(str1.substring(1)).getLeaderName();
			}
			return team.getLeaderName();
		} else if (identifier.startsWith("team_onleader")) {
			return (team == null || team.getLeader() != player) ? "false" : "true";
		} else if (identifier.startsWith("team_members_amount")) {
			String str1 = identifier.replace("team_members_amount", "");
			if (str1.isEmpty() == false) {
				return String.valueOf(tManager.getTeam(str1.substring(1)).getMembers().size());
			}
			return String.valueOf(team.getMembers().size());
		} else if (identifier.startsWith("team_members")) {
			String str1 = identifier.replace("team_members", "");
			if (str1.isEmpty() == false) {
				return tManager.getTeam(str1.substring(1)).getMemberNames().toString();
			}
			return team.getMemberNames().toString();
		}
		return null;
	}

}
