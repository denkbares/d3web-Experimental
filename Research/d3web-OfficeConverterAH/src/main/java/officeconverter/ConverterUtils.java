package officeconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.clapper.util.html.HTMLUtil;

import de.casetrain.cleanup.EntityName2Number;

public class ConverterUtils {

	/**
	 * replaces file with one which can be used with textmarker
	 * @param f
	 * @param encoding
	 */
	public static void doCharacterConversion(File f, String encoding, boolean leaveUmlauts) {
		
		if (!f.exists() || f.length() == 0)
			return;
		
		String sep = System.getProperty("line.separator");
		
		String txt = null;
		// TextFile2String stripped down
		{
			StringBuffer contents = new StringBuffer();
			
			BufferedReader input = null;
			try {
				InputStreamReader isr =
					new InputStreamReader(new FileInputStream(f), encoding);
				input = new BufferedReader(isr);
				
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(sep);
				}
			} catch (FileNotFoundException ex) {
				;
			} catch (IOException ex) {
				;
			} finally {
				try {
					if (input != null) {
						// flush and close both "input" and its underlying
						// FileReader
						input.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			txt = contents.toString();
		}
		
		String repTo = "~~~###~~~";
		txt = txt.replaceAll(sep.replaceAll("\\\\", "\\\\"), repTo);
		txt = txt.replaceAll("\\s", " ");

		// code from DocumentFile.saveAsHtml
		txt = HTMLUtil.makeCharacterEntities(txt);
		
		txt = convertEntities(txt, leaveUmlauts);
		
		txt = txt.replaceAll(repTo, sep);

		
		// TextFile2String stripped down
		{
			Writer output = null;
			try {
				output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), encoding));
				output.write(txt);
			} catch (Exception ex) {
				;
			} finally {
				if (output != null)
					try {
						output.close();
					} catch (Exception ex) {
						;
					}
			}
		}
		
	}

	public static String convertEntities(String s, boolean leaveUmlauts) {
		String res = EntityName2Number.convertName2Number(s);
		if (!leaveUmlauts)
			res =
				res
				.replaceAll("&#228;", "ä")
				.replaceAll("&#246;", "ö")
				.replaceAll("&#252;", "ü")
				.replaceAll("&#196;", "Ä")
				.replaceAll("&#214;", "Ö")
				.replaceAll("&#220;", "Ü")
				.replaceAll("&#223;", "ß")
				;
		
		res = res.replaceAll("&#173;", "-");
		res = res.replaceAll("&#65279;", "");
		return res;
	}

}
