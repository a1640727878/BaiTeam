package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTDissolveTeamEvent extends BaiTeamEvent {

	public BTDissolveTeamEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Player player) {
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		BTDissolveTeamEvent bDissolveTeamEvent = new BTDissolveTeamEvent(team, player);
		if (bDissolveTeamEvent.isCancelled() == false) {
			bDissolveTeamEvent.run();
		}
	}

	private void run() {
		Set<Player> players = getTeam().getMembers();
		players.remove(getPlayer());
		List<String> list = Arrays.asList(getTeam().getTeamName(), getPlayer().getName());
		BaiTeam.getTeamManager().delTeam(getTeam());
		BTMessage.send(getPlayer(), BTMessage.Team.Leave_OnLeader_Leader, list);
		for (Player player : players) {
			BTMessage.send(player, BTMessage.Team.Leave_OnLeader_Member, list);
		}
	}

}
