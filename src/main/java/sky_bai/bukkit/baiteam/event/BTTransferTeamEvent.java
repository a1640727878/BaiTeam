package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTTransferTeamEvent extends BaiTeamEvent {

	public BTTransferTeamEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Team team, Player player) {
		BTTransferTeamEvent bTransferTeamEvent = new BTTransferTeamEvent(team, player);
		Bukkit.getPluginManager().callEvent(bTransferTeamEvent);
		if (bTransferTeamEvent.isCancelled() == false) {
			bTransferTeamEvent.run();
		}
	}

	private void run() {
		Player leader = getTeam().getLeader();
		getTeam().setLeader(getPlayer());
		List<String> list = Arrays.asList(getTeam().getTeamName(), leader.getName(), getPlayer().getName());
		BTMessage.send(leader, BTMessage.Team.Transfer_OnLeader, list);
		BTMessage.send(getPlayer(), BTMessage.Team.Transfer_OnPlayer, list);
		Set<Player> players = getTeam().getMembers();
		players.remove(getPlayer());
		players.remove(leader);
		for (Player player : players) {
			BTMessage.send(player, BTMessage.Team.Transfer_OnMembers, list);
		}
	}

}
