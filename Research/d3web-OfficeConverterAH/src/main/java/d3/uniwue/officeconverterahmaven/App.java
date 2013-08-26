package d3.uniwue.officeconverterahmaven;

import java.io.File;
import java.io.PrintWriter;

import officeconverter.Config;
import officeconverter.Converter;
import de.uniwue.abstracttools.StringUtils;

/**
 * Class for converting word --> ProKEt xml
 * 
 * Converts a word specification of iTrees (interactive tree clarification
 * systems) into a html representation that further can be processed by the
 * quick and dirty parser of Elmar that produces finally prototype xml for
 * ProKEt
 * 
 * @author Elmar Böhler, Martina Freiberg
 */
public class App {

	// private static String wordinput =
	// "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_Mietrecht_Teil1.doc";
	private static String wordinput =
			"/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_M1.doc";
	private static String cleanedIntermedHtml =
			"/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/cleanedIntermed.html";
	private static String embeddedObjectsDir =
			"/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/Resources";
	private static String wordhtmlFinalOutput =
			"/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_M1.html";

	public static void main(String[] args) {
		try {

			Config conf = new Config().setEmbeddedObjectsDir(embeddedObjectsDir).setWithLists(true).setLeaveUmlauts(
					true).setWithHeadlineDepths(true);

			// Config conf = new Config(FILETYPE_OUTPUT, false, "UTF-8",
			// embeddedObjectsDir , true, false, true);
			Converter.convertFile2File(new File(wordinput),
					new File(cleanedIntermedHtml), conf);
			String s = StringUtils.readFileString(cleanedIntermedHtml);

			// remove all <p> as those are not supported
			s = s.replaceAll("\\<p\\>FRAGE\\</p\\>", "FRAGE\\<br/\\>");
			s = s.replaceAll("\\<li\\>\\<p\\>", "\\<li\\>");
			s = s.replaceAll("\\</p\\>\\</li\\>", "\\</li\\>");
			s = s.replaceAll("\\<p\\>", "");
			s = s.replaceAll("\\</p\\>", "\\<br/\\>");
			s = s.replaceAll("\\<br/\\>", "\n");

			// replace umlauts etc
			// s = replaceCharEntities(s);

			PrintWriter w = new PrintWriter(wordhtmlFinalOutput);
			w.print(s);
			w.flush();
			w.close();
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

}
