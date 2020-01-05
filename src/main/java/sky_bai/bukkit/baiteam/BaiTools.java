package sky_bai.bukkit.baiteam;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class BaiTools {

	public static double getStringLength(String string) {
		List<String> strs = Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§m", "§n");
		for (String string2 : strs) {
			string.replaceAll("\\" + string2, "");
		}
		double d = 0.0;
		char[] chs = string.toCharArray();
		try {
			for (char c : chs) {
				String str = "" + c;
				if (str.getBytes("UTF-8").length > 1) {
					d = d + 2.5;
				} else {
					d = d + 1.0;
				}
			}
			return d;
		} catch (UnsupportedEncodingException e) {
			return d;
		}
	}

	public static String setStringCentered(String string, Double d) {
		String str = "";
		if (BaiTools.getStringLength(string) > d) {
			return str;
		} else if (BaiTools.getStringLength(string) < d) {
			int i1 = (int) ((d - BaiTools.getStringLength(string)) / 2);
			for (int i = 0; i < i1; i++) {
				str = " " + str;
			}
		}
		return str;
	}

	public static String setStringCentered(String string) {
		return setStringCentered(string, 29.0);
	}

	public static String setStringRright(String string, Double d) {
		String str = "";
		if (BaiTools.getStringLength(string) > d) {
			return str;
		}else if (BaiTools.getStringLength(string) < d) {
			int i1 = (int) ((d - BaiTools.getStringLength(string)));
			for (int i = 0; i < i1; i++) {
				str = " " + str;
			}
		}
		return str;
	}
}
