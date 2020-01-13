package sky_bai.bukkit.baiteam;

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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import sky_bai.bukkit.baiteam.book.TeamGui;
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
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.team.TeamTeleport;

public class BaiCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (sender instanceof Player) ? (Player) sender : null;
		if (player == null) {
			BaiTeam.getBaiTeam().getLogger().info("这些命令只能玩家执行");
			return false;
		}
		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
			case "teleport": {
				return teleport(player, args);
			}
			case "play": {
				return play(player, args);
			}
			case "opengui": {
				return openGui(player, args);
			}
			case "create": {
				String name = player.getName();
				if (args.length > 1) {
					name = args[1];
				}
				int i1 = 0;
				while (BaiTeam.getTeamManager().ifTeam(name)) {
					name = name + "#" + i1;
					i1++;
				}
				Team team = new Team((Player) sender, name);
				BTCreateTeamEvent.run(team, (Player) sender);
				return true;
			}
			case "leave": {
				if (BaiTeam.getTeamManager().ifOnTeam(player) == false) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoTeam, null);
					return false;
				}
				Team team = BaiTeam.getTeamManager().getTeam(player, false);
				BTLeaveTeamEvent.run(team, player);
				return true;
			}
			case "applyto": {
				if (args.length <= 1) {
					return false;
				}
				BTPlayerApplyEvent.run(BaiTeam.getTeamManager().getTeam(args[1]), player);
				return true;
			}
			case "inviteto": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
					return false;
				}
				if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				BTTeamInviteEvent.run(BaiTeam.getTeamManager().getTeam(player, true), Bukkit.getPlayer(args[1]), player);
				return true;
			}
			case "transfer": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
					return false;
				}
				if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				BTTransferTeamEvent.run(BaiTeam.getTeamManager().getTeam(player, true), Bukkit.getPlayer(args[1]));
				return true;
			}
			case "guitransfer": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
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
			case "kick": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
					return false;
				}
				Team team = BaiTeam.getTeamManager().getTeam(player, true);
				if (args.length <= 1 || Bukkit.getPlayer(args[1]) == null) {
					return false;
				}
				BTKickPlayerEvent.run(team, Bukkit.getPlayer(args[1]));
				return true;
			}
			case "guikick": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
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
			case "apply": {
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
						BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
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
						BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
						return false;
					}
					Team team = BaiTeam.getTeamManager().getTeam(player,true);
					BTAcceptPlayerApplyEvent.run(team, player2, false);
					return true;
				}
				}
			}
			case "invite": {
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
			}
			case "promotional": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
					return false;
				}
				Team team = BaiTeam.getTeamManager().getTeam(args[3]);
				BTPromotionalTeamEvent.run(team);
				return true;
			}
			case "guipromotional": {
				if (BaiTeam.getTeamManager().getTeam(player, true) == null) {
					BaiMessage.send(player, BaiMessage.Error.OnPlayerNoLeader, null);
					return false;
				}
				Team team = BaiTeam.getTeamManager().getTeam(args[3]);
				BTPromotionalTeamEvent.run(team);
				TeamGui.getGui().openTeamInfoGui(player, team);
				return true;
			}
			}
			player.sendMessage(BaiMessage.getMesPrefix() + "你甚至忘记了写参数!");
			return false;
		}
		return false;
	}

	private boolean teleport(Player sender, String[] args) {
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
					if (player2 != player) {
						BaiMessage.Action action = BaiMessage.Action.setAction(BaiMessage.Button.Teleport_Yes.getMes(), "/baiteam Teleport Yes " + uuid, BaiMessage.Button.Text_Teleport_Yes.getMes());
						BaiMessage.send(player2, BaiMessage.TeamMesEnum.Teleport_Member, Arrays.asList(team.getTeamName(), player.getName(), location.toString()), action);
					}
				}
			}
		} else if (args[1].equalsIgnoreCase("yes") && args.length > 2) {
			String uuid = args[2];
			if (TeamTeleport.LocationMap.containsKey(uuid) == false) {
				sender.sendMessage(BaiMessage.Error.OnTeleportIsNo.getMes());
				return false;
			}
			if (TeamTeleport.TeleportPlayer.get(uuid).contains(sender)) {
				sender.sendMessage(BaiMessage.Error.OnTeleportIsNo.getMes());
				return false;
			}
			sender.teleport(TeamTeleport.LocationMap.get(uuid));
			TeamTeleport.TeleportPlayer.get(uuid).add(sender);
		}
		return true;
	}

	private boolean play(Player sender, String[] args) {
		DungeonsXL dungeonsXL = DungeonsXL.getInstance();
		if (args.length <= 1) {
			return false;
		}
		if (dungeonsXL.getDWorldCache().getResourceByName(args[1]) == null || BaiTeam.getTeamManager().getTeam(sender, true) == null || dungeonsXL.getDPlayerCache().getByPlayer(sender) instanceof DInstancePlayer) {
			return false;
		}
		DResourceWorld resource = dungeonsXL.getDWorldCache().getResourceByName(args[1]);
		Dungeon dungeon = new Dungeon(dungeonsXL, resource);
		Team team = BaiTeam.getTeamManager().getTeam(sender, true);
		List<Player> members = new ArrayList<Player>(team.getMembers());
		members.remove(sender);
		DGroup group = new DGroup(dungeonsXL, team.getTeamName(), sender, members, dungeon);
		DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
		new Game(dungeonsXL, group, gameWorld);
		for (Player groupPlayer : group.getPlayers().getOnlinePlayers()) {
			new DGamePlayer(dungeonsXL, groupPlayer, group.getGameWorld());
		}
		return true;
	}

	private boolean openGui(Player sender, String[] args) {
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

}
