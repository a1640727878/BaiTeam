package sky_bai.bukkit.baiteam.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class BTTools {
	// 获取字符串长度
	public static double getStringLength(String text) {
		List<String> strs = Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§m", "§n");
		for (String str : strs) {
			text.replaceAll("\\" + str, "");
		}
		double d = 0.0;
		char[] chs = text.toCharArray();
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

	// 添加空格
	private static String addSpace(int i) {
		String str = "";
		for (int j = 0; j < i; j++) {
			str = str + " ";
		}
		return str;
	}

	// 在指定长度中居中
	public static String setStringCentered(String text, Double d) {
		String str = "";
		double d1 = getStringLength(text);
		if (d1 > d) {
			str = text.substring(0, (int) (d - 3)) + "...";
		} else {
			int i = (int) ((d - d1) / 2);
			str = addSpace(i) + text + addSpace(i);
		}
		return str;
	}
}
