package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.team.Team;

public class BTTeamInviteEvent extends BaiTeamEvent {

	private Player leader;

	public BTTeamInviteEvent(Team team, Player player, Player leader) {
		super(team, player);
		this.leader = leader;
	}

	public void setLeader(Player leader) {
		this.leader = leader;
	}

	public Player getLeader() {
		return leader;
	}

	public static void run(Team team, Player player, Player leader) {
		BTTeamInviteEvent bTeamInviteEvent = new BTTeamInviteEvent(team, player, leader);
		Bukkit.getPluginManager().callEvent(bTeamInviteEvent);
		if (bTeamInviteEvent.isCancelled() == false) {
			bTeamInviteEvent.run();
		}
	}

	private void run() {
		List<String> list = Arrays.asList(getTeam().getTeamName(), getLeader().getName(), getPlayer().getName());
		if (BaiTeam.getTeamManager().ifOnTeam(getPlayer()) == true) {
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnPlayerInTeam, list);
			return;
		}
		getTeam().addOnInvitePlayer(getPlayer());
		BaiMessage.send(getLeader(), BaiMessage.TeamMesEnum.Join_Invite_InviteForLeader, list);
		BaiMessage.Action action_1 = BaiMessage.Action.setAction(BaiMessage.Button.Yes.getMes(), "/baiteam Invite Yes " + getTeam().getTeamName(), BaiMessage.Button.Text_Invite_Yes.getMes());
		BaiMessage.Action action_2 = BaiMessage.Action.setAction(BaiMessage.Button.NO.getMes(), "/baiteam Invite No " + getTeam().getTeamName(), BaiMessage.Button.Text_Invite_No.getMes());
		BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Join_Invite_InviteForPlayer, list, action_1, action_2);
	}
}
