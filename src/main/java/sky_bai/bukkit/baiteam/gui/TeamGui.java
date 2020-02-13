package sky_bai.bukkit.baiteam.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.Team;
import sky_bai.bukkit.baiteam.util.BTTools;

public class TeamGui {

	public static void openGui(Player player, String name, int i) {
		FileConfiguration config = BTConfig.getGui().getConfig();
		List<String> strs = config.getStringList(name);
		if (strs.size() < 1) {
			openPageGui(player, config.getConfigurationSection(name));
			return;
		}
		BookGui bookGui = BookGui.getBookGui();
		bookGui.addPage(setPage(player, strs, i));
		bookGui.openBook(player);
	}

	private static void openPageGui(Player player, ConfigurationSection config) {
		BookGui bookGui = BookGui.getBookGui();
		Set<String> keys = config.getKeys(false);
		for (String key : keys) {
			List<String> strs = config.getStringList(key);
			bookGui.addPage(setPage(player, strs, 0));
		}
		bookGui.openBook(player);
	}

	private static TextComponent setPage(Player player, List<String> strs, int i) {
		TextComponent mes = new TextComponent();
		for (String str : strs) {
			String str1 = setPlaceholder(str, i);
			str1 = PlaceholderAPI.setPlaceholders(player, str1);
			if (str1.startsWith("[Text]", 0)) {
				mes.addExtra(str1.replace("[Text]", ""));
			} else if (str1.startsWith("[Action]", 0)) {
				TextComponent tc = runAction(player, str1.replace("[Action]", ""), i);
				if (tc != null) {
					mes.addExtra(tc);
				}
			} else if (str1.startsWith("[Centered]", 0)) {
				mes.addExtra(runSize(str1.replace("[Centered]", ""), i));
			} else if (str1.startsWith("[Module]", 0)) {
				mes.addExtra(runModule(player, str1.replace("[Module]", ""), i));
			}
		}
		return mes;
	}

	private static String setPlaceholder(String str, int page) {
		str = setPlaceholder(str, page, 0, 0);
		return str;
	}

	private static String setPlaceholder(String str, int page, int size, int i) {
		str = str.replaceAll("&<p>", "" + page);
		str = str.replaceAll("&<p\\+>", "" + (page + 1));
		str = str.replaceAll("&<p->", "" + (page - 1));
		str = str.replaceAll("\\&\\<\\?\\>", "" + ((size * page) + i));
		return str;
	}

	private static String runSize(String text, int page) {
		if (text.indexOf("[") != 0 || text.indexOf("]") == -1) {
			return "";
		}
		String str = text.substring(1, text.indexOf("]"));
		text = setPlaceholder(text, page);
		text = PlaceholderAPI.setPlaceholders(null, text);
		Double size = 0.0;
		try {
			size = Double.valueOf(str);
		} catch (NumberFormatException e) {
			System.out.println(str + "不是数字");
		}
		return BTTools.setStringCentered(text.replace("[" + str + "]", ""), size);
	}

	private static TextComponent runAction(Player player, String text, int i) {
		ConfigurationSection config = BTConfig.getAction().getConfig().getConfigurationSection(text);
		Set<String> keys = config.getKeys(false);
		if (keys.contains("Text") == false) {
			return null;
		}
		BTMessage.Action ac1 = BTMessage.Action.setAction(PlaceholderAPI.setPlaceholders(player, setPlaceholder(config.getString("Text"), i)));
		if (keys.contains("Command")) {
			ac1.setCommand(PlaceholderAPI.setPlaceholders(player, setPlaceholder(config.getString("Command"), i)));
		}
		if (keys.contains("ShowText")) {
			ac1.setText(PlaceholderAPI.setPlaceholders(player, setPlaceholder(config.getString("ShowText"), i)));
		}
		return ac1.getText();
	}

	private static TextComponent runCustomModuleAction(Player player, String text, int size, int page, int i) {
		ConfigurationSection config = BTConfig.getAction().getConfig().getConfigurationSection(text);
		Set<String> keys = config.getKeys(false);
		if (keys.contains("Text") == false) {
			return null;
		}
		String Text = config.getString("Text");
		Text = setPlaceholder(Text, page, size, i);
		Text = PlaceholderAPI.setPlaceholders(player, Text);
		BTMessage.Action ac1 = BTMessage.Action.setAction(Text);
		if (keys.contains("Command")) {
			String str = config.getString("Command");
			str = setPlaceholder(str, page, size, i);
			str = PlaceholderAPI.setPlaceholders(player, str);
			ac1.setCommand(str);
		}
		if (keys.contains("ShowText")) {
			String str = config.getString("ShowText");
			str = setPlaceholder(str, page, size, i);
			str = PlaceholderAPI.setPlaceholders(player, str);
			ac1.setText(str);
		}
		return ac1.getText();
	}

