package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTLeaveTeamEvent extends BaiTeamEvent {

	public BTLeaveTeamEvent(Team team, Player player) {
		super(team, player);

	}

	public static void run(Team team,Player player) {
		BTLeaveTeamEvent btLeaveTeamEvent = new BTLeaveTeamEvent(team, player);
		if (team.getLeader() == player) {
			BTDissolveTeamEvent.run(player);
			return;
		}
		Bukkit.getPluginManager().callEvent(btLeaveTeamEvent);
		if (btLeaveTeamEvent.isCancelled() == false) {
			btLeaveTeamEvent.run();
		}
	}

	private void run() {
		getTeam().delMember(getPlayer());
		List<String> list = Arrays.asList(getTeam().getTeamName(), getTeam().getLeader().getName(), getPlayer().getName());
		BTMessage.send(getPlayer(), BTMessage.Team.Leave_OnMember_Member, list);
		Set<Player> players = getTeam().getMembers();
		for (Player player : players) {
			BTMessage.send(player, BTMessage.Team.Leave_OnMember_Members, list);
		}
		if (getTeam().getLeader() == getPlayer()) {
			BaiTeam.getTeamManager().delTeam(getTeam());
		}
	}

}
