package sky_bai.bukkit.baiteam.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.event.BTEAcceptPlayerApplyEvent;
import sky_bai.bukkit.baiteam.event.BTEAcceptTeamInviteEvent;
import sky_bai.bukkit.baiteam.event.BTECreateTeamEvent;
import sky_bai.bukkit.baiteam.event.BTEKickPlayerEvent;
import sky_bai.bukkit.baiteam.event.BTELeaveTeamEvent;
import sky_bai.bukkit.baiteam.event.BTEPlayerApplyEvent;
import sky_bai.bukkit.baiteam.event.BTEPromotionalTeamEvent;
import sky_bai.bukkit.baiteam.event.BTETeamInviteEvent;
import sky_bai.bukkit.baiteam.event.BTETransferTeamEvent;
import sky_bai.bukkit.baiteam.gui.TeamGui;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamPromotional;
import sky_bai.bukkit.baiteam.team.TeamTeleportExpired;
import sky_bai.bukkit.baiteam.util.BTTools;

public class BTCommand {
	private static boolean ifPlayerTeleport(Player player1, Player player2) {
		if (player1.getWorld() != player2.getWorld()) {
			return false;
		}
		double x = (player1.getLocation().getX() > player2.getLocation().getX())
				? (player1.getLocation().getX() - player2.getLocation().getX())
				: (player2.getLocation().getX() - player1.getLocation().getX());
		double y = (player1.getLocation().getY() > player2.getLocation().getY())
				? (player1.getLocation().getY() - player2.getLocation().getY())
				: (player2.getLocation().getY() - player1.getLocation().getY());
		double z = (player1.getLocation().getZ() > player2.getLocation().getZ())
				? (player1.getLocation().getZ() - player2.getLocation().getZ())
				: (player2.getLocation().getZ() - player1.getLocation().getZ());
		return ((x + y + z) >= 10);
	}

