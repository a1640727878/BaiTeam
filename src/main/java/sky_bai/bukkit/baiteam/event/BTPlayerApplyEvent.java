package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTPlayerApplyEvent extends BaiTeamEvent {

	public BTPlayerApplyEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Team team, Player player) {
		BTPlayerApplyEvent bPlayerApplyEvent = new BTPlayerApplyEvent(team, player);
		Bukkit.getPluginManager().callEvent(bPlayerApplyEvent);
		if (bPlayerApplyEvent.isCancelled() == false) {
			bPlayerApplyEvent.run();
		}
	}

	private void run() {
		if (BaiTeam.getTeamManager().ifOnTeam(getPlayer()) == true) {
			BTMessage.send(getPlayer(), BTMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		getTeam().addJoinPlayer(getPlayer());
		List<String> list = Arrays.asList(getTeam().getTeamName(), getTeam().getLeader().getName(), getPlayer().getName());
		BTMessage.send(getPlayer(), BTMessage.Team.Join_Apply_ApplyForPlayer, list);
		BTMessage.Action action_1 = BTMessage.Action.setAction(BTMessage.Button.Yes.getMes(), "/baiteam Apply Yes " + getPlayer().getName(), BTMessage.Button.Text_Apply_Yes.getMes());
		BTMessage.Action action_2 = BTMessage.Action.setAction(BTMessage.Button.NO.getMes(), "/baiteam Apply No " + getPlayer().getName(), BTMessage.Button.Text_Apply_No.getMes());
		BTMessage.send(getTeam().getLeader(), BTMessage.Team.Join_Apply_ApplyForLeader, list, action_1, action_2);
	}

}
