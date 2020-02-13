package sky_bai.bukkit.baiteam.event;

import org.bukkit.entity.Player;

import sky_bai.bukkit.baiteam.team.Team;

public class BTEAcceptTeamInviteEvent extends BaiTeamEvent {
	
	private Boolean isApply;

	public BTEAcceptTeamInviteEvent(Team team, Player player,Boolean isApply) {
		super(team, player);
		this.isApply = isApply;
	}

	public Boolean getIsApply() {
		return isApply;
	}

	public void setIsApply(Boolean isApply) {
		this.isApply = isApply;
	}
	
}
