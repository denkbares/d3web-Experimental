/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.input.officexmlParser;

import de.uniwue.abstracttools.StringUtils;
import java.io.*;
import officeconverter.Config;
import officeconverter.Converter;

/**
 * Utility class for converting .doc files into a prototype xml specification:
 * JURI-TREE (.doc based) into HIERARCHICAL CLARIFIFCATION PROTOTYPE SPEC. For
 * basically converting .doc to html (first step) the basic officeconverter (by
 * Alex Hoernlein) is used. For converting html into the prototype XML, a parser
 * originally by Elmar Boehler is used, that currently only supports the
 * conversion of juri-trees into hierarchical clarification prototype XML.
 *
 * @author mafre
 */
public class DocToPrototypeXMLConverter {

    /* Basic directory where word parsing docs are stored */
    private static String PARSINGPATH =
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/";
    
    /* SOURCE and TARGET doc names */
    private static String SOURCEDOC = PARSINGPATH + "RNS_AR_USER.doc";
    private static String TARGETXML = PARSINGPATH + "juriUser_neu.xml";
    
    private static String WEBAPPPREFIX = "Juri";
    
    /*
     * further needed intermed files
     */
    private static String EMBEDDEDOBJECTSDIR = PARSINGPATH + "Resources";
    private static String PREHTML = PARSINGPATH + "PreIntermed.html";
    private static String HTML = PARSINGPATH + "Intermed.html";
    private static String FILETYPE_OUTPUT = "html";

    public static void main(String[] args) {
        convert();
    }

    /**
     * Uses the files SOURCEDOC and TARGETXML specified at the beginning of the
     * class for performing the doc-to-xml conversion.
     */
    private static void convert() {
        try {
            Config conf =
                    new Config(FILETYPE_OUTPUT, false, "UTF-8",
                    EMBEDDEDOBJECTSDIR, true, false, true);
            Converter.convertFile2File(
                    new File(SOURCEDOC), new File(PREHTML), conf);

            String s = StringUtils.readFileString(PREHTML);

            // remove all <p> as those are not supported
            s = s.replaceAll("\\<p\\>FRAGE\\</p\\>", "FRAGE\\<br/\\>");
            s = s.replaceAll("\\<li\\>\\<p\\>", "\\<li\\>");
            s = s.replaceAll("\\</p\\>\\</li\\>", "\\</li\\>");
            s = s.replaceAll("\\<p\\>", "");
            s = s.replaceAll("\\</p\\>", "\\<br/\\>");
            s = s.replaceAll("\\<br/\\>", "\n");

            // replace umlauts etc --> needed only for direct parsing and
            // wanting the complete HTML with Umlauts back

            PrintWriter w = new PrintWriter(HTML);
            w.print(s);
            w.flush();
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        /*
         * From here, this is what formerly Elmar's "QuickParser" did
         */
        QuickParser qp = new QuickParser();
        try {
            String text = StringUtils.readFileString(HTML);
            ParserQuestionManager qm = qp.parseQuestionTree(qp.getTreePart(text));
            text = qp.getInfoPart(text);
            qp.extractQuestionInformations(text, qm, WEBAPPPREFIX);

            Writer out = 
                    new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(TARGETXML), "UTF8"));
           
            out.write(qm.getXmlEncoding());
            out.close();
        
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

    }
}
