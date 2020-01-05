package sky_bai.bukkit.baiteam.event;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.BaiMessage;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.team.Team;

public class BTCreateTeamEvent extends BaiTeamEvent {

	public BTCreateTeamEvent(Team team, Player player) {
		super(team, player);
	}

	public static void run(Team team, Player player) {
		BTCreateTeamEvent bCreateTeamEvent = new BTCreateTeamEvent(team, player);
		Bukkit.getPluginManager().callEvent(bCreateTeamEvent);
		if (bCreateTeamEvent.isCancelled() == false) {
			bCreateTeamEvent.run();
		}
	}

	private void run() {
		if (BaiTeam.getTeamManager().ifOnTeam(getPlayer())) {
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnPlayerOnTeam, null);
			return;
		}
		if (BaiTeam.getTeamManager().ifTeam(getTeam())) {
			BaiMessage.send(getPlayer(), BaiMessage.Error.OnTeamNameIsUse, null);
			return;
		}
		BaiTeam.getTeamManager().addTeam(getTeam());
		BaiMessage.send(getPlayer(), BaiMessage.TeamMesEnum.Create, Arrays.asList(getTeam().getTeamName(), getPlayer().getName()));
	}

}
