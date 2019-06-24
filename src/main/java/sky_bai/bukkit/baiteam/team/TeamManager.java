package sky_bai.bukkit.baiteam.team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.ConfigType;
import sky_bai.bukkit.baiteam.config.MessageConfig;
import sky_bai.bukkit.baiteam.gui.TeamGui;

public class TeamManager {

	private static final Set<Team> TEAMS = new HashSet<Team>();

	public void reset() {
		TEAMS.clear();
	}

	public static Set<Team> getTeams() {
		return TEAMS;
	}

	public static Team getTeam(String teamName) {
		for (Team team : TEAMS) {
			if (team.getTeamName().equalsIgnoreCase(teamName)) {
				return team;
			}
		}
		return null;
	}

	public static Team getTeam(Player player, Boolean onLeader) {
		for (Team team : TEAMS) {
			if (onLeader && team.getLeader() == player) {
				return team;
			}
			if (!onLeader && team.getMembers().contains(player)) {
				return team;
			}
		}
		return null;
	}

	public static List<String> getTeamNames() {
		List<String> a1 = Arrays.asList();
		for (Team team : TEAMS) {
			a1.add(team.getTeamName());
		}
		return a1;
	}

	// 创建队伍
	public static void createTeam(Player player, String teamName) {
		if (getTeam(player, false) != null) {
			playerSendMessage(player, "Team.Error.onPlayerIsTeam", "你已经在一个队伍中了");
			return;
		}
		Team team = new Team(player, teamName);
		TEAMS.add(team);
		playerSendMessage(player, "Team.Create", "你成功的创建了队伍 [&{0}]", team.getTeamName(), team.getLeader().getName());
		TeamGui.getGui().openTeamInfoGui(team, player);
	}

	// 离开或解散队伍
	public static void leaveTeam(Player player) {
		if (getTeam(player, false) == null) {
			playerSendMessage(player, "Team.Error.onPlayerNoTeam", "你不在任何一个队伍内");
		}
		Team team = getTeam(player, false);
		if (team.getLeader() == player) {
			dissolveTeam(player);
			return;
		}
		team.delMember(player);
		List<Player> players = new ArrayList<Player>(team.getMembers());
		for (Player player2 : players) {
			playerSendMessage(player2, "Team.Leave.OnMember.Members", "&{2} 离开了队伍", team.getTeamName(), team.getLeader().getName(), player.getName());
		}
		playerSendMessage(player, "Team.Leave.OnMember.Member", "你离开了队伍 [&{0}]", team.getTeamName(), team.getLeader().getName(), player.getName());
	}

	// 解散队伍
	public static void dissolveTeam(Player leader) {
		if (getTeam(leader, true) == null) {
			playerSendMessage(leader, "Team.Error.onPlayerNoLeader", "你不是一个队伍的队长");
			return;
		}
		Team team = getTeam(leader, true);
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(leader);
		TEAMS.remove(team);
		for (Player player2 : players) {
			playerSendMessage(player2, "Team.Leave.OnLeader.Member", "&{1} 解散了队伍", team.getTeamName(), leader.getName());
		}
		playerSendMessage(leader, "Team.Leave.OnLeader.Leader", "你解散了队伍 [&{0}]", team.getTeamName(), leader.getName());
	}

	// 转让队伍
	public static void transferTeam(Player leader, Player player) {
		if (getTeam(leader, true) == null) {
			playerSendMessage(leader, "Team.Error.onPlayerNoLeader", "你不是一个队伍的队长");
			return;
		}
		Team team = getTeam(leader, true);
		if (team.getMembers().contains(player)) {
			return;
		}
		team.setLeader(player);
		playerSendMessage(leader, "Team.Transfer.OnLeader", "你把队伍 [&{0}] 转让给了 &{2}", team.getTeamName(), leader.getName(), player.getName());
		playerSendMessage(player, "Team.Transfer.OnPlayer", "&{1} 把队伍 [&{0}] 转让给了你", team.getTeamName(), leader.getName(), player.getName());
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(player);
		players.remove(leader);
		for (Player player2 : players) {
			playerSendMessage(player2, "Team.Transfer.OnMembers", "&{1} 把队伍 [&{0}] 转让给了 &{2}", team.getTeamName(), leader.getName(), player.getName());
		}
		TeamGui.getGui().openTeamInfoGui(team, leader);
		TeamGui.getGui().openTeamInfoGui(team, player);
	}

