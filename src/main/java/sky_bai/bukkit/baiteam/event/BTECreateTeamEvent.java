package sky_bai.bukkit.baiteam.event;

import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.team.Team;

public class BTECreateTeamEvent extends BaiTeamEvent{

	public BTECreateTeamEvent(Team team, Player player) {
		super(team, player);
	}

}
