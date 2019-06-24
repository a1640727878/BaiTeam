package sky_bai.bukkit.baiteam.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Team {
	// 队长
	private Player leader;
	// 队员
	private List<Player> members;
	// 队伍名
	private String teamName;
	// 申请入队
	private List<Player> onJoinPlayer;
	// 邀请入队
	private List<Player> onInvitePlayer;
	public Team(Player leader, String teamName) {
		this.members = new ArrayList<Player>();
		this.onJoinPlayer = new ArrayList<Player>();
		this.onInvitePlayer = new ArrayList<Player>();
		this.teamName = teamName;
		setLeader(leader);
		addMember(leader);
	}

	public void setLeader(Player player) {
		this.leader = player;
	}
	
	public Player getLeader() {
		return leader;
	}

	public List<Player> getMembers() {
		return members;
	}

	public String getTeamName() {
		return teamName;
	}

	public List<Player> getOnJoinPlayer() {
		return onJoinPlayer;
	}
	
	public List<Player> getOnInvitePlayer() {
		return onInvitePlayer;
	}
	
	public void addJoinPlayer(Player player) {
		onJoinPlayer.add(player);
	}

	public void delJoinPlayer(Player player) {
		onJoinPlayer.remove(player);
	}
	
	public void addInvitePlayer(Player player) {
		onInvitePlayer.add(player);
	}
	
	public void delInvitePlayer(Player player) {
		onInvitePlayer.remove(player);
	}
	
	public void addMember(Player player) {
		members.add(player);
	}

	public void delMember(Player player) {
		members.remove(player);
	}
}
