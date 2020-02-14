package sky_bai.bukkit.baiteam.config.message;

import java.io.File;

import sky_bai.bukkit.baiteam.BaiTeam;

public class BTConfigMes {
	private final static File messageConfigPath = new File(BaiTeam.getInstance().getDataFolder(), "message");
	
	public static File getMessageconfigpath() {
		return messageConfigPath;
	}
}