	public static boolean play(Player player, String[] args) {
		DungeonsXL dungeonsXL = DungeonsXL.getInstance();
		if (args.length < 2 || dungeonsXL.getDWorldCache().getResourceByName(args[1]) == null
				|| dungeonsXL.getDPlayerCache().getByPlayer(player) instanceof DInstancePlayer) {
			return false;
		}
		DResourceWorld resource = dungeonsXL.getDWorldCache().getResourceByName(args[1]);
		Dungeon dungeon = new Dungeon(dungeonsXL, resource);
		if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
			DGroup group = new DGroup(dungeonsXL, player.getName() + "Dun", player, dungeon);
			DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
			new Game(dungeonsXL, group, gameWorld);
			for (Player groupPlayer : group.getPlayers().getOnlinePlayers()) {
				new DGamePlayer(dungeonsXL, groupPlayer, group.getGameWorld());
			}
			return true;
		}
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		List<Player> members = new ArrayList<Player>(team.getMembers());
		members.remove(player);
		for (Player player2 : members) {
			if (ifPlayerTeleport(player, player2)) {
				BTMessage.send(player, BTMessage.Error.OnPlayNotEnough, null);
				return false;
			}
		}
		DGroup group = new DGroup(dungeonsXL, team.getTeamName(), player, members, dungeon);
		DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
		new Game(dungeonsXL, group, gameWorld);
		for (Player groupPlayer : group.getPlayers().getOnlinePlayers()) {
			new DGamePlayer(dungeonsXL, groupPlayer, group.getGameWorld());
		}
		return true;
	}

	public static boolean teleport(Player sender, String[] args) {
		if (args.length < 1) {
			return false;
		}
		if (args[1].equalsIgnoreCase("teleport") && args.length > 5) {
			Player player = Bukkit.getPlayer(args[2]);
			Team team = BaiTeam.getTeamManager().getTeam(player, false);
			double x = Double.valueOf(args[3]);
			double y = Double.valueOf(args[4]);
			double z = Double.valueOf(args[5]);
			World world = (args.length > 6 && Bukkit.getWorld(args[6]) != null) ? Bukkit.getWorld(args[6])
					: player.getWorld();
			Location location = new Location(world, x, y, z);
			String uuid = UUID.randomUUID().toString();
			while (TeamTeleportExpired.LocationMap.containsKey(uuid)) {
				uuid = UUID.randomUUID().toString();
			}
			long time = System.currentTimeMillis();
			while (TeamTeleportExpired.UuidTime.containsKey(time)) {
				time = time + 1;
			}
			TeamTeleportExpired.LocationMap.put(uuid, location);
			Set<Player> tpPlayers = new HashSet<Player>();
			tpPlayers.add(player);
			TeamTeleportExpired.TeleportPlayer.put(uuid, tpPlayers);
			TeamTeleportExpired.UuidTime.put(time, uuid);
			player.teleport(location);
			if (team != null && team.getLeader() == player) {
				Set<Player> players = team.getMembers();
				for (Player player2 : players) {
					if (player2 != player && ifPlayerTeleport(player, player2)) {
						BTMessage.Action action = BTMessage.Action.setAction(BTMessage.Button.Teleport_Yes.getMes(),
								"/baiteam Teleport Yes " + uuid, BTMessage.Button.Text_Teleport_Yes.getMes());
						BTMessage.send(player2, BTMessage.Team.Teleport_Member,
								Arrays.asList(team.getTeamName(), player.getName(), location.toString()), action);
					}
				}
			}
		} else if (args[1].equalsIgnoreCase("yes") && args.length > 2) {
			String uuid = args[2];
			if (TeamTeleportExpired.LocationMap.containsKey(uuid) == false) {
				sender.sendMessage(BTMessage.Error.OnTeleportIsNo.getMes());
				return false;
			}
			if (TeamTeleportExpired.TeleportPlayer.get(uuid).contains(sender)) {
				sender.sendMessage(BTMessage.Error.OnTeleportIsNo.getMes());
				return false;
			}
			sender.teleport(TeamTeleportExpired.LocationMap.get(uuid));
			TeamTeleportExpired.TeleportPlayer.get(uuid).add(sender);
		}
		return true;
	}

	public static boolean kickTeamFoDungeon(Player player) {
		DungeonsXL dXl = DungeonsXL.getInstance();
		if (BaiTeam.getTeamManager().ifOnTeam(player)) {
			Team team = BaiTeam.getTeamManager().getTeam(player, false);
			Set<Player> players = team.getMembers();
			for (Player player2 : players) {
				DGlobalPlayer dPlayer = dXl.getDPlayerCache().getByPlayer(player2);
				if (dPlayer instanceof DInstancePlayer) {
					((DInstancePlayer) dPlayer).leave();
				}
			}
		} else {
			DGlobalPlayer dPlayer = dXl.getDPlayerCache().getByPlayer(player);
			if (dPlayer instanceof DInstancePlayer) {
				((DInstancePlayer) dPlayer).leave();
			}
		}
		return true;
	}

	public static boolean openGui(Player sender, String[] args) {
		if (args.length < 2) {
			if (BaiTeam.getTeamManager().ifOnTeam(sender) == false) {
				TeamGui.openGui(sender, "Main", 0);
			} else {
				TeamGui.openGui(sender, "Teaminfo", 0);
			}
			return true;
		} else {
			int i = 0;
			if (args.length > 2) {
				try {
					i = Integer.valueOf(args[2]);
				} catch (NumberFormatException e) {
					System.out.println(args[2] + "不是整数");
				}
			}
			TeamGui.openGui(sender, args[1], i);
			return true;
		}
	}

	public static boolean create(Player player, String[] args) {
		String name = "";
		boolean b = BTConfig.getConfig().getConfig().getBoolean("TeamNames.Enable", true);
		if (b) {
			List<String> names = BTConfig.getConfig().getConfig().getStringList("TeamNames.List");
			name = BTTools.RandomString(names);
		} else {
			name = player.getName();
		}
		if (args.length > 1) {
			name = args[1];
		}
		int i1 = 0;
		while (BaiTeam.getTeamManager().ifTeam(name)) {
			name = name + "#" + i1;
			i1++;
		}
		Team team = new Team(player, name);
		Bukkit.getPluginManager().callEvent(new BTECreateTeamEvent(team, player));
		return true;
	}

	public static boolean leave(Player player, String[] args) {
		if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoTeam, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, false);
		Bukkit.getPluginManager().callEvent(new BTELeaveTeamEvent(team, player));
		return true;
	}

	public static boolean applyto(Player player, String[] args) {
		if (args.length <= 1) {
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(args[1]);
		Bukkit.getPluginManager().callEvent(new BTEPlayerApplyEvent(team, player));
		return true;
	}

	public static boolean guiapplyto(Player player, String[] args) {
		if (args.length <= 1) {
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(args[1]);
		Bukkit.getPluginManager().callEvent(new BTEPlayerApplyEvent(team, player));
		TeamGui.openGui(player, "TeamList", 0);
		return true;
	}

	public static boolean apply(Player player, String[] args) {
		if (args.length <= 2 || Bukkit.getPlayer(args[2]) == null) {
			return false;
		}
		boolean b = args[1].toLowerCase().equalsIgnoreCase("yes") ? true : false;
		Player player2 = Bukkit.getPlayer(args[2]);
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		Bukkit.getPluginManager().callEvent(new BTEAcceptPlayerApplyEvent(team, player2, b));
		return true;
	}

	public static boolean inviteto(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		Player player2 = Bukkit.getPlayer(args[1]);
		Bukkit.getPluginManager().callEvent(new BTETeamInviteEvent(team, player2));
		return true;
	}

	public static boolean guiinviteto(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		Player player2 = Bukkit.getPlayer(args[1]);
		Bukkit.getPluginManager().callEvent(new BTETeamInviteEvent(team, player2));
		TeamGui.openGui(player, "PlayerList", 0);
		return true;
	}

	public static boolean invite(Player player, String[] args) {
		if (args.length <= 2 || BaiTeam.getTeamManager().ifTeam(args[2]) == false) {
			return false;
		}
		boolean b = args[1].toLowerCase().equalsIgnoreCase("yes") ? true : false;
		Team team = BaiTeam.getTeamManager().getTeam(args[2]);
		Bukkit.getPluginManager().callEvent(new BTEAcceptTeamInviteEvent(team, player, b));
		return true;
	}

	public static boolean transfer(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		Player player2 = Bukkit.getPlayer(args[1]);
		Bukkit.getPluginManager().callEvent(new BTETransferTeamEvent(team, player2));
		return true;
	}

	public static boolean guitransfer(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
			return false;
		}
		Player player2 = Bukkit.getPlayer(args[1]);
		Bukkit.getPluginManager().callEvent(new BTETransferTeamEvent(team, player2));
		TeamGui.openGui(player, "Teaminfo", 0);
		TeamGui.openGui(player2, "Teaminfo", 0);
		return true;
	}

	public static boolean kick(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
			return false;
		}
		Player player2 = Bukkit.getPlayer(args[1]);
		Bukkit.getPluginManager().callEvent(new BTEKickPlayerEvent(team, player2));
		return true;
	}

	public static boolean guikick(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
			return false;
		}
		Player player2 = Bukkit.getPlayer(args[1]);
		Bukkit.getPluginManager().callEvent(new BTEKickPlayerEvent(team, player2));
		TeamGui.openGui(player, "Teaminfo", 0);
		return true;
	}

	public static boolean promotional(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(args[1]);
		if (TeamPromotional.PromotionalTime.containsKey(team) && (TeamPromotional.PromotionalTime.get(team)
				+ BTConfig.getConfig().getConfig().getLong("Time.Promotional.CoolDown", 20000)) >= System
						.currentTimeMillis()) {
			return false;
		}
		Bukkit.getPluginManager().callEvent(new BTEPromotionalTeamEvent(team));
		return true;
	}

	public static boolean guipromotional(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(args[1]);
		if (TeamPromotional.PromotionalTime.containsKey(team) && (TeamPromotional.PromotionalTime.get(team)
				+ BTConfig.getConfig().getConfig().getLong("Time.Promotional.CoolDown", 20000)) >= System
						.currentTimeMillis()) {
			return false;
		}
		Bukkit.getPluginManager().callEvent(new BTEPromotionalTeamEvent(team));
		TeamGui.openGui(player, "Teaminfo", 0);
		return true;
	}

}
