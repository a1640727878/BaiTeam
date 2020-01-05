package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.BaiTeam;
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
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		getTeam().addOnJoinPlayer(getPlayer());
		List<String> list = Arrays.asList(getTeam().getTeamName(), getTeam().getLeader().getName(), getPlayer().getName());
		BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Join_Apply_ApplyForPlayer, list);
		BaiMessage.Action action_1 = BaiMessage.Action.setAction(BaiMessage.Button.Yes.getMes(), "/baiteam Apply Yes " + getPlayer().getName(), BaiMessage.Button.Text_Apply_Yes.getMes());
		BaiMessage.Action action_2 = BaiMessage.Action.setAction(BaiMessage.Button.NO.getMes(), "/baiteam Apply No " + getPlayer().getName(), BaiMessage.Button.Text_Apply_No.getMes());
		BaiMessage.send(getTeam().getLeader(), BaiMessage.TeamMesEnum.Join_Apply_ApplyForLeader, list, action_1, action_2);
	}

}
