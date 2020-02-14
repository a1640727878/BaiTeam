package sky_bai.bukkit.baiteam.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.event.BTEAcceptPlayerApplyEvent;
import sky_bai.bukkit.baiteam.event.BTEAcceptTeamInviteEvent;
import sky_bai.bukkit.baiteam.event.BTECreateTeamEvent;
import sky_bai.bukkit.baiteam.event.BTEDisbandTeamEvent;
import sky_bai.bukkit.baiteam.event.BTEJoinTeamEvent;
import sky_bai.bukkit.baiteam.event.BTEKickPlayerEvent;
import sky_bai.bukkit.baiteam.event.BTELeaveTeamEvent;
import sky_bai.bukkit.baiteam.event.BTEPlayerApplyEvent;
import sky_bai.bukkit.baiteam.event.BTEPromotionalTeamEvent;
import sky_bai.bukkit.baiteam.event.BTETeamInviteEvent;
import sky_bai.bukkit.baiteam.event.BTETransferTeamEvent;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamManager;
import sky_bai.bukkit.baiteam.team.TeamPromotional;

public class BaiTeamListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreateTeam(BTECreateTeamEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		TeamManager tManager = BaiTeam.getTeamManager();
		if (tManager.ifOnTeam(player)) {
			BTMessage.send(player, BTMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		if (tManager.ifTeam(team)) {
			BTMessage.send(player, BTMessage.Error.OnTeamNameIsUse, null);
			return;
		}
		tManager.addTeam(team);
		BTMessage.send(player, BTMessage.Team.Create, Arrays.asList(team.getTeamName(), player.getName()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeaveTeam(BTELeaveTeamEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		if (team.getLeader() == player) {
			Bukkit.getPluginManager().callEvent(new BTEDisbandTeamEvent(team, player));
			return;
		}
		team.delMember(player);
		List<String> list = Arrays.asList(team.getTeamName(), team.getLeader().getName(), player.getName());
		BTMessage.send(player, BTMessage.Team.Leave_OnMember_Member, list);
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(team.getLeader());
		for (Player p : players) {
			BTMessage.send(p, BTMessage.Team.Leave_OnMember_Members, list);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDisbandTeam(BTEDisbandTeamEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		List<String> list = Arrays.asList(team.getTeamName(), player.getName());
		BTMessage.send(player, BTMessage.Team.Leave_OnLeader_Leader, list);
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(player);
		for (Player p : players) {
			BTMessage.send(p, BTMessage.Team.Leave_OnLeader_Member, list);
		}
		BaiTeam.getTeamManager().delTeam(team);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoinTeamr(BTEJoinTeamEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		List<String> list = new ArrayList<String>();
		list.add(team.getTeamName());
		int teamsize = BTConfig.getConfig().getConfig().getInt("TeamSize", 5);
		if (team.getMembers().size() >= teamsize) {
			// BTMessage.send(player, BTMessage.Error.OnTeamIsFull, list);
			return;
		}
		if (BaiTeam.getTeamManager().ifOnTeam(player)) {
			// BTMessage.send(player, BTMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		list.add(team.getLeaderName());
		list.add(player.getName());
		List<Player> players = new ArrayList<Player>(team.getMembers());
		for (Player player2 : players) {
			BTMessage.send(player2, BTMessage.Team.Join_Members, list);
		}
		team.addMembers(player);
		BTMessage.send(player, BTMessage.Team.Join_Member, list);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerApply(BTEPlayerApplyEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		Player leader = team.getLeader();
		if (BaiTeam.getTeamManager().ifOnTeam(player) == true) {
			BTMessage.send(player, BTMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		if (team.getOnJoinPlayer().contains(player)) {
			return;
		}
		List<String> list = Arrays.asList(team.getTeamName(), leader.getName(), player.getName());
		team.addJoinPlayer(player);
		BTMessage.send(player, BTMessage.Team.Join_Apply_ApplyForPlayer, list);
		BTMessage.Action action_1 = BTMessage.Action.setAction(BTMessage.Button.Yes.getMes(), "/baiteam Apply Yes " + player.getName(), BTMessage.Button.Text_Apply_Yes.getMes());
		BTMessage.Action action_2 = BTMessage.Action.setAction(BTMessage.Button.NO.getMes(), "/baiteam Apply No " + player.getName(), BTMessage.Button.Text_Apply_No.getMes());
		BTMessage.send(leader, BTMessage.Team.Join_Apply_ApplyForLeader, list, action_1, action_2);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAcceptPlayer(BTEAcceptPlayerApplyEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		Boolean isApply = event.getIsApply();
		List<String> list = Arrays.asList(team.getTeamName(), team.getLeaderName(), player.getName());
		if (team.getOnJoinPlayer().contains(player) == false) {
			BTMessage.send(team.getLeader(), BTMessage.Team.Join_Apply_PlayerNoApply, list);
			return;
		}
		team.deJoinPlayer(player);
		if (isApply) {
			BTMessage.send(player, BTMessage.Team.Join_Apply_Yes, list);
			BTMessage.send(team.getLeader(), BTMessage.Team.Join_Apply_YesFoLeader, list);
			Bukkit.getPluginManager().callEvent(new BTEJoinTeamEvent(team, player));
		} else {
			BTMessage.send(player, BTMessage.Team.Join_Apply_No, list);
			BTMessage.send(team.getLeader(), BTMessage.Team.Join_Apply_NoFoLeader, list);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeamInvite(BTETeamInviteEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		Player leader = team.getLeader();
		List<String> list = Arrays.asList(team.getTeamName(), leader.getName(), player.getName());
		if (BaiTeam.getTeamManager().ifOnTeam(player) == true) {
			BTMessage.send(player, BTMessage.Error.OnPlayerInTeam, list);
			return;
		}
		team.addInvitePlayer(player);
		BTMessage.send(leader, BTMessage.Team.Join_Invite_InviteForLeader, list);
		BTMessage.Action action_1 = BTMessage.Action.setAction(BTMessage.Button.Yes.getMes(), "/baiteam Invite Yes " + team.getTeamName(), BTMessage.Button.Text_Invite_Yes.getMes());
		BTMessage.Action action_2 = BTMessage.Action.setAction(BTMessage.Button.NO.getMes(), "/baiteam Invite No " + team.getTeamName(), BTMessage.Button.Text_Invite_No.getMes());
		BTMessage.send(player, BTMessage.Team.Join_Invite_InviteForPlayer, list, action_1, action_2);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAcceptTeam(BTEAcceptTeamInviteEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		Boolean isApply = event.getIsApply();
		List<String> list = Arrays.asList(team.getTeamName(), team.getLeaderName(), player.getName());
		if (team.getOnInvitePlayer().contains(player) == false) {
			BTMessage.send(player, BTMessage.Team.Join_Invite_TeamNoInvite, list);
			return;
		}
		team.delInvitePlayer(player);
		if (BaiTeam.getTeamManager().ifOnTeam(player) == true) {
			// BTMessage.send(player, BTMessage.Error.OnPlayerOnTeam, list);
			return;
		}
		if (isApply) {
			BTMessage.send(player, BTMessage.Team.Join_Invite_YesFoMember, list);
			BTMessage.send(team.getLeader(), BTMessage.Team.Join_Invite_Yes, list);
			Bukkit.getPluginManager().callEvent(new BTEJoinTeamEvent(team, player));
		} else {
			BTMessage.send(player, BTMessage.Team.Join_Invite_NoFoMember, list);
			BTMessage.send(team.getLeader(), BTMessage.Team.Join_Invite_No, list);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTransferTeam(BTETransferTeamEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		Player old_leader = team.getLeader();
		List<String> list = Arrays.asList(team.getTeamName(), old_leader.getName(), player.getName());
		BTMessage.send(old_leader, BTMessage.Team.Transfer_OnLeader, list);
		BTMessage.send(player, BTMessage.Team.Transfer_OnPlayer, list);
		team.setLeader(player);
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(player);
		players.remove(old_leader);
		for (Player player2 : players) {
			BTMessage.send(player2, BTMessage.Team.Transfer_OnMembers, list);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKickPlayer(BTEKickPlayerEvent event) {
		Team team = event.getTeam();
		Player player = event.getPlayer();
		Player leader = team.getLeader();
		if (player == team.getLeader()) {
			BTMessage.send(player, BTMessage.Error.OnPlayerKickMe, null);
			return;
		}
		List<String> list = Arrays.asList(team.getTeamName(), team.getLeaderName(), player.getName());
		BTMessage.send(player, BTMessage.Team.Kick_OnPlayer, list);
		BTMessage.send(leader, BTMessage.Team.Kick_OnLeader, list);
		team.delMember(player);
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(leader);
		for (Player player2 : players) {
			BTMessage.send(player2, BTMessage.Team.Kick_OnMembers, list);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPromotionalTeam(BTEPromotionalTeamEvent event) {
		Team team = event.getTeam();
		Player leader = team.getLeader();
		List<String> list = Arrays.asList(team.getTeamName(),leader.getName());
		BTMessage.Action action_1 = BTMessage.Action.setAction(BTMessage.Button.ApplyTo.getMes(), "/baiteam ApplyTo " + team.getTeamName(), BTMessage.Button.Text_ApplyTo.getMes());
		BTMessage.broadcast(BTMessage.Team.Promotional, list, action_1);
		TeamPromotional.PromotionalTime.put(team, System.currentTimeMillis());
	}
	
}
