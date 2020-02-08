package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;

public class BTAcceptPlayerApplyEvent extends BaiTeamEvent {

	private Boolean isApply;

	public BTAcceptPlayerApplyEvent(Team team, Player player, Boolean isApply) {
		super(team, player);
		this.isApply = isApply;
	}

	public Boolean getIsApply() {
		return isApply;
	}

	public void setIsApply(Boolean isApply) {
		this.isApply = isApply;
	}

	public static void run(Team team, Player player, Boolean isApply) {
		BTAcceptPlayerApplyEvent bAcceptPlayerApplyEvent = new BTAcceptPlayerApplyEvent(team, player, isApply);
		Bukkit.getPluginManager().callEvent(bAcceptPlayerApplyEvent);
		if (bAcceptPlayerApplyEvent.isCancelled() == false) {
			bAcceptPlayerApplyEvent.run();
		}
	}

	private void run() {
		List<String> list = Arrays.asList(getTeam().getTeamName(), getTeam().getLeader().getName(), getPlayer().getName());
		if (getTeam().getOnJoinPlayer().contains(getPlayer()) == false) {
			BTMessage.send(getTeam().getLeader(), BTMessage.Team.Join_Apply_PlayerNoApply, list);
			return;
		}
		getTeam().deJoinPlayer(getPlayer());
		if (getIsApply()) {
			BTJoinTeamEvent.run(getTeam(), getPlayer());
			BTMessage.send(getPlayer(), BTMessage.Team.Join_Apply_Yes, list);
			BTMessage.send(getTeam().getLeader(), BTMessage.Team.Join_Apply_YesFoLeader, list);
			return;
		}
		BTMessage.send(getPlayer(), BTMessage.Team.Join_Apply_No, list);
		BTMessage.send(getTeam().getLeader(), BTMessage.Team.Join_Apply_NoFoLeader, list);
	}

}
