package sky_bai.bukkit.baiteam.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.event.dplayer.DPlayerLeaveDGroupEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.event.BTAcceptPlayerApplyEvent;
import sky_bai.bukkit.baiteam.event.BTAcceptTeamInviteEvent;
import sky_bai.bukkit.baiteam.event.BTCreateTeamEvent;
import sky_bai.bukkit.baiteam.event.BTKickPlayerEvent;
import sky_bai.bukkit.baiteam.event.BTLeaveTeamEvent;
import sky_bai.bukkit.baiteam.event.BTPlayerApplyEvent;
import sky_bai.bukkit.baiteam.event.BTPromotionalTeamEvent;
import sky_bai.bukkit.baiteam.event.BTTeamInviteEvent;
import sky_bai.bukkit.baiteam.event.BTTransferTeamEvent;
import sky_bai.bukkit.baiteam.gui.TeamGui;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamTeleport;

public class BTCommand {
	public static boolean ifPlayerTeleport(Player player1, Player player2) {
		if (player1.getWorld() != player2.getWorld()) {
			return false;
		}
		double x = (player1.getLocation().getX() > player2.getLocation().getX()) ? (player1.getLocation().getX() - player2.getLocation().getX()) : (player2.getLocation().getX() - player1.getLocation().getX());
		double y = (player1.getLocation().getY() > player2.getLocation().getY()) ? (player1.getLocation().getY() - player2.getLocation().getY()) : (player2.getLocation().getY() - player1.getLocation().getY());
		double z = (player1.getLocation().getZ() > player2.getLocation().getZ()) ? (player1.getLocation().getZ() - player2.getLocation().getZ()) : (player2.getLocation().getZ() - player1.getLocation().getZ());
		return ((x + y + z) <= 10);
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
			World world = (args.length > 6 && Bukkit.getWorld(args[6]) != null) ? Bukkit.getWorld(args[6]) : player.getWorld();
			Location location = new Location(world, x, y, z);
			String uuid = UUID.randomUUID().toString();
			while (TeamTeleport.LocationMap.containsKey(uuid)) {
				uuid = UUID.randomUUID().toString();
			}
			long time = System.currentTimeMillis();
			while (TeamTeleport.UuidTime.containsKey(time)) {
				time = time + 1;
			}
			TeamTeleport.LocationMap.put(uuid, location);
			Set<Player> tpPlayers = new HashSet<Player>();
			tpPlayers.add(player);
			TeamTeleport.TeleportPlayer.put(uuid, tpPlayers);
			TeamTeleport.UuidTime.put(time, uuid);
			player.teleport(location);
			if (team != null && team.getLeader() == player) {
				Set<Player> players = team.getMembers();
				for (Player player2 : players) {
					if (player2 != player && ifPlayerTeleport(player, player2)) {
						BTMessage.Action action = BTMessage.Action.setAction(BTMessage.Button.Teleport_Yes.getMes(), "/baiteam Teleport Yes " + uuid, BTMessage.Button.Text_Teleport_Yes.getMes());
						BTMessage.send(player2, BTMessage.Team.Teleport_Member, Arrays.asList(team.getTeamName(), player.getName(), location.toString()), action);
					}
				}
			}
		} else if (args[1].equalsIgnoreCase("yes") && args.length > 2) {
			String uuid = args[2];
			if (TeamTeleport.LocationMap.containsKey(uuid) == false) {
				sender.sendMessage(BTMessage.Error.OnTeleportIsNo.getMes());
				return false;
			}
			if (TeamTeleport.TeleportPlayer.get(uuid).contains(sender)) {
				sender.sendMessage(BTMessage.Error.OnTeleportIsNo.getMes());
				return false;
			}
			sender.teleport(TeamTeleport.LocationMap.get(uuid));
			TeamTeleport.TeleportPlayer.get(uuid).add(sender);
		}
		return true;
	}

	public static boolean kickTeamFoDungeon(Player player, String[] args) {
		if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, false);
		Set<Player> players = team.getMembers();
		for (Player player2 : players) {
			DGlobalPlayer dPlayer = DungeonsXL.getInstance().getDPlayerCache().getByPlayer(player2);
			Game game = Game.getByPlayer(player);
			if (game != null && game.isTutorial()) {
				return false;
			}
			DGroup dGroup = DGroup.getByPlayer(player);
			if (dGroup == null && !(dPlayer instanceof DEditPlayer)) {
				return false;
			}
			if (dPlayer instanceof DGamePlayer) {
				DGamePlayerEscapeEvent dPlayerEscapeEvent = new DGamePlayerEscapeEvent((DGamePlayer) dPlayer);
				Bukkit.getPluginManager().callEvent(dPlayerEscapeEvent);
				if (dPlayerEscapeEvent.isCancelled()) {
					return false;
				}
			}
			final DPlayerLeaveDGroupEvent dPlayerLeaveDGroupEvent = new DPlayerLeaveDGroupEvent(dPlayer, dGroup);
			Bukkit.getPluginManager().callEvent(dPlayerLeaveDGroupEvent);
			if (dPlayerLeaveDGroupEvent.isCancelled()) {
				return false;
			}
			if (dPlayer instanceof DInstancePlayer) {
				((DInstancePlayer) dPlayer).leave();
			} else {
				dGroup.removePlayer(player);
			}
		}
		return true;
	}

	public static boolean openGui(Player sender, String[] args) {
		if (args.length <= 1) {
			if (BaiTeam.getTeamManager().ifOnTeam(sender) == false) {
				TeamGui.getGui().openMainGui(sender);
			} else {
				TeamGui.getGui().openTeamInfoGui(sender, BaiTeam.getTeamManager().getTeam(sender, false));
			}
			return true;
		}
		switch (args[1].toLowerCase()) {
		case "main": {
			TeamGui.getGui().openMainGui(sender);
			return true;
		}
		case "teaminfo": {
			if (BaiTeam.getTeamManager().ifOnTeam(sender) == false) {
				return false;
			}
			TeamGui.getGui().openTeamInfoGui(sender, BaiTeam.getTeamManager().getTeam(sender, false));
			return true;
		}
		case "teamlist": {
			if (BaiTeam.getTeamManager().ifOnTeam(sender)) {
				return false;
			}
			if (BaiTeam.getTeamManager().getTeams().isEmpty()) {
				TeamGui.getGui().openMainGui(sender);
				return false;
			}
			int i1 = 0;
			if (args.length >= 4 && Integer.valueOf(args[3]) != null) {
				i1 = Integer.valueOf(args[3]);
			}
			List<Team> teams = new ArrayList<Team>();
			for (Team team : BaiTeam.getTeamManager().getTeams()) {
				if (team.getMembers().size() < BTConfig.getConfig().getConfig().getInt("TeamSize", 5)) {
					teams.add(team);
				}
			}
			if (teams.isEmpty()) {
				TeamGui.getGui().openMainGui(sender);
				return false;
			}
			TeamGui.getGui().openTeamListGui(sender, teams, i1);
			return true;
		}
		case "playerlist": {
			if (BaiTeam.getTeamManager().ifOnTeam(sender) == false) {
				return false;
			}
			Team team = BaiTeam.getTeamManager().getTeam(sender, true);
			if (team == null) {
				return false;
			}
			int i1 = 0;
			if (args.length >= 4 && Integer.valueOf(args[3]) != null) {
				i1 = Integer.valueOf(args[3]);
			}
			List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
			List<Player> players2 = new ArrayList<Player>();
			for (OfflinePlayer offlinePlayer : players) {
				if (offlinePlayer.isOnline() && BaiTeam.getTeamManager().ifOnTeam((Player) offlinePlayer) == false) {
					players2.add((Player) offlinePlayer);
				}
			}
			if (players2.isEmpty()) {
				TeamGui.getGui().openTeamInfoGui(sender, team);
				return false;
			}
			TeamGui.getGui().openPlayerListGui(sender, players2, i1);
			return true;
		}
		}
		return false;

	}

	public static boolean play(Player player, String[] args) {
		DungeonsXL dungeonsXL = DungeonsXL.getInstance();
		if (args.length <= 2) {
			return false;
		}
		if (dungeonsXL.getDWorldCache().getResourceByName(args[2]) == null || dungeonsXL.getDPlayerCache().getByPlayer(player) instanceof DInstancePlayer) {
			return false;
		}
		DResourceWorld resource = dungeonsXL.getDWorldCache().getResourceByName(args[2]);
		Dungeon dungeon = new Dungeon(dungeonsXL, resource);

		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			DGroup group = new DGroup(dungeonsXL, player.getName() + "Dun", player, dungeon);
			DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
			new Game(dungeonsXL, group, gameWorld);
			for (Player groupPlayer : group.getPlayers().getOnlinePlayers()) {
				new DGamePlayer(dungeonsXL, groupPlayer, group.getGameWorld());
			}
			return true;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, true);
		List<Player> members = new ArrayList<Player>(team.getMembers());
		members.remove(player);
		for (Player player2 : members) {
			if (ifPlayerTeleport(player, player2)) {
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

	public static boolean create(Player player, String[] args) {
		String name = player.getName();
		if (args.length > 1) {
			name = args[1];
		}
		int i1 = 0;
		while (BaiTeam.getTeamManager().ifTeam(name)) {
			name = name + "#" + i1;
			i1++;
		}
		Team team = new Team(player, name);
		BTCreateTeamEvent.run(team, player);
		return true;
	}

	public static boolean leave(Player player, String[] args) {
		if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoTeam, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(player, false);
		BTLeaveTeamEvent.run(team, player);
		return true;
	}
	
	public static boolean applyto(Player player, String[] args) {
		if (args.length <= 1) {
			return false;
		}
		BTPlayerApplyEvent.run(BaiTeam.getTeamManager().getTeam(args[1]), player);
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
		BTTeamInviteEvent.run(BaiTeam.getTeamManager().getTeam(player, true), Bukkit.getPlayer(args[1]), player);
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
		BTTransferTeamEvent.run(BaiTeam.getTeamManager().getTeam(player, true), Bukkit.getPlayer(args[1]));
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
		BTTransferTeamEvent.run(team, Bukkit.getPlayer(args[1]));
		TeamGui.getGui().openTeamInfoGui(player, team);
		TeamGui.getGui().openTeamInfoGui(Bukkit.getPlayer(args[1]), team);
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
		BTKickPlayerEvent.run(team, Bukkit.getPlayer(args[1]));
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
		BTKickPlayerEvent.run(team, Bukkit.getPlayer(args[1]));
		TeamGui.getGui().openTeamInfoGui(player, team);
		return true;
	}
	
	public static boolean apply(Player player, String[] args) {
		if (args.length <= 1) {
			return false;
		}
		switch (args[1].toLowerCase()) {
		case "yes": {
			if (args.length <= 2 || Bukkit.getPlayer(args[2]) == null) {
				return false;
			}
			Player player2 = Bukkit.getPlayer(args[2]);
			if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
				BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
				return false;
			}
			Team team = BaiTeam.getTeamManager().getTeam(player,true);
			BTAcceptPlayerApplyEvent.run(team, player2, true);
			return true;
		}
		case "no": {
			if (args.length <= 2 || Bukkit.getPlayer(args[2]) == null) {
				return false;
			}
			Player player2 = Bukkit.getPlayer(args[2]);
			if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
				BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
				return false;
			}
			Team team = BaiTeam.getTeamManager().getTeam(player,true);
			BTAcceptPlayerApplyEvent.run(team, player2, false);
			return true;
		}
		}
		return false;
	}
	
	public static boolean invite(Player player, String[] args) {
		if (args.length <= 1) {
			return false;
		}
		switch (args[1].toLowerCase()) {
		case "yes": {
			if (args.length <= 2 || BaiTeam.getTeamManager().ifTeam(args[2]) == false) {
				return false;
			}
			Team team = BaiTeam.getTeamManager().getTeam(args[2]);
			BTAcceptTeamInviteEvent.run(team, player, true);
			return true;
		}
		case "no": {
			if (args.length <= 2 || BaiTeam.getTeamManager().ifTeam(args[2]) == false) {
				return false;
			}
			Team team = BaiTeam.getTeamManager().getTeam(args[2]);
			BTAcceptTeamInviteEvent.run(team, player, false);
			return true;
		}
		}
		return false;
	}
	
	public static boolean promotional(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(args[1]);
		BTPromotionalTeamEvent.run(team);
		return true;
	}
	
	public static boolean guipromotional(Player player, String[] args) {
		if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
			BTMessage.send(player, BTMessage.Error.OnPlayerNoLeader, null);
			return false;
		}
		Team team = BaiTeam.getTeamManager().getTeam(args[1]);
		BTPromotionalTeamEvent.run(team);
		TeamGui.getGui().openTeamInfoGui(player, team);
		return true;
	}
	
}
