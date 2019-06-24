package sky_bai.bukkit.baiteam.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.ConfigType;
import sky_bai.bukkit.baiteam.gui.TeamGui;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamManager;

public class BaiTeamCommand implements TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String str1 = "";
			if (args.length >= 1) {
				str1 = args[0];
			}
			switch (str1) {
			case "game": {
				DungeonsXL dungeonsXL = DungeonsXL.getInstance();
				String str2 = "";
				if (args.length >= 2) {
					str2 = args[1];
				}
				switch (str2) {
				case "play": {
					if (args.length < 3 || dungeonsXL.getDWorldCache().getResourceByName(args[2]) == null || TeamManager.getTeam(player, true) == null || dungeonsXL.getDPlayerCache().getByPlayer(player) instanceof DInstancePlayer) {
						System.out.println(args.length);
						System.out.println(dungeonsXL.getDWorldCache().getResourceByName(args[2]));
						System.out.println(TeamManager.getTeam(player, true));
						System.err.println(dungeonsXL.getDPlayerCache().getByPlayer(player) instanceof DInstancePlayer);
						return false;
					}
					DResourceWorld resource = dungeonsXL.getDWorldCache().getResourceByName(args[2]);
					Dungeon dungeon = new Dungeon(dungeonsXL, resource);
					Team team = TeamManager.getTeam(player, true);
					List<Player> members = new ArrayList<Player>(team.getMembers());
					members.remove(player);
					DGroup group = new DGroup(dungeonsXL, team.getTeamName(), player, members, dungeon);
					DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
					new Game(dungeonsXL, group, gameWorld);
					for (Player groupPlayer : group.getPlayers().getOnlinePlayers()) {
						new DGamePlayer(dungeonsXL, groupPlayer, group.getGameWorld());
					}
					return true;
				}
				case "gui": {
					if (args.length < 3) {
						return false;
					}
					String str3 = args[2];
					switch (str3) {
					case "all": {
						if (TeamManager.getTeam(player, false) == null) {
							TeamGui.getGui().openMainGui(player);
						} else {
							Team team = TeamManager.getTeam(player, false);
							TeamGui.getGui().openTeamInfoGui(team, player);
						}
						return true;
					}
					case "Main": {
						TeamGui.getGui().openMainGui(player);
						return true;
					}
					case "TeamInfo": {
						if (TeamManager.getTeam(player, false) == null) {
							return false;
						}
						Team team = TeamManager.getTeam(player, false);
						TeamGui.getGui().openTeamInfoGui(team, player);
						return true;
					}
					case "TeamList": {
						int int1 = 0;
						if (args.length >= 4 && Integer.valueOf(args[3]) != null) {
							int1 = Integer.valueOf(args[3]);
						}
						TeamGui.getGui().openTeamListGui(player, int1);
						return true;
					}
					case "PlayerList": {
						if (TeamManager.getTeam(player, true) == null) {
							return false;
						}
						Team team = TeamManager.getTeam(player, true);
						int int1 = 0;
						if (args.length >= 4 && Integer.valueOf(args[3]) != null) {
							int1 = Integer.valueOf(args[3]);
						}
						List<OfflinePlayer> offlinePlayers = Arrays.asList(Bukkit.getOfflinePlayers());
						List<Player> players = new ArrayList<Player>();
						for (OfflinePlayer offlinePlayer : offlinePlayers) {
							if (offlinePlayer.isOnline() && TeamManager.getTeam((Player) offlinePlayer, false) == null) {
								players.add((Player) offlinePlayer);
							}
						}
						if (players.isEmpty()) {
							TeamGui.getGui().openTeamInfoGui(team, player);
							return false;
						}
						TeamGui.getGui().openPlayerList(player, players, int1);
						return true;
					}
					default:
						return false;
					}
				}
				default:
					return false;
				}
			}
			case "创建队伍": {
				Random random = new Random();
				List<String> teamNames = BaiTeam.getConfig(ConfigType.Config).getStringList("RandomTeamNames");
				String teamName = teamNames.get(random.nextInt(teamNames.size() - 1)) + "#" + getRandomInt();
				while (TeamManager.getTeam(teamName) != null) {
					teamName = teamNames.get(random.nextInt(teamNames.size() - 1)) + "#" + getRandomInt();
				}
				TeamManager.createTeam(player, teamName);
				return true;
			}
			case "解散队伍": {
				TeamManager.dissolveTeam(player);
				return true;
			}
			case "离开队伍": {
				TeamManager.leaveTeam(player);
				return true;
			}
			case "申请入队": {
				if (args.length < 2 || TeamManager.getTeam(args[1]) == null) {
					return false;
				}
				Team team = TeamManager.getTeam((args[1]));
				TeamManager.playerApply(player, team);
				return true;
			}
			case "邀请玩家": {
				if (args.length < 2 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				Player player2 = Bukkit.getPlayer(args[1]);
				TeamManager.teamInvite(player, player2);
				return true;
			}
			case "转让队伍": {
				if (args.length < 2 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				Player player2 = Bukkit.getPlayer(args[1]);
				TeamManager.transferTeam(player, player2);
				return true;
			}
			case "踢出队伍": {
				if (args.length < 2 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				Player player2 = Bukkit.getPlayer(args[1]);
				TeamManager.kickPlayer(player, player2);
				return true;
			}
			case "同意申请": {
				if (args.length < 2 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				Player player2 = Bukkit.getPlayer(args[1]);
				TeamManager.acceptPlayerApply(player, player2, true);
				return true;
			}
			case "拒绝申请": {
				if (args.length < 2 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				Player player2 = Bukkit.getPlayer(args[1]);
				TeamManager.acceptPlayerApply(player, player2, false);
				return true;
			}
			case "接受邀请": {
				if (args.length < 2 || TeamManager.getTeam(args[1]) == null) {
					return false;
				}
				Team team = TeamManager.getTeam((args[1]));
				TeamManager.acceptTeamInvite(player, team, true);
				return true;
			}
			case "拒绝邀请": {
				if (args.length < 2 || TeamManager.getTeam(args[1]) == null) {
					return false;
				}
				Team team = TeamManager.getTeam((args[1]));
				TeamManager.acceptTeamInvite(player, team, false);
				return true;
			}
			/*case "快速加入": {
				Set<Team> teams = TeamManager.getTeams();
				for (Team team : teams) {
					if (team.getMembers().size() > BaiTeam.getConfig(ConfigType.Config).getInt("TeamSize", 5)) {
						TeamManager.joinTeam(player, team);
						return true;
					}
				}
				TeamGui.getGui().openMainGui(player);
				return false;
			}*/
			case "集结传送": {
				if (TeamManager.getTeam(player, false) == null || TeamManager.getTeam(player, false).getLeader() == player || TeamManager.getTeam(player, false).getLeader().getWorld() != player.getWorld() || BaiTeam.getConfig(ConfigType.Config).getStringList("ButtonWorld").contains(player.getWorld().getName()) == false) {
					return false;
				}
				player.teleport(TeamManager.getTeam(player, false).getLeader());
				return true;
			}
			case "公开招募": {
				TeamManager.promotionalTeam(player);
				return true;
			}
			default:
				return false;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length <= 1) {
			return Arrays.asList("game");
		}
		if (args.length >= 2) {
			String str1 = args[0];
			if (str1.equalsIgnoreCase("game")) {
				if (args.length == 3) {
					String str2 = args[1];
					if (str2.equalsIgnoreCase("play")) {
						List<String> strs = Arrays.asList(DungeonsXL.MAPS.list());
						strs.remove(".raw");
						return strs;
					}
					if (str2.equalsIgnoreCase("gui")) {
						return Arrays.asList("all","Main","TeamInfo","TeamList","PlayerList");
					}
				}
				return Arrays.asList("play", "gui");
			}
		}
		return Arrays.asList();
	}

	private String getRandomInt() {
		Random random = new Random();
		String string = "";
		int i = random.nextInt(999);
		if (i <= 9) {
			string = "00" + i;
		} else if (i <= 99) {
			string = "0" + i;
		} else {
			string = "" + i;
		}
		return string;
	}

}
