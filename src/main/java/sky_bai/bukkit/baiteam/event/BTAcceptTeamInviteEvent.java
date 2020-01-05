package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.team.Team;

public class BTAcceptTeamInviteEvent extends BaiTeamEvent {

	private Boolean isApply;

	public BTAcceptTeamInviteEvent(Team team, Player player, Boolean isApply) {
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
		BTAcceptTeamInviteEvent bAcceptTeamInviteEvent = new BTAcceptTeamInviteEvent(team, player, isApply);
		Bukkit.getPluginManager().callEvent(bAcceptTeamInviteEvent);
		if (bAcceptTeamInviteEvent.isCancelled() == false) {
			bAcceptTeamInviteEvent.run();
		}
	}

	private void run() {
		List<String> list = Arrays.asList(getTeam().getTeamName(), getTeam().getLeader().getName(), getPlayer().getName());
		if (getTeam().getOnInvitePlayer().contains(getPlayer()) == false) {
			BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Join_Invite_TeamNoInvite, list);
			return;
		}
		getTeam().delOnInvitePlayer(getPlayer());
		if (BaiTeam.getTeamManager().ifOnTeam(getPlayer()) == true) {
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnPlayerOnTeam, list);
			return;
		}
		if (getIsApply()) {
			BTJoinTeamEvent.run(getTeam(), getPlayer());
			BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Join_Invite_YesFoMember, list);
			BaiMessage.send(getTeam().getLeader(), BaiMessage.TeamMesEnum.Join_Invite_Yes, list);
			return;
		}
		BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Join_Invite_NoFoMember, list);
		BaiMessage.send(getTeam().getLeader(), BaiMessage.TeamMesEnum.Join_Invite_No, list);
	}

}
