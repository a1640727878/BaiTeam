package sky_bai.bukkit.baiteam.book;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.BaiTools;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.team.Team;

public class TeamGui {
	private static TeamGui tGui = new TeamGui();

	public static TeamGui getGui() {
		return tGui;
	}

	public void openMainGui(Player player) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent mes = new TextComponent();
		mes.addExtra("§m============================\n\n\n\n\n");
		mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_CreateTeam.getMes()));
		mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_CreateTeam.getMes(), "/baiteam Create", BaiMessage.TeamGui.Text_CreateTeam.getMes()).getText());
		mes.addExtra("\n\n");
		mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_JoinTeam.getMes()));
		mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_JoinTeam.getMes(), "/baiteam OpenGui TeamList", BaiMessage.TeamGui.Text_JoinTeam.getMes()).getText());
		mes.addExtra("\n\n\n\n\n§m============================");
		bookGui.addPage(mes);
		bookGui.openBook(player);
	}

	public void openTeamInfoGui(Player player, Team team) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent mes = new TextComponent();
		String str1 = BaiMessage.TeamGui.Text_TeamInfo.getMes();
		mes.addExtra(BaiTools.setStringCentered(str1) + str1 + "\n\n");
		mes.addExtra(BaiMessage.TeamGui.Text_TeamInfo_1.getMes() + ": " + team.getTeamName() + "\n\n");
		mes.addExtra(BaiMessage.TeamGui.Text_TeamInfo_1.getMes() + ": " + team.getLeader().getName() + "\n\n");
		mes.addExtra(BaiMessage.TeamGui.Text_TeamInfo_1.getMes() + ": ");
		String str2 = team.getMembers().toString();
		str2 = str2.substring(1, str2.length() - 1).replaceAll(", ", "\n");
		BaiMessage.Action ac1 = BaiMessage.Action.setAction("[" + team.getMembers().size() + "/" + BTConfig.getMessage().getConfig().getInt("TeamSize", 5) + "]", null, str2);
		mes.addExtra(ac1.getText());
		mes.addExtra("\n\n§m                             \n\n");
		if (team.getLeader() == player) {
			mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_TeamInfo_InvitePlayer.getMes()));
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_InvitePlayer.getMes(), "/baiteam OpenGui PlayerList", BaiMessage.TeamGui.Text_TeamInfo_InvitePlayer.getMes()).getText());
			mes.addExtra("\n");
			mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_TeamInfo_Promotional.getMes()));
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_Promotional.getMes(), "/baiteam GuiPromotional " + team.getTeamName(), BaiMessage.TeamGui.Text_TeamInfo_Promotional.getMes()).getText());
			mes.addExtra("\n");
			mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_TeamInfo_LeaveTeam.getMes()));
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_LeaveTeam.getMes(), "/baiteam Leave", BaiMessage.TeamGui.Text_TeamInfo_LeaveTeam.getMes()).getText());
			bookGui.addPage(mes);
			foMemberGui(bookGui, team, player);
		} else {
			mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_TeamInfo_InvitePlayer_Member.getMes()));
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_InvitePlayer_Member.getMes(), null, BaiMessage.TeamGui.Text_TeamInfo_InvitePlayer_Member.getMes()).getText());
			mes.addExtra("\n");
			mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_TeamInfo_Promotional_Member.getMes()));
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_Promotional_Member.getMes(), null, BaiMessage.TeamGui.Text_TeamInfo_Promotional_Member.getMes()).getText());
			mes.addExtra("\n");
			mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_TeamInfo_LeaveTeam_Member.getMes()));
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_LeaveTeam_Member.getMes(), null, BaiMessage.TeamGui.Text_TeamInfo_LeaveTeam_Member.getMes()).getText());
			bookGui.addPage(mes);
		}
		bookGui.openBook(player);
	}

	public void openTeamListGui(Player player, List<Team> teams, int page) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent mes = new TextComponent();
		int i1 = teams.size() % 6 > 0 ? teams.size() / 6 + 1 : teams.size() / 6;
		page = page > i1 ? i1 : page;
		for (int i = 0; i < 6; i++) {
			if (teams.size() <= (6 * page + i)) {
				break;
			}
			mes.addExtra(getTeamText(teams.get(6 * page + i)));
		}
		if (page == i1 && teams.size() % 6 > 0) {
			for (int i = 0; i < teams.size() % 6; i++) {
				mes.addExtra("\n\n");
			}
		}
		mes.addExtra("§m============================\n");
		if (page == 0) {
			mes.addExtra(BaiMessage.TeamGui.Button_Previous_No.getMes());
		} else {
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_Previous.getMes(), "/baiteam OpenGui TeamList " + (page - 1), null).getText());
		}
		String str1 = BaiTools.setStringCentered("[00]", 29.0 - BaiTools.getStringLength(BaiMessage.TeamGui.Button_Previous_No.getMes()) - BaiTools.getStringLength(BaiMessage.TeamGui.Button_Next_No.getMes()));
		String str2 = page + "";
		if (page <= 9) {
			str2 = "0" + page;
		}
		mes.addExtra(str1 + "[" + str2 + "]" + str1);
		if (page == i1) {
			mes.addExtra(BaiMessage.TeamGui.Button_Next_No.getMes());
		} else {
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_Next.getMes(), "/baiteam OpenGui TeamList " + (page + 1), null).getText());
		}
		bookGui.addPage(mes);
		bookGui.openBook(player);
	}

	public void openPlayerListGui(Player player, List<Player> players, int page) {
		BookGui bookGui = BookGui.getBookGui();
		TextComponent mes = new TextComponent();
		List<Player> p1 = players;
		int i1 = p1.size() % 12 > 0 ? p1.size() / 12 + 1 : p1.size() / 12;
		page = page > i1 ? i1 : page;
		for (int i = 0; i < 12; i++) {
			if (p1.size() <= (12 * page + i)) {
				break;
			}
			mes.addExtra(getPlayerText(p1.get(12 * page + i).getName()));
		}
		if (page == i1 && p1.size() % 12 > 0) {
			for (int i = 0; i < p1.size() % 12; i++) {
				mes.addExtra("\n");
			}
		}
		mes.addExtra("§m============================\n");
		if (page == 0) {
			mes.addExtra(BaiMessage.TeamGui.Button_Previous_No.getMes());
		} else {
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_Previous.getMes(), "/baiteam OpenGui PlayerList " + (page - 1), null).getText());
		}
		String str1 = BaiTools.setStringCentered("[00]", 29.0 - BaiTools.getStringLength(BaiMessage.TeamGui.Button_Previous_No.getMes())/* - BaiTools.getStringLength(BaiMessage.TeamGui.Button_Next_No.getMes()) */);
		String str2 = page + "";
		if (page <= 9) {
			str2 = "0" + page;
		}
		mes.addExtra(str1);
		mes.addExtra("[" + str2 + "]");
		mes.addExtra(str1);
		if (page == i1 || page == 0) {
			mes.addExtra(BaiMessage.TeamGui.Button_Next_No.getMes());
		} else {
			mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_Next.getMes(), "/baiteam OpenGui PlayerList " + (page + 1), null).getText());
		}
		bookGui.addPage(mes);
		bookGui.openBook(player);
	}

	private TextComponent getPlayerText(String name) {
		TextComponent mes = new TextComponent();
		Double d1 = 29.0 - BaiTools.getStringLength(BaiMessage.TeamGui.Button_Invite.getMes()) - 2.0;
		if (BaiTools.getStringLength(name) > d1) {
			String name2 = name.substring(0, (int) (d1 - 3)) + "...";
			mes.addExtra(BaiMessage.Action.setAction(name2, null, name).getText());
		} else {
			mes.addExtra(name);
			mes.addExtra(BaiTools.setStringRright(BaiMessage.TeamGui.Button_Invite.getMes(), 29.0 - BaiTools.getStringLength(name)));
		}
		mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_Invite.getMes(), "/baiteam InviteTo " + name, null).getText());
		mes.addExtra("\n");
		return mes;
	}

	private TextComponent getTeamText(Team team) {
		TextComponent mes = new TextComponent();
		String str1 = "[" + team.getMembers().size() + "/" + BTConfig.getMessage().getConfig().getInt("TeamSize", 5) + "]";
		Double d1 = 29.0 - BaiTools.getStringLength(str1) - 2.0;
		if (BaiTools.getStringLength(team.getTeamName()) > d1) {
			String name = team.getTeamName().substring(0, (int) (d1 - 3)) + "...";
			mes.addExtra(BaiMessage.Action.setAction(name, null, team.getTeamName()).getText());
			mes.addExtra("  ");
		} else {
			mes.addExtra(BaiTools.setStringCentered(team.getTeamName() + "  " + str1));
			mes.addExtra(team.getTeamName());
			mes.addExtra("  ");
		}
		Set<String> strings = new HashSet<String>();
		for (Player player : team.getMembers()) {
			strings.add(player.getName());
		}
		String str2 = strings.toString();
		str2 = str2.substring(1, str2.length() - 1).replaceAll(", ", "\n");
		BaiMessage.Action ac1 = BaiMessage.Action.setAction("[" + team.getMembers().size() + "/" + BTConfig.getMessage().getConfig().getInt("TeamSize", 5) + "]", null, str2);
		mes.addExtra(ac1.getText());
		mes.addExtra("\n");
		mes.addExtra(BaiTools.setStringCentered(BaiMessage.TeamGui.Button_JoinTeamOnList.getMes()));
		mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_JoinTeamOnList.getMes(), "/baiteam ApplyTo " + team.getTeamName(), BaiMessage.TeamGui.Text_JoinTeamOnList.getMes()).getText());
		mes.addExtra("\n");
		return mes;
	}

	private TextComponent getMemberText(String name) {
		TextComponent mes = new TextComponent();
		String str1 = BaiMessage.TeamGui.Button_TeamInfo_Kick.getMes() + BaiMessage.TeamGui.Button_TeamInfo_Transfer.getMes();
		Double d1 = 29.0 - BaiTools.getStringLength(str1) - 2.0;
		if (BaiTools.getStringLength(name) > d1) {
			String name2 = name.substring(0, (int) (d1 - 3)) + "...";
			mes.addExtra(BaiMessage.Action.setAction(name2, null, name).getText());
		} else {
			mes.addExtra(name + BaiTools.setStringRright(str1, 29.0 - BaiTools.getStringLength(name)));
		}
		mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_Kick.getMes(), "/baiteam GuiKick " + name, BaiMessage.TeamGui.Text_TeamInfo_Kick.getMes()).getText());
		mes.addExtra(BaiMessage.Action.setAction(BaiMessage.TeamGui.Button_TeamInfo_Transfer.getMes(), "/baiteam GuiTransfer " + name, BaiMessage.TeamGui.Text_TeamInfo_Transfer.getMes()).getText());
		mes.addExtra("\n");
		return mes;
	}

	private void foMemberGui(BookGui bookGui, Team team, Player player) {
		Set<Player> players = team.getMembers();
		if (players.size() > 13) {
			List<Player> players_2 = new ArrayList<Player>(players);
			int i1 = players.size() % 13 > 0 ? players.size() / 13 + 1 : players.size() / 13;
			for (int i = 0; i < i1; i++) {
				TextComponent mes = new TextComponent();
				for (int j = 0; j < 13; j++) {
					if (players_2.get(i * 13 + j) == null) {
						break;
					}
					mes.addExtra(getMemberText(players_2.get(i * 13 + j).getName()));
				}
				if (players.size() - (i * 13) < 13) {
					for (int j = 0; j < 13 - (players.size() - (i * 13)); j++) {
						mes.addExtra("\n");
					}
				}
				mes.addExtra("\n§m============================\n");
				bookGui.addPage(mes);
			}
		} else {
			TextComponent mes = new TextComponent();
			for (Player player2 : players) {
				mes.addExtra(getMemberText(player2.getName()));
			}
			for (int i = 0; i < 13 - players.size(); i++) {
				mes.addExtra("\n");
			}
			mes.addExtra("\n§m============================\n");
			bookGui.addPage(mes);
		}
	}
}
