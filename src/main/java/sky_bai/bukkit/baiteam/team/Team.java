package sky_bai.bukkit.baiteam.team;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class Team {
	// 队长
	private Player leader = null;
	// 队员们
	private Set<Player> members = new HashSet<Player>();
	// 队伍名
	private String teamName = null;
	// 申请入队
	private Set<Player> onJoinPlayer = new HashSet<Player>();
	// 邀请入队
	private Set<Player> onInvitePlayer = new HashSet<Player>();

	public Team(Player leader, String teamName) {
		setTeamName(teamName);
		setLeader(leader);
		addMembers(leader);
	}

	public void setLeader(Player leader) {
		this.leader = leader;
	}

	public Player getLeader() {
		Player player = leader;
		return player;
	}

	public Team addMembers(Player player) {
		members.add(player);
		return this;
	}
	
	public Team delMember(Player player) {
		members.remove(player);
		return this;
	}

	public Set<Player> getMembers() {
		Set<Player> players = members;
		return players;
	}

	public Team addOnInvitePlayer(Player player) {
		onInvitePlayer.add(player);
		return this;
	}
	
	public Team delOnInvitePlayer(Player player) {
		onInvitePlayer.remove(player);
		return this;
	}

	public Set<Player> getOnInvitePlayer() {
		Set<Player> players = onInvitePlayer;
		return players;
	}

	public Team addOnJoinPlayer(Player player) {
		onJoinPlayer.add(player);
		return this;
	}

	public Team delOnJoinPlayer(Player player) {
		onJoinPlayer.remove(player);
		return this;
	}
	
	public Set<Player> getOnJoinPlayer() {
		Set<Player> players = onJoinPlayer;
		return players;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamName() {
		String string = teamName;
		return string;
	}

}
