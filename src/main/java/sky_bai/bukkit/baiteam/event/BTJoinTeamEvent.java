package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.team.Team;

public class BTJoinTeamEvent extends BaiTeamEvent {

	public BTJoinTeamEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Team team, Player player) {
		BTJoinTeamEvent bJoinTeamEvent = new BTJoinTeamEvent(team, player);
		Bukkit.getPluginManager().callEvent(bJoinTeamEvent);
		if (bJoinTeamEvent.isCancelled() == false) {
			bJoinTeamEvent.run();
		}
	}

	private void run() {
		List<String> list = Arrays.asList(getTeam().getTeamName());
		if (BTConfig.getConfig().getConfig().getInt("TeamSize", 5) <= getTeam().getMembers().size()) {
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnTeamIsFull, list);
			return;
		}
		if (BaiTeam.getTeamManager().ifOnTeam(getPlayer())) {
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		Set<Player> players = getTeam().getMembers();
		getTeam().addMembers(getPlayer());
		list.add(getTeam().getLeader().getName());
		list.add(getPlayer().getName());
		for (Player player : players) {
			BaiMessage.send(player, BaiMessage.TeamMesEnum.Join_Members, list);
		}
		BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Join_Member, list);
	}

}
