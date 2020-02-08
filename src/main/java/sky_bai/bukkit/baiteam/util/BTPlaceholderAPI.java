package sky_bai.bukkit.baiteam.util;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.team.Team;

public class BTPlaceholderAPI extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "sky_bai";
	}

	@Override
	public String getIdentifier() {
		return "BaiTeam";
	}

	@Override
	public String getVersion() {
		return "0.1.0";
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		Team team = null;
		if (BaiTeam.getTeamManager().ifOnTeam(player)) {
			team = BaiTeam.getTeamManager().getTeam(player, false);
		}
		switch (identifier) {
		case "IfOnTeam": {
			return BaiTeam.getTeamManager().ifOnTeam(player) ? "true" : "false";
		}
		}
		if (team != null) {
			switch (identifier) {
			case "Name": {
				return team.getTeamName();
			}
			case "Leader": {
				return team.getLeader().getName();
			}
			case "IfLeader": {
				return team.getLeader() == player ? "true" : "false";
			}
			case "Members": {
				String str = team.getMembers().toString();
				return str.substring(1, str.length() - 1);
			}
			case "Amount": {
				return team.getMembers().size() + "";
			}
			}
		} else {
			return "null";
		}
		return null;
	}

}
