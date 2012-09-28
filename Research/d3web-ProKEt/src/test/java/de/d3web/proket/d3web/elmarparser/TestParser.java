/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.elmarparser;

import converter.Html2KnowWECompiler;
import java.io.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author mafre
 */
public class TestParser {

    public static void main(String[] args) {
        Html2KnowWECompiler compiler = new converter.Html2KnowWECompiler();
        
        String parserFolder = "/Users/mafre/CodingSpace/PARSER";
        //writeFileTo(parserFolder + "/Grrr.txt");
        
        String doc = parserFolder + "/ARExt.doc";
        String errFile = parserFolder + "/ARError.html";
        String tmp = parserFolder + "/tmp/";
        String d3web = parserFolder + "/d3web/";
        
       
       String knowwe = parserFolder + "/KnowWE-Headless-App.jar";
       
       
        try {
          compiler.compileTo3web(doc, errFile, tmp, d3web, knowwe);
          //de.uniwue.abstracttools.StringUtils.writefileString(knowwe, knowwe);
          
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ine) {
            ine.printStackTrace();
        }
    }
    
    
    public static void writeFileTo (String location){
        File dfile =
                    new File(location);
            
            // write the link to the dialog list file
            try {

                FileWriter fw = new FileWriter(dfile, true);
                BufferedWriter output = new BufferedWriter(fw);
                output.write("test");
                output.newLine();
                output.close();
                 
            } catch (IOException e1) {
                e1.printStackTrace(); 
            }
    }
}
