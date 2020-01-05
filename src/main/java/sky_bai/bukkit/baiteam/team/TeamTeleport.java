package sky_bai.bukkit.baiteam.team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamTeleport {
	public static Map<String, Location> LocationMap = new HashMap<String, Location>();

	public static Map<Long, String> UuidTime = new HashMap<Long, String>();

	public static Map<String, Set<Player>> TeleportPlayer = new HashMap<String, Set<Player>>();
}
