package sky_bai.bukkit.baiteam.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import sky_bai.bukkit.baiteam.team.Team;

public class BaiTeamEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private boolean isCancelled;

	private Team team;
	private Player player;

	public BaiTeamEvent(Team team, Player player) {
		this.team = team;
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

}
