package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTPromotionalTeamEvent extends BaiTeamEvent {

	public BTPromotionalTeamEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Team team) {
		BTPromotionalTeamEvent bPromotionalTeamEvent = new BTPromotionalTeamEvent(team, null);
		Bukkit.getPluginManager().callEvent(bPromotionalTeamEvent);
		if (bPromotionalTeamEvent.isCancelled() == false) {
			bPromotionalTeamEvent.run();
		}
	}

	private void run() {
		List<String> list = Arrays.asList(getTeam().getTeamName(),getTeam().getLeader().getName());
		BaiMessage.Action action_1 = BaiMessage.Action.setAction(BaiMessage.Button.ApplyTo.getMes(), "/baiteam ApplyTo " + getTeam().getTeamName(), BaiMessage.Button.Text_ApplyTo.getMes());
		BaiMessage.broadcast(BaiMessage.TeamMesEnum.Promotional, list, action_1);
	}

}
