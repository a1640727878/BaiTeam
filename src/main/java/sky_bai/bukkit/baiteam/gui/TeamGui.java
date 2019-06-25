package sky_bai.bukkit.baiteam.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.bookgui.BookGui;
import sky_bai.bukkit.baiteam.config.ConfigType;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamManager;

public class TeamGui {
	private static TeamGui tGui = new TeamGui();

	public static TeamGui getGui() {
		return tGui;
	}

	public void openMainGui(Player player) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent a1 = new TextComponent();
		a1.addExtra("§m=============================\n");
		a1.addExtra("\n\n\n\n");
		a1.addExtra("         ");
		TextComponent b2 = new TextComponent("[创建队伍]");
		b2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 创建队伍"));
		b2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("创建属于你的队伍").create()));
		a1.addExtra(b2);
		a1.addExtra("\n\n");
		a1.addExtra("         ");
		TextComponent b3 = new TextComponent("[加入队伍]");
		b3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam game gui TeamList"));
		b3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("寻找可以加入队伍").create()));
		a1.addExtra(b3);
		a1.addExtra("\n\n\n\n\n");
		a1.addExtra("§m=============================\n");
		bookGui.addPage(a1);
		bookGui.openBook(player);
	}

	public void openTeamInfoGui(Team team, Player player) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent a1 = new TextComponent();
		a1.addExtra("         [队伍信息]\n\n");
		a1.addExtra("队伍名称: " + team.getTeamName() + "\n\n");
		a1.addExtra("队长名称: " + team.getLeader().getName() + "\n\n");
		List<Player> b1 = new ArrayList<Player>();
		b1.addAll(team.getMembers());
		TextComponent c1 = new TextComponent("" + b1.size() + "/5");
		TextComponent d1 = new TextComponent();
		for (Player player2 : b1) {
			d1.addExtra(player2.getName() + "\n");
		}
		c1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(d1).create()));
		a1.addExtra("队伍人数: [");
		a1.addExtra(c1);
		a1.addExtra("]\n\n");
		a1.addExtra("§m                             \n\n");
		a1.addExtra("         ");
		TextComponent c2 = new TextComponent();
		if (team.getLeader() != player) {
			c2.addExtra("[§7邀请玩家§r]\n");
			c2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f只有队长才能邀请玩家").create()));
		} else {
			c2.addExtra("[邀请玩家]\n");
			c2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f浏览等待中的玩家").create()));
			c2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam game gui PlayerList"));
		}
		a1.addExtra(c2);
		a1.addExtra("         ");
		TextComponent c3 = new TextComponent();
		if (team.getLeader() == player) {
			c3.addExtra("[公开招募]\n");
			c3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f发送队伍招募公告").create()));
			c3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 公开招募"));
		} else {
			c3.addExtra("[集结传送]\n");
			c3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f传送至队长身边").create()));
			c3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 集结传送"));
		}
		a1.addExtra(c3);
		a1.addExtra("         ");
		TextComponent c5 = new TextComponent();
		if (team.getLeader() == player) {
			c5.addExtra("[解散队伍]");
		} else {
			c5.addExtra("[离开队伍]");
		}
		c5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f退出或解散该队伍").create()));
		c5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 离开队伍"));
		a1.addExtra(c5);
		bookGui.addPage(a1);
		if (team.getLeader() == player) {
			bookGui.addPage(foMemberGui(team, player));
		}
		bookGui.openBook(player);
	}

	public void openTeamListGui(Player player, int page) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent a1 = new TextComponent();
		List<Team> teams = new ArrayList<Team>(TeamManager.getTeams());
		for (Team team : teams) {
			if (team.getMembers().size() >= BaiTeam.getConfig(ConfigType.Config).getInt("TeamSize", 5)) {
				teams.remove(team);
			}
		}
		for (int i = page * 6; i < teams.size(); i++) {
			a1.addExtra(getTeamText(teams.get(i)));
			if (i % 6 == 5) {
				int maxpage = teams.size() / 6;
				a1.addExtra(customTextName2(page, maxpage));
				bookGui.addPage(a1);
				break;
			}
			if (i == teams.size() - 1) {
				int maxpage = teams.size() / 6;
				if (teams.size() % 6 > 0) {
					maxpage = maxpage + 1;
				}
				String str = "";
				if (6 * maxpage > teams.size()) {
					for (int j = 0; j < 6 * maxpage - teams.size(); j++) {
						str = str + "\n\n";
					}
				}
				a1.addExtra(str);
				a1.addExtra(customTextName2(page, maxpage));
				bookGui.addPage(a1);
				break;
			}
		}
		bookGui.openBook(player);
	}

	public void openPlayerList(Player player, List<Player> players, int page) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent a1 = new TextComponent("");
		for (int i = page * 12; i < players.size(); i++) {
			a1.addExtra(getPlayerText(players.get(i).getName()));
			if (i % 12 == 11) {
				int maxpage = players.size() / 12;
				a1.addExtra(customTextName2(page, maxpage));
				bookGui.addPage(a1);
				break;
			}
			if (i == players.size() - 1) {
				int maxpage = players.size() / 12;
				if (players.size() % 12 > 0) {
					maxpage = maxpage + 1;
				}
				String str = "";
				if (12 * maxpage > players.size()) {
					for (int j = 0; j < 12 * maxpage - players.size(); j++) {
						str = str + "\n";
					}
				}
				a1.addExtra(str);
				a1.addExtra(customTextName2(page, maxpage));
				bookGui.addPage(a1);
				break;
			}
		}
		bookGui.openBook(player);
	}

	private TextComponent customTextName(String str, int length, TextComponent text) {
		str = str + "§l";
		if (str.length() < length) {
			for (int i = 0; i < length - str.length(); i++) {
				str = str + " ";
			}
		}
		str = str + "§r";
		TextComponent a1 = new TextComponent(str);
		a1.addExtra(text);
		return a1;
	}

	private TextComponent customTextName2(int page, int maxpage) {
		TextComponent a1 = new TextComponent("§m=============================\n");
		a1.addExtra("  ");
		TextComponent a2 = new TextComponent();
		if (page == 0) {
			a2.addExtra("[§7上一页§r]");
		} else {
			a2.addExtra("[上一页]");
			a2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam game GUI TeamList " + page));
		}
		a1.addExtra(a2);
		String str1 = page + "";
		if (page <= 9) {
			str1 = "0" + page;
		}
		a1.addExtra("   [" + str1 + "]   ");
		TextComponent a3 = new TextComponent();
		if (page == maxpage) {
			a3.addExtra("[§7下一页§r]");
		} else {
			a3.addExtra("[下一页]");
			a3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam game GUI TeamList " + page));
		}
		a1.addExtra(a3);
		return a1;
	}

	private TextComponent getPlayerText(String playerName) {
		TextComponent a1 = new TextComponent("[邀请]\n");
		a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击邀请 " + playerName + " 加入队伍").create()));
		a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 邀请玩家 " + playerName));
		return customTextName(playerName, 20, a1);
	}

	private TextComponent getMemberText(String playerName) {
		TextComponent a1 = new TextComponent();
		TextComponent a2 = new TextComponent("[踢出]");
		a2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 踢出队伍 " + playerName));
		a2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("将玩家踢出队伍").create()));
		a1.addExtra(a2);
		TextComponent a3 = new TextComponent("[转让]");
		a3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 转让队伍 " + playerName));
		a3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("把队伍转让给他").create()));
		a1.addExtra(a3);
		a1.addExtra("\n");
		return customTextName(playerName, 18, a1);
	}

	private TextComponent getTeamText(Team team) {
		TextComponent a1 = new TextComponent();
		List<Player> b1 = new ArrayList<Player>(team.getMembers());
		String str1 = "";
		for (Player player : b1) {
			str1 = str1 + player.getName() + "\n";
		}
		TextComponent a2 = new TextComponent("[" + b1.size() + "/" + BaiTeam.getConfig(ConfigType.Config).getInt("TeamSize", 5) + "]");
		a2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(str1).create()));
		a1.addExtra(a2);
		a1.addExtra("\n         ");
		TextComponent a3 = new TextComponent("[申请加入]\n");
		a3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击申请加入队伍").create()));
		a3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baiteam 申请加入 " + team.getTeamName()));
		a1.addExtra(a3);
		return customTextName("      " + team.getTeamName() + " ", 0, a1);
	}

	private TextComponent foMemberGui(Team team, Player player) {
		TextComponent a1 = new TextComponent();
		List<Player> players = new ArrayList<Player>(team.getMembers());
		players.remove(team.getLeader());
		for (Player player2 : players) {
			a1.addExtra(getMemberText(player2.getName()));
		}
		if (players.size() < 4) {
			for (int i = 0; i < 4 - players.size(); i++) {
				a1.addExtra("\n");
			}
		}
		a1.addExtra("\n");
		a1.addExtra("§m=============================\n");
		return a1;
	}
}
