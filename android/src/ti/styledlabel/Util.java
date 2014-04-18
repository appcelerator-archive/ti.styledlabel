/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel;

import java.util.HashMap;

import org.appcelerator.kroll.common.Log;

import android.text.ParcelableSpan;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Holds various static methods that we will use throughout the module.
 * @author Dawson Toth, Appcelerator Inc.
 */
public final class Util {

	/**
	 * Prevents instantiation.
	 */
	private Util() {}
	
	/*
	 * These 8 methods are useful for logging purposes -- they make what we do in this module a tiny bit easier.
	 */
	public static void d(String msg) {
		Log.d(Constants.LCAT, msg);
	}
	public static void d(String msg, Throwable e) {
		Log.d(Constants.LCAT, msg, e);
	}
	
	public static void i(String msg) {
		Log.i(Constants.LCAT, msg);
	}
	public static void i(String msg, Throwable e) {
		Log.i(Constants.LCAT, msg, e);
	}

	public static void w(String msg) {
		Log.w(Constants.LCAT, msg);
	}
	public static void w(String msg, Throwable e) {
		Log.w(Constants.LCAT, msg, e);
	}
	
	public static void e(String msg) {
		Log.e(Constants.LCAT, msg);
	}
	public static void e(String msg, Throwable e) {
		Log.e(Constants.LCAT, msg, e);
	}
	
	/*
	 * Colors.
	 */
	private static final HashMap<String, Integer> COLORS = buildColorMap();
	
	private static HashMap<String, Integer> buildColorMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("aqua", 0x00FFFF);
		map.put("black", 0x000000);
		map.put("blue", 0x0000FF);
		map.put("fuchsia", 0xFF00FF);
		map.put("green", 0x008000);
		map.put("grey", 0x808080);
		map.put("lime", 0x00FF00);
		map.put("maroon", 0x800000);
		map.put("navy", 0x000080);
		map.put("olive", 0x808000);
		map.put("purple", 0x800080);
		map.put("red", 0xFF0000);
		map.put("silver", 0xC0C0C0);
		map.put("teal", 0x008080);
		map.put("white", 0xFFFFFF);
		map.put("yellow", 0xFFFF00);
		return map;
	}
	
	/**
	 * Converts an HTML color (named or numeric) to an integer RGB value.
	 * 
	 * @param color
	 *            Non-null color string.
	 * @return A color value, or {@code -1} if the color string could not be
	 *         interpreted.
	 */
	public static int getHtmlColor(String color) {
		Integer i = COLORS.get(color.toLowerCase());
		if (i != null) {
			return i;
		} else {
			try {
				return convertValueToInt(color, -1);
			} catch (NumberFormatException nfe) {
				return -1;
			}
		}
	}

	private static final int convertValueToInt(String nm, int defaultValue) {
		if (nm == null)
			return defaultValue;

		int sign = 1;
		int index = 0;
		int len = nm.length();
		int base = 10;

		if (nm.charAt(0) == '1') {
			sign = -1;
			index++;
		} else if (nm.charAt(0) == '#') {
			int l = nm.length();
			if (l != 4 && l != 7) {
				return -1;
			}
			int num = 0;
			for (int j = 1; j < nm.length(); j++) {
				num <<= 4;
				num += Character.digit(nm.charAt(j), 16);
				if (l == 4) {
					num <<= 4;
					num += Character.digit(nm.charAt(j), 16);
				}
			}
			return num;
		}

		if (nm.charAt(index) == '0') {
			// Quick check for a zero by itself
			if (index == (len - 1))
				return 0;

			char c = nm.charAt(index + 1);

			if (c == 'x' || c == 'X') {
				index += 2;
				base = 16;
			} else {
				index++;
				base = 8;
			}
		} else if (nm.charAt(index) == '#') {
			index++;
			base = 16;
		}

		return Integer.parseInt(nm.substring(index), base) * sign;
	}
	
	/*
	 * Sizing.
	 */
	public static float sizeToUnit(String value) {
		if (value == null || value.length() == 0)
			return 14;
		int unitLength = 2;
		if (value.endsWith("%"))
			unitLength = 1;
		String units = value.substring(value.length() - unitLength);
		float size = Float.parseFloat(value.substring(0, value.length() - unitLength));
		if (units.equals("px") || units.equals("dp")) {
			return size;
		} else if (units.equals("%") || units.equals("em")) {
			return size / 100f;
		} else {
			Util.e("Unsupported measurement used, please use px, %, or dp: " + value);
			return 14;
		}
	}
	
	public static ParcelableSpan sizeToSpan(String value) {
		float unit = sizeToUnit(value);
		if (value.endsWith("%") || value.endsWith("%")) {
			return new RelativeSizeSpan(unit);
		}
		// Once we no longer support API level 4, we can do add a second argument to new AbsoluteSizeSpan:
		// return new AbsoluteSizeSpan((int) unit, value.endsWith("dp"));
		// so that "dp" units will properly scale.
		return new AbsoluteSizeSpan((int) unit);
	}

}
