package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTKickPlayerEvent extends BaiTeamEvent {

	public BTKickPlayerEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Team team, Player player) {
		BTKickPlayerEvent bKickPlayerEvent = new BTKickPlayerEvent(team, player);
		Bukkit.getPluginManager().callEvent(bKickPlayerEvent);
		if (bKickPlayerEvent.isCancelled() == false) {
			bKickPlayerEvent.run();
		}
	}

	private void run() {
		if (getPlayer() == getTeam().getLeader()) {
			BTMessage.send(getPlayer(), BTMessage.Error.OnPlayerKickMe, null);
			return;
		}
		getTeam().delMember(getPlayer());
		List<String> list = Arrays.asList(getTeam().getTeamName(), getTeam().getLeader().getName(), getPlayer().getName());
		BTMessage.send(getPlayer(), BTMessage.Team.Kick_OnPlayer, list);
		BTMessage.send(getTeam().getLeader(), BTMessage.Team.Kick_OnLeader, list);
		Set<Player> players = getTeam().getMembers();
		for (Player player : players) {
			BTMessage.send(player, BTMessage.Team.Kick_OnMembers, list);
		}
	}

}
