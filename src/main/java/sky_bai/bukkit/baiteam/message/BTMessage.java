package sky_bai.bukkit.baiteam.message;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BTMessage {
	private static String mesPrefix = "[BaiTeam] ";

	public static void send(Player player, BaiMes es, List<String> list) {
		String mes = setMes(es.getMes(), list);
		player.sendMessage(mes);
	}

	public static void send(Player player, BaiMes es, List<String> list, Action... actions) {
		TextComponent mes = new TextComponent(setMes(es.getMes(), list));
		for (Action action : actions) {
			mes.addExtra(action.getText());
		}
		player.spigot().sendMessage(ChatMessageType.CHAT, mes);
	}

	public static void broadcast(BaiMes es, List<String> list) {
		String mes = setMes(es.getMes(), list);
		Bukkit.broadcast(mes, "");
	}

	public static void broadcast(BaiMes es, List<String> list, Action... actions) {
		TextComponent mes = new TextComponent(setMes(es.getMes(), list));
		for (Action action : actions) {
			mes.addExtra(action.getText());
		}
		Bukkit.spigot().broadcast(mes);
	}

	private static String setMes(String str, List<String> list) {
		if (list == null) {
			return str = getMesPrefix() + str;
		}
		for (int i = 0; i < list.size(); i++) {
			str = str.replaceAll("\\&\\{" + i + "\\}", list.get(i));
		}
		return str = getMesPrefix() + str;
	}

	public static String getMesPrefix() {
		String str = mesPrefix;
		return str;
	}

	public static void setMesPrefix(String str) {
		mesPrefix = str;
	}

	interface BaiMes {
		public String getKey();

		public void setMes(String mes);

		public String getMes();
	}

	public static class Action {
		String str;
		String command;
		String text;

		private Action(@Nonnull String str, @Nullable String command, @Nullable String text) {
			this.str = str;
			this.command = command;
			this.text = text;
		}

		public static Action setAction(@Nonnull String str, @Nullable String command, @Nullable String text) {
			Action action = new Action(str, command, text);
			return action;
		}

		public static Action setAction(String str) {
			Action action = new Action(str, null, null);
			return action;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public void setText(String text) {
			this.text = text;
		}

		public TextComponent getText() {
			TextComponent a1 = new TextComponent(str);
			if (command != null) {
				a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
			}
			if (text != null) {
				a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
			}
			return a1;
		}
	}

	public enum Error implements BaiMes {
		OnTeamNameIsUse("Error.OnTeamNameIsUse", "这个队伍名已经被使用"),
		OnPlayerNoTeam("Error.OnPlayerNoTeam", "你不在一个队伍中"),
		OnPlayerNoLeader("Error.OnPlayerNoLeader", "你不是队长"),
		OnPlayerToDunge("Error.OnPlayerToDunge", "在副本中组队是不允许的"),
		OnPlayerOnTeam("Error.OnPlayerOnTeam", "你已经在一个队伍中"),
		OnPlayerInTeam("Error.OnPlayerInTeam", "玩家 &{2} 已经在别的队伍中"),
		OnTeamIsFull("Error.OnTeamIsFull", "队伍 &{0} 已满"),
		OnTeamIsNo("Error.OnTeamNameIsUse", "队伍不存在"),
		OnPlayerKickMe("Error.OnPlayerKickMe", "你不能把自己从队伍里踢出"),
		OnTeleportIsNo("Error.OnTeleportIsNo", "传送已被使用或已过期");

		private String key = null;
		private String mes = null;

		Error(String key, String mes) {
			this.key = key;
			this.mes = mes;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getMes() {
			return mes;
		}

		@Override
		public void setMes(String mes) {
			this.mes = mes;
		}
	}

	public enum Team implements BaiMes {
		Create("Team.Create", "你成功的创建了队伍 [&{0}]"),
		Leave_OnLeader_Member("Team.Leave_OnLeader_Member", "&{1} 解散了队伍"),
		Leave_OnLeader_Leader("Team.Leave_OnLeader_Leader", "你解散了队伍 [&{0}]"),
		Leave_OnMember_Members("Team.Leave_OnMember_Members", "&{2} 离开了队伍"),
		Leave_OnMember_Member("Team.Leave_OnMember_Member", "你离开了队伍 [&{0}]"),
		Join_Apply_ApplyForPlayer("Team.Join_Apply_ApplyForPlayer", "你向队伍 [&{0}] 提交了申请 "),
		Join_Apply_ApplyForLeader("Team.Join_Apply_ApplyForLeader", "&{2} 申请加入你的队伍"),
		Join_Apply_PlayerNoApply("Team.Join_Apply_PlayerNoApply", "&{2} 没有向你的队伍发过申请"),
		Join_Apply_Yes("Team.Join_Apply_Yes", "队伍 [&{0}] 同意了你的申请"),
		Join_Apply_YesFoLeader("Team.Join_Apply_YesFoLeader", "你同意了 &{2} 的入队申请"),
		Join_Apply_No("Team.Join_Apply_No", "队伍 [&{0}] 拒绝了你的申请"),
		Join_Apply_NoFoLeader("Team.Join_Apply_NoFoLeader", "你拒绝了 &{2} 的入队申请"),
		Join_Invite_InviteForPlayer("Team.Join_Invite_InviteForPlayer", "队伍 [&{0}] 邀请你加入"),
		Join_Invite_InviteForLeader("Team.Join_Invite_InviteForLeader", "你已经向 &{2} 发出邀请"),
		Join_Invite_TeamNoInvite("Team.Join_Invite_TeamNoInvite", "这个队伍没有邀请过你"),
		Join_Invite_Yes("Team.Join_Invite_Yes", "&{2} 接受了你的邀请"),
		Join_Invite_YesFoMember("Team.Join_Invite_YesFoMember", "你接受了队伍 [&{0}] 的邀请"),
		Join_Invite_No("Team.Join_Invite_No", "&{2} 拒绝了你的邀请"),
		Join_Invite_NoFoMember("Team.Join_Invite_NoFoMember", "你拒绝了队伍 [&{0}] 的邀请"),
		Join_Members("Team.Join_Members", "&{2} 加入了队伍"),
		Join_Member("Team.Join_Member", "你加入了队伍 [&{0}]"),
		Transfer_OnLeader("Team.Transfer_OnLeader", "你把队伍 [&{0}] 转让给了 &{2}"),
		Transfer_OnPlayer("Team.Transfer_OnPlayer", "&{1} 把队伍 [&{0}] 转让给了你"),
		Transfer_OnMembers("Team.Transfer_OnMembers", "&{1} 把队伍 [&{0}] 转让给了 &{2}"),
		Promotional("Team.Promotional", "&{1} 邀请大家加入他的队伍 [&{0}]"),
		Kick_OnLeader("Team.Kick_OnLeader", "你把 &{2} 踢出了队伍"),
		Kick_OnPlayer("Team.Kick_OnPlayer", "&{1} 把你踢出了队伍"),
		Kick_OnMembers("Team.Kick_OnMembers", "&{1} 把 &{2} 踢出了队伍"),
		Teleport_Member("Team.Teleport_Member", "队长被传送走了,是否跟上");

		private String key = null;
		private String mes = null;

		Team(String key, String mes) {
			this.key = key;
			this.mes = mes;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getMes() {
			return mes;
		}

		@Override
		public void setMes(String mes) {
			this.mes = mes;
		}
	}

	public enum Button {

		Yes("Button.Yes", "[§a同意§r] "),
		NO("Button.No", "[§c拒绝§r] "),
		ApplyTo("Button.ApplyTo", "[申请加入]"),
		Text_Apply_Yes("Button.Text_Apply_Yes", "点击同意玩家加入"),
		Text_Apply_No("Button.Text_Apply_No", "点击拒绝玩家加入"),
		Text_Invite_Yes("Button.Text_Invite_Yes", "点击接受队伍邀请"),
		Text_Invite_No("Button.Text_Invite_No", "点击拒绝队伍邀请"),
		Text_ApplyTo("Button.Text_ApplyTo", "点击申请加入队伍"),
		Teleport_Yes("Button.Teleport_Yes", "[§a是§r]"),
		Text_Teleport_Yes("Button.Text_Teleport_Yes", "点击传送到队长刚刚传送的那个点");

		private String key = null;
		private String mes = null;

		Button(String key, String mes) {
			this.key = key;
			this.mes = mes;
		}

		public String getKey() {
			return key;
		}

		public String getMes() {
			return mes;
		}

		public void setMes(String mes) {
			this.mes = mes;
		}
	}
}
