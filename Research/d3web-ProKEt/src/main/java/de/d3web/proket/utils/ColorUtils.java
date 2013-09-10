/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.proket.utils;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some helpers for working with colors.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class ColorUtils {

	/**
	 * List of predefined colors, as defined for HTML/CSS usage.
	 */
	private static final HashMap<String, String> colorMap = new HashMap<String, String>() {
		private static final long serialVersionUID = -1686932525441788279L;
		{
			put("black", "000000");
			put("navy", "000080");
			put("darkblue", "00008B");
			put("mediumblue", "0000CD");
			put("midnightblue", "191970");
			put("indigo", "4B0082");
			put("blue", "0000FF");
			put("maroon", "800000");
			put("darkred", "8B0000");
			put("purple", "800080");
			put("darkmagenta", "8B008B");
			put("darkviolet", "9400D3");
			put("saddlebrown", "8B4513");
			put("darkslateblue", "483D8B");
			put("darkgreen", "006400");
			put("darkslategray", "2F4F4F");
			put("brown", "A52A2A");
			put("firebrick", "B22222");
			put("sienna", "A0522D");
			put("blueviolet", "8A2BE2");
			put("darkolivegreen", "556B2F");
			put("green", "008000");
			put("crimson", "DC143C");
			put("mediumvioletred", "C71585");
			put("red", "FF0000");
			put("darkorchid", "9932CC");
			put("teal", "008080");
			put("dimgray", "696969");
			put("chocolate", "D2691E");
			put("fuchsia", "FF00FF");
			put("magenta", "FF00FF");
			put("slateblue", "6A5ACD");
			put("olive", "808000");
			put("indianred", "CD5C5C");
			put("darkcyan", "008B8B");
			put("orangered", "FF4500");
			put("royalblue", "4169E1");
			put("forestgreen", "228B22");
			put("mediumorchid", "BA55D3");
			put("deeppink", "FF1493");
			put("darkgoldenrod", "B8860B");
			put("seagreen", "2E8B57");
			put("olivedrab", "6B8E23");
			put("mediumslateblue", "7B68EE");
			put("tomato", "FF6347");
			put("palevioletred", "DB7093");
			put("gray", "808080");
			put("slategray", "708090");
			put("peru", "CD853F");
			put("dodgerblue", "1E90FF");
			put("darkorange", "FF8C00");
			put("mediumpurple", "9370DB");
			put("steelblue", "4682B4");
			put("coral", "FF7F50");
			put("lightslategray", "778899");
			put("orchid", "DA70D6");
			put("salmon", "FA8072");
			put("lightcoral", "F08080");
			put("hotpink", "FF69B4");
			put("rosybrown", "BC8F8F");
			put("orange", "FFA500");
			put("goldenrod", "DAA520");
			put("cadetblue", "5F9EA0");
			put("lightseagreen", "20B2AA");
			put("darksalmon", "E9967A");
			put("deepskyblue", "00BFFF");
			put("violet", "EE82EE");
			put("cornflowerblue", "6495ED");
			put("mediumseagreen", "3CB371");
			put("sandybrown", "F4A460");
			put("darkturquoise", "00CED1");
			put("lightsalmon", "FFA07A");
			put("limegreen", "32CD32");
			put("darkgray", "A9A9A9");
			put("darkkhaki", "BDB76B");
			put("plum", "DDA0DD");
			put("tan", "D2B48C");
			put("darkseagreen", "8FBC8F");
			put("yellowgreen", "9ACD32");
			put("burlywood", "DEB887");
			put("gold", "FFD700");
			put("mediumaquamarine", "66CDAA");
			put("lightpink", "FFB6C1");
			put("silver", "C0C0C0");
			put("lime", "00FF00");
			put("mediumturquoise", "48D1CC");
			put("mediumspringgreen", "00FA9A");
			put("thistle", "D8BFD8");
			put("springgreen", "00FF7F");
			put("lightsteelblue", "B0C4DE");
			put("pink", "FFC0CB");
			put("turquoise", "40E0D0");
			put("aqua", "00FFFF");
			put("cyan", "00FFFF");
			put("lawngreen", "7CFC00");
			put("skyblue", "87CEEB");
			put("chartreuse", "7FFF00");
			put("lightskyblue", "87CEFA");
			put("lightgrey", "D3D3D3");
			put("lightblue", "ADD8E6");
			put("lightgreen", "90EE90");
			put("greenyellow", "ADFF2F");
			put("peachpuff", "FFDAB9");
			put("yellow", "FFFF00");
			put("khaki", "F0E68C");
			put("wheat", "F5DEB3");
			put("navajowhite", "FFDEAD");
			put("gainsboro", "DCDCDC");
			put("powderblue", "B0E0E6");
			put("palegoldenrod", "EEE8AA");
			put("moccasin", "FFE4B5");
			put("bisque", "FFE4C4");
			put("palegreen", "98FB98");
			put("mistyrose", "FFE4E1");
			put("paleturquoise", "AFEEEE");
			put("blanchedalmond", "FFEBCD");
			put("lavender", "E6E6FA");
			put("antiquewhite", "FAEBD7");
			put("aquamarine", "7FFFD4");
			put("papayawhip", "FFEFD5");
			put("linen", "FAF0E6");
			put("beige", "F5F5DC");
			put("lavenderblush", "FFF0F5");
			put("oldlace", "FDF5E6");
			put("lemonchiffon", "FFFACD");
			put("lightgoldenrodyellow", "FAFAD2");
			put("cornsilk", "FFF8DC");
			put("seashell", "FFF5EE");
			put("whitesmoke", "F5F5F5");
			put("aliceblue", "F0F8FF");
			put("floralwhite", "FFFAF0");
			put("ghostwhite", "F8F8FF");
			put("lightyellow", "FFFFE0");
			put("snow", "FFFAFA");
			put("honeydew", "F0FFF0");
			put("lightcyan", "E0FFFF");
			put("ivory", "FFFFF0");
			put("mintcream", "F5FFFA");
			put("azure", "F0FFFF");
			put("white", "FFFFFF");
		}
	};

	/**
	 * Get a color that is representing a percentage of the base color.
	 * 
	 * @param color
	 *            Base color.
	 * @param percentage
	 *            Percentage value to represent in the returned color.
	 * @return Color that has been calculated.
	 */
	public static Color getHTMLColorShaded(Color color, double percentage) {
		percentage = Math.pow(percentage, 1.0 / 2.0);
		double red = 255.0 - ((255.0 - color.getRed()) * percentage);
		double green = 255.0 - ((255.0 - color.getGreen()) * percentage);
		double blue = 255.0 - ((255.0 - color.getBlue()) * percentage);
		return new Color((int) red, (int) green, (int) blue);
	}

	/**
	 * Returns the rgb hex expression like in html/css.
	 * 
	 * @param color
	 *            Java color object to convert.
	 * @return rgb hex.
	 */
	public static String getHTMLString(Color color) {
		if (color == null)
			return "";
		String result = "#";
		result += String.format("%02x", color.getRed());
		result += String.format("%02x", color.getGreen());
		result += String.format("%02x", color.getBlue());
		return result;
	}

	/**
	 * Convert html/css color definition to java color object.
	 * 
	 * @param string
	 *            html/css color defintion.
	 * @return Java color object.
	 */
	public static Color parseColor(String string) {
		if (string == null) return null;

		// HTML-AttributeContainer (#)RRGGBB
		Pattern p = Pattern.compile("#?([a-fA-F0-9]{6})");
		Matcher m = p.matcher(string);
		if (m.matches()) return new Color(Integer.parseInt(m.group(1).substring(0, 2), 16),
				Integer.parseInt(m.group(1).substring(2, 4), 16),
				Integer.parseInt(m.group(1).substring(4, 6), 16));

		// short color
		p = Pattern.compile("#?([a-fA-F0-9]{3})");
		m = p.matcher(string);
		if (m.matches()) return new Color(
				Integer.parseInt(m.group(1).substring(0, 1), 16) * 17,
				Integer.parseInt(m.group(1).substring(1, 2), 16) * 17,
				Integer.parseInt(m.group(1).substring(2, 3), 16) * 17);

		// Farbname
		if (Pattern.matches("\\w*", string)) {
			if (colorMap.containsKey(string.toLowerCase())) return parseColor(colorMap.get(string.toLowerCase()));
		}

		return null;
	}

	public static String toCSSString(Color htmlColorShaded) {
		return MessageFormat.format("rgb({0},{1},{2})",
				String.valueOf(htmlColorShaded.getRed()),
				String.valueOf(htmlColorShaded.getGreen()),
				String.valueOf(htmlColorShaded.getBlue()));
	}
}
