package sky_bai.bukkit.baiteam.team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

public class Team {
	// 队长
	private Player leader;
	// 队员们
	private Set<Player> members = new HashSet<Player>();
	// 队伍名
	private String teamName;
	// 入队申请表
	private Set<Player> onJoinPlayer = new HashSet<Player>();
	// 邀请申请表
	private Set<Player> onInvitePlayer = new HashSet<Player>();
	
	public Team(Player player,String name) {
		leader = player;
		members.add(player);
		teamName = name;
	}
	
	public void setLeader(Player player) {
		leader = player;
	}
	
	public Player getLeader() {
		Player player = leader;
		return player;
	}

	public String getLeaderName() {
		return leader.getName();
	}
	
	public void setTeamName(String name) {
		teamName = name;
	}

	public String getTeamName() {
		String name = teamName;
		return name;
	}
	
	public Set<Player> getMembers() {
		Set<Player> players = members;
		return players;
	}
	
	public List<String> getMemberNames() {
		List<String> names = new ArrayList<String>();
		for (Player player : members) {
			names.add(player.getName());
		}
		return names;
	}
	
	public void addMembers(Player player) {
		members.add(player);
	}
	
	public void delMember(Player player) {
		members.remove(player);
	}
	
	public Set<Player> getOnInvitePlayer() {
		Set<Player> players = onInvitePlayer;
		return players;
	}
	
	public void addInvitePlayer(Player player) {
		onInvitePlayer.add(player);
	}
	
	public void delInvitePlayer(Player player) {
		onInvitePlayer.remove(player);
	}
	
	public Set<Player> getOnJoinPlayer() {
		Set<Player> players = onJoinPlayer;
		return players;
	}
	
	public void addJoinPlayer(Player player) {
		onJoinPlayer.add(player);
	}
	
	public void deJoinPlayer(Player player) {
		onJoinPlayer.remove(player);
	}
	
}
