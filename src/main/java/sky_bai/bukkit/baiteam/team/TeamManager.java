package sky_bai.bukkit.baiteam.team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

public class TeamManager {
	private final Set<Team> teams = new HashSet<Team>();
	
	public void reset() {
		teams.clear();
	}
	
	public Set<Team> getTeams() {
		Set<Team> teams = this.teams;
		return teams;
	}
	
	public Team getTeam(String name) {
		for (Team team : teams) {
			if (team.getTeamName().equalsIgnoreCase(name)) {
				return team;
			}
		}
		return null;
	}
	
	public Team getTeam(Player player, Boolean onLeader) {
		for (Team team : teams) {
			if ((onLeader == true && team.getLeader() == player) || (onLeader == false && team.getMembers().contains(player))) {
				return team;
			}
		}
		return null;
	}
	
	public List<String> getTeamNames() {
		List<String> names = new ArrayList<String>();
		for (Team team : teams) {
			names.add(team.getTeamName());
		}
		return names;
	}
	
	public boolean ifTeam(String str) {
		if (getTeam(str) != null) {
			return true;
		}
		return false;
	}
	
	public boolean ifTeam(Team team) {
		if (getTeams().contains(team)) {
			return true;
		}
		return false;
	}

	public boolean ifOnTeam(Player player) {
		if (getTeam(player, false) != null) {
			return true;
		}
		return false;
	}

	public void addTeam(Team team) {
		teams.add(team);
	}

	public void delTeam(Team team) {
		teams.remove(team);
	}
}