	// 加入队伍
	public static void joinTeam(Player player, Team team) {
		if (BaiTeam.getConfig(ConfigType.Config).getInt("TeamSize", 5) < team.getMembers().size()) {
			playerSendMessage(player, "Team.Error.onTeamIsFull", "队伍 [&{0}] 已满", team.getTeamName());
			return;
		}
		if (getTeam(player, false) != null) {
			playerSendMessage(player, "Team.Error.onPlayerIsTeam", "你已经在一个队伍中了");
			return;
		}
		List<Player> players = new ArrayList<Player>(team.getMembers());
		team.addMember(player);
		for (Player player2 : players) {
			playerSendMessage(player2, "Team.Join.Members", "&{2} 加入了队伍", team.getTeamName(), team.getLeader().getName(), player.getName());
		}
		playerSendMessage(player, "Team.Join.Member", "你加入了队伍 [&{0}]", team.getTeamName(), team.getLeader().getName(), player.getName());
		TeamGui.getGui().openTeamInfoGui(team, player);
	}

	// 申请入队
	public static void playerApply(Player player, Team team) {
		if (getTeam(player, false) != null) {
			playerSendMessage(player, "Team.Error.onPlayerIsTeam", "你已经在一个队伍中了");
			return;
		}
		team.addJoinPlayer(player);
		Player leader = team.getLeader();
		playerSendMessage(player, "Team.Join.Apply.ApplyForPlayer", "你已经向队伍 [&{0}] 提交了申请 ", team.getTeamName(), leader.getName(), player.getName());
		TextComponent a1 = new TextComponent(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString("Team.Join.Apply.ApplyForLeader", "&{2} 申请加入你的队伍"), team.getTeamName(), leader.getName(), player.getName()));
		TextComponent a2 = new TextComponent("[§a同意§r] ");
		a2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 同意申请 " + player.getName()));
		a2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击同意玩家加入").create()));
		a1.addExtra(a2);
		TextComponent a3 = new TextComponent("[§c拒绝§r] ");
		a3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 拒绝申请 " + player.getName()));
		a3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击拒绝玩家加入").create()));
		a1.addExtra(a3);
		leader.spigot().sendMessage(ChatMessageType.CHAT, a1);
	}

	// 邀请玩家
	public static void teamInvite(Player leader, Player player) {
		if (getTeam(leader, true) == null) {
			playerSendMessage(leader, "Team.Error.onPlayerNoLeader", "你不是一个队伍的队长");
			return;
		}
		Team team = getTeam(leader, true);
		if (getTeam(player, false) != null) {
			playerSendMessage(leader, "Team.Error.onPlayerIsTeamForLeader", "&{2} 已经在一个队伍中了", team.getTeamName(), leader.getName(), player.getName());
			return;
		}
		team.addInvitePlayer(player);
		TextComponent a1 = new TextComponent(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString("Team.Join.Invite.InviteForPlayer", "队伍 [&{0}] 邀请你加入"), team.getTeamName(), leader.getName(), player.getName()));
		TextComponent a2 = new TextComponent("[§a接受§r] ");
		a2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 接受邀请 " + getTeam(leader, true).getTeamName()));
		a2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击接受队伍邀请").create()));
		a1.addExtra(a2);
		TextComponent a3 = new TextComponent("[§c拒绝§r] ");
		a3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 拒绝邀请 " + getTeam(leader, true).getTeamName()));
		a3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击拒绝队伍邀请").create()));
		a1.addExtra(a3);
		player.spigot().sendMessage(ChatMessageType.CHAT, a1);
		playerSendMessage(leader, "Team.Join.Invite.InviteForLeader", "你已经向 &{2} 发出邀请", team.getTeamName(), leader.getName(), player.getName());
	}

	// 踢出队伍
	public static void kickPlayer(Player leader, Player player) {
		if (getTeam(leader, true) == null) {
			playerSendMessage(leader, "Team.Error.onPlayerNoLeader", "你不是一个队伍的队长");
			return;
		}
		Team team = getTeam(leader, true);
		team.delMember(player);
		playerSendMessage(leader, "Team.Kick.OnLeader", "你把 &{2} 踢出了队伍", team.getTeamName(), leader.getName(), player.getName());
		playerSendMessage(player, "Team.Kick.OnPlayer", "&{1} 把你踢出了队伍", team.getTeamName(), leader.getName(), player.getName());
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(leader);
		for (Player player2 : players) {
			playerSendMessage(player2, "Team.Kick.OnMembers", "&{1} 把 &{2} 踢出了队伍", team.getTeamName(), leader.getName(), player.getName());
		}
	}

	// 公开招募
	public static void promotionalTeam(Player leader) {
		if (getTeam(leader, true) == null) {
			playerSendMessage(leader, "Team.Error.onPlayerNoLeader", "你不是一个队伍的队长");
			return;
		}
		Team team = getTeam(leader, true);
		TextComponent a1 = new TextComponent(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString("Team.Promotional", "&{1} 邀请大家加入他的队伍 [&{0}]"), team.getTeamName(), leader.getName()));
		TextComponent a2 = new TextComponent("[申请加入]");
		a2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 申请入队 " + team.getTeamName()));
		a2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击申请加入队伍").create()));
		a1.addExtra(a2);
		Bukkit.spigot().broadcast(a1);
	}

	// 同意或拒绝申请
	public static void acceptPlayerApply(Player leader, Player player, Boolean onAccept) {
		if (getTeam(leader, true) == null) {
			playerSendMessage(leader, "Team.Error.onPlayerNoLeader", "你不是一个队伍的队长");
			return;
		}
		Team team = getTeam(leader, true);
		if (team.getOnJoinPlayer().contains(player) == false) {
			playerSendMessage(leader, "Team.Join.Apply.PlayerNoApply", "&{2} 没有向你的队伍发过申请", team.getTeamName(), leader.getName(), player.getName());
			return;
		}
		team.delJoinPlayer(player);
		if (getTeam(player, false) != null) {
			playerSendMessage(leader, "Team.Error.onPlayerIsTeamForLeader", "&{2} 已经在一个队伍中了", team.getTeamName(), leader.getName(), player.getName());
			return;
		}
		if (onAccept) {
			joinTeam(player, team);
			playerSendMessage(leader, "Team.Join.Apply.YesFoLeader", "你同意了 &{2} 的入队申请", team.getTeamName(), leader.getName(), player.getName());
			playerSendMessage(player, "Team.Join.Apply.Yes", "队伍 [&{0}] 同意了你的申请", team.getTeamName(), leader.getName(), player.getName());
			TeamGui.getGui().openTeamInfoGui(team, player);
			return;
		}
		playerSendMessage(leader, "Team.Join.Apply.NoFoLeader", "你拒绝了 &{2} 的入队申请", team.getTeamName(), leader.getName(), player.getName());
		playerSendMessage(player, "Team.Join.Apply.No", "队伍 [&{0}] 拒绝了你的申请", team.getTeamName(), leader.getName(), player.getName());
	}

	// 同意或接受邀请
	public static void acceptTeamInvite(Player player, Team team, Boolean onAccept) {
		Player leader = team.getLeader();
		if (team.getOnInvitePlayer().contains(player) == false) {
			playerSendMessage(player, "Team.Join.Invite.TeamNoInvite", "这个队伍没有邀请过你", team.getTeamName(), leader.getName(), player.getName());
			return;
		}
		team.delInvitePlayer(player);
		if (getTeam(player, false) != null) {
			playerSendMessage(player, "Team.Error.onPlayerIsTeam", "你已经在一个队伍中了", team.getTeamName(), leader.getName(), player.getName());
			return;
		}
		if (onAccept) {
			joinTeam(player, team);
			playerSendMessage(leader, "Team.Join.Invite.YesFoMember", "&{2} 接受了你的邀请", team.getTeamName(), leader.getName(), player.getName());
			playerSendMessage(player, "Team.Join.Invite.Yes", "你接受了队伍 [&{0}] 的邀请", team.getTeamName(), leader.getName(), player.getName());
			TeamGui.getGui().openTeamInfoGui(team, player);
			return;
		}
		playerSendMessage(leader, "Team.Join.Invite.NoFoMember", "&{2} 拒绝了你的邀请", team.getTeamName(), leader.getName(), player.getName());
		playerSendMessage(player, "Team.Join.Invite.No", "你拒绝了队伍 [&{0}] 的邀请", team.getTeamName(), leader.getName(), player.getName());
	}

	private static void playerSendMessage(Player player, String key, @Nonnull String string, @Nonnull String... strings) {
		if (string == null && strings == null) {
			player.sendMessage(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString(key)));
			return;
		}
		if (string == null) {
			player.sendMessage(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString(key), strings));
			return;
		}
		if (strings == null) {
			player.sendMessage(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString(key, string)));
			return;
		}
		player.sendMessage(MessageConfig.BTMessage(BaiTeam.getConfig(ConfigType.Message).getString(key, string), strings));
	}
}
