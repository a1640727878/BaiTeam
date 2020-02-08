package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.message.BTMessage;
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
			BTMessage.send(getPlayer(), BTMessage.Error.OnPlayerInTeam, list);
			return;
		}
		getTeam().addInvitePlayer(getPlayer());
		BTMessage.send(getLeader(), BTMessage.Team.Join_Invite_InviteForLeader, list);
		BTMessage.Action action_1 = BTMessage.Action.setAction(BTMessage.Button.Yes.getMes(), "/baiteam Invite Yes " + getTeam().getTeamName(), BTMessage.Button.Text_Invite_Yes.getMes());
		BTMessage.Action action_2 = BTMessage.Action.setAction(BTMessage.Button.NO.getMes(), "/baiteam Invite No " + getTeam().getTeamName(), BTMessage.Button.Text_Invite_No.getMes());
		BTMessage.send(getPlayer(), BTMessage.Team.Join_Invite_InviteForPlayer, list, action_1, action_2);
	}
}