	private static TextComponent runModule(Player player, String text, int page) {
		TextComponent mes = new TextComponent();
		if (text.indexOf("[") != 0 || text.indexOf("]") == -1) {
			return mes;
		}
		String str = text.substring(1, text.indexOf("]"));
		Integer size = 0;
		try {
			size = Integer.valueOf(str);
		} catch (NumberFormatException e) {
			System.out.println(str + "不是整数");
		}
		ConfigurationSection config = BTConfig.getModule().getConfig().getConfigurationSection(text.replace("[" + str + "]", ""));
		String type = config.getString("Type");
		List<String> moduleList = config.getStringList("List");
		int nullint = config.getInt("Row");
		switch (type) {
		case "Player": {
			String collection = config.getString("Collection");
			switch (collection) {
			case "MembersNoLeader": {
				Team team = BaiTeam.getTeamManager().getTeam(player, false);
				List<Player> players = new ArrayList<Player>(team.getMembers());
				players.remove(team.getLeader());
				mes.addExtra(getPlayerModule(players, moduleList, nullint, size, page));
				break;
			}
			}
			break;
		}
		case "Custom": {
			int listSize = config.getInt("Amount", 0);
			if (listSize < 1) {
				try {
					listSize = Integer.valueOf(PlaceholderAPI.setPlaceholders(null, config.getString("Amount")));
				} catch (NumberFormatException e) {
					System.out.println(PlaceholderAPI.setPlaceholders(null, config.getString("Amount")) + "不是整数");
				}
			}
			mes.addExtra(getCustomModule(listSize, moduleList, nullint, size, page));
			break;
		}
		}
		return mes;
	}

	private static TextComponent getModuleList(Player player, List<String> strs, int i) {
		TextComponent mes = new TextComponent();
		for (String str : strs) {
			String str1 = setPlaceholder(str, i);
			str1 = PlaceholderAPI.setPlaceholders(player, str);
			if (str1.startsWith("[Text]", 0)) {
				mes.addExtra(str1.replace("[Text]", ""));
			} else if (str1.startsWith("[Action]", 0)) {
				TextComponent tc = runAction(player, str1.replace("[Action]", ""), i);
				if (tc != null) {
					mes.addExtra(tc);
				}
			} else if (str1.startsWith("[Centered]", 0)) {
				mes.addExtra(runSize(str1.replace("[Centered]", ""), i));
			}
		}
		return mes;
	}

	private static TextComponent getPlayerModule(List<Player> players, List<String> strs, int nullint, int size, int page) {
		page = getPageInt(players.size(), size, page);
		TextComponent mes = new TextComponent();
		int i1 = size;
		int listSize = players.size() ;
		if (listSize - size * page != size) {
			i1 = players.size() - size * page;
		}
		if (listSize == 0) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < nullint; j++) {
					mes.addExtra("\n");
				}
			}
		}
		for (int i = 0; i < i1; i++) {
			if (players.isEmpty()) {
				continue;
			}
			mes.addExtra(getModuleList(players.get((size * page) + i), strs, page));
		}
		if (i1 != size) {
			for (int i = 0; i < size - i1; i++) {
				for (int j = 0; j < nullint; j++) {
					mes.addExtra("\n");
				}
			}
		}
		return mes;
	}

	private static TextComponent getCustomModule(int listSize, List<String> strs, int nullint, int size, int page) {
		page = getPageInt(listSize, size, page);
		TextComponent mes = new TextComponent();
		int i1 = size;
		if (listSize - size * page != size) {
			i1 = listSize - size * page;
		}
		if (listSize == 0) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < nullint; j++) {
					mes.addExtra("\n");
				}
			}
		}
		for (int i = 0; i < listSize; i++) {
			TextComponent mes1 = new TextComponent();
			for (String str : strs) {
				str = setPlaceholder(str, page, size, i);
				str = PlaceholderAPI.setPlaceholders(null, str);
				if (str.startsWith("[Text]", 0)) {
					mes1.addExtra(str.replace("[Text]", ""));
				} else if (str.startsWith("[Action]", 0)) {
					TextComponent tc = runCustomModuleAction(null, str.replace("[Action]", ""), size, page, i);
					if (tc != null) {
						mes1.addExtra(tc);
					}
				} else if (str.startsWith("[Centered]", 0)) {
					mes1.addExtra(runSize(str.replace("[Centered]", ""), i));
				}
			}
			mes.addExtra(mes1);
		}
		if (i1 != size) {
			for (int i = 0; i < size - i1; i++) {
				for (int j = 0; j < nullint; j++) {
					mes.addExtra("\n");
				}
			}
		}
		return mes;
	}

	private static int getPageInt(int listSize, int size, int page) {
		page = page < 0 ? 0 : page;
		while (listSize < size * page || listSize - size * page == 0) {
			page = page - 1;
		}
		return page;
	}
}
