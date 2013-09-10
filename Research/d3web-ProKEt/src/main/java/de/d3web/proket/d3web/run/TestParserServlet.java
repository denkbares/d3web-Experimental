/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.run;

import converter.Html2KnowWECompiler;
import de.d3web.proket.d3web.input.D3webXMLParserOrig;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.d3web.proket.utils.GlobalSettings;
import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * DialogManager Servlet. A framing application for using ProKEt. Enables users
 * to upload their word-based (or also d3web) files and xml specs, to parse the
 * word doc and to assemble the corresponding dialog link using the properties
 * of the xml specs and the given d3web (parsed or native)
 *
 * @author Martina Freiberg @date September 2012
 */
/*public class TestParserServlet extends HttpServlet {

    GlobalSettings GLOBSET = GlobalSettings.getInstance();
    protected D3webXMLParserOrig d3webParser;

    public TestParserServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // write the servletcontext path
        String servletcontext = config.getServletContext().getRealPath("/");
        GLOBSET.setServletBasePath(servletcontext);

    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        //System.out.println("Servlet init");
        response.setContentType("text/html; charset=UTF-8");
        HttpSession httpSession = request.getSession(true);
        httpSession.setMaxInactiveInterval(20 * 60);

        String parserFolder = GLOBSET.getServletBasePath() + "PARSER";

        //System.out.println(parserFolder + "/Grrr.txt");
        Html2KnowWECompiler compiler = new converter.Html2KnowWECompiler();
        //writeFileTo(parserFolder + "/Grrr.txt");

        show(request, response, httpSession);

        String doc = parserFolder + "AR.doc";
        String errFile = parserFolder + "ARError.html";
        String tmp = parserFolder + "/tmp";
        File tmpFile = new File(tmp);
        tmpFile.setWritable(true, true);
        tmpFile.setExecutable(true, true);
        tmpFile.setReadable(true, true);
        
        String d3web = parserFolder + "/d3web";
        File d3webFile = new File(tmp);
        d3webFile.setWritable(true, true);
        d3webFile.setExecutable(true, true);
        d3webFile.setReadable(true, true);
        
        String knowwe = parserFolder + "KnowWE-Headless-App.jar";
        
        try {
          compiler.compileTo3web(doc, errFile, tmp, d3web, knowwe);
            
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

    public static void writeFileTo(String location) {
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

   protected void show(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession)
            throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        // output
        PrintWriter writer = response.getWriter();
        writer.write("parsertest");

        writer.close();
    }
}*/
