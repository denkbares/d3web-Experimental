package de.d3web.we.biolog.utils;

public class BiologUtils {

	/**
	 * Replaces escaped Text-Umlauts with the Umlauts.
	 * 
	 * @param buffy
	 * @return buffy without escaped Umlauts
	 */
	public static String replaceBibTeX(String buffy) {
		buffy = buffy.replaceAll("\\{\\\\\"U\\}", "Ü");
		buffy = buffy.replaceAll("\\{\\\\\"u\\}", "ü");
		buffy = buffy.replaceAll("\\{\\\\\"A\\}", "Ä");
		buffy = buffy.replaceAll("\\{\\\\\"a\\}", "ä");
		buffy = buffy.replaceAll("\\{\\\\\"O\\}", "Ö");
		buffy = buffy.replaceAll("\\{\\\\\"o\\}","ö");
		buffy = buffy.replaceAll("\\{\\\\ss\\}","ß");
		return buffy;
	}
}
