package sky_bai.bukkit.baiteam.config.gui;

import java.io.File;

import sky_bai.bukkit.baiteam.BaiTeam;

public class BTConfigGui {
	private static File guiConfigPath = new File(BaiTeam.getInstance().getDataFolder(), "gui");
	
	public static File getGuiConfigPath() {
		return guiConfigPath;
	}
	
}
