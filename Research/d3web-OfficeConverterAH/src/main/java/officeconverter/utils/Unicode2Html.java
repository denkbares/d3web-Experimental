package officeconverter.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Unicode2Html {

	private static Map<Character, String> charToEntityName = null;
	
	private Unicode2Html() { /* hide */ }

	static {
		charToEntityName = new HashMap<Character, String>();
		
		ResourceBundle bundle = ResourceBundle.getBundle("org.clapper.util.html.HTMLUtil");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String sChar = bundle.getString(key);
			key = key.substring("html_".length());
			char c = sChar.charAt(0);
			charToEntityName.put(c, key);
		}
	}
	
	/**
	 * This method converts a string from the input file into an HTML string,
	 * with some special characters being replaced by their appropriate HTML
	 * codes. The string will be checked for invalid ISO control characters.
	 * 
	 * @param s
	 *            The string that is to be converted.
	 * @return The HTML string or null if s is null.
	 * @throws Exception
	 *             if there is an invalid ISO control character
	 */
	public static String getHtmlStringWithCheckOnISOControls(String s)
			throws Exception {

		if (s == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer();

		String translated = null;
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);

			translated = charToEntityName.get(c);
			
			if (translated != null) {
				buf.append('&').append(translated).append(';');
			} else {
				if (Character.isISOControl(c) && c != '\r' && c != '\n' && c != '\t') {
					System.err.println("OfficeConverter.Unicode2Html.getHtlm...: invalid character '" + c + "'");
					throw new Exception("Invalid Character");
				} else if (c == '\n') {
					buf.append("\n<br>");
				} else {
					buf.append(c);
				}
			}
		}

		String res = buf.toString();
		
		// [HOTFIX]:alexander:either that or textmarker needs to recognize all these as delimiters
		res =
			res
			.replaceAll("&shy;", "-")
			.replaceAll("&hyphen;", "-")
			.replaceAll("&mdash;", "-")
			.replaceAll("&ndash;", "-")
			.replaceAll("&semi;", ";") // [HOTFIX]:aha:added 20071127 1550
		;
		
		return res;
	}
}
