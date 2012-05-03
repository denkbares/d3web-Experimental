package de.d3web.proket.d3web.input.officexmlParser;


import java.io.File;
import java.io.PrintWriter;

import de.uniwue.abstracttools.StringUtils;
import officeconverter.Config;
import officeconverter.Converter;


/**
 * Class for converting word --> ProKEt xml
 * 
 * Converts a word specification of iTrees (interactive tree clarification
 * systems) into a html representation that further can be processed by the 
 * quick and dirty parser of Elmar that produces finally prototype xml for ProKEt
 * 
 * @author Elmar Böhler, Martina Freiberg
 */
public class App {

    private static String wordinput = 
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_M1.doc";
    private static String cleanedIntermedHtml = 
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_cleanedIntermed.html";
    private static String embeddedObjectsDir = 
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/Resources";
    private static String wordhtmlFinalOutput = 
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_M1.html";
    
    private static String FILETYPE_OUTPUT = "html";
    

	public static void main(String[] args) {
		try {
			Config conf = new Config(FILETYPE_OUTPUT, false, "UTF-8", embeddedObjectsDir , true, false, true);
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
			//s = replaceCharEntities(s);
                        
			PrintWriter w = new PrintWriter(wordhtmlFinalOutput);
			w.print(s);
			w.flush();
			w.close();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
        /**
         * Replace all special characters by entitites
         * @param s the String where replace needs to be performed
         * @return the cleaned String
         */
	private static String replaceCharEntities(String s) {
		s = s.replaceAll("&#228;", "ä");
		s = s.replaceAll("&#252;", "ü");
		s = s.replaceAll("&#246;", "ö");
		s = s.replaceAll("&#220;", "Ü");
		s = s.replaceAll("&#223;", "ß");
                s = s.replaceAll("&#196;", "Ä");
                s = s.replaceAll("&#214;", "Ö");
		s = s.replaceAll("&#167;", "§");
		s = s.replaceAll("&#8220;", "\"");
		s = s.replaceAll("&#8222;", "\"");
		s = s.replaceAll("&#8230;", "...");
              
		return s;
	}
	
	
	
}
