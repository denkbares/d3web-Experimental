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
package de.d3web.proket.d3web.elmarparser;

import converter.Html2KnowWECompiler;
import de.d3web.proket.d3web.run.*;
import de.d3web.proket.d3web.input.D3webXMLParserOrig;
import de.d3web.proket.data.DialogType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.GlobalSettings;
import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * DialogManager Servlet.
 * A framing application for using ProKEt. Enables users to upload their
 * word-based (or also d3web) files and xml specs, to parse the word doc
 * and to assemble the corresponding dialog link using the properties of 
 * the xml specs and the given d3web (parsed or native)
 *
 * @author Martina Freiberg
 * @date September 2012
 */
public class TestParserServlet extends HttpServlet {

    GlobalSettings GLOBSET = GlobalSettings.getInstance();
    protected D3webXMLParserOrig d3webParser;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestParserServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // write the servletcontext path
        String servletcontext = config.getServletContext().getRealPath("/");
        GLOBSET.setServletBasePath(servletcontext);
        //System.out.println("HALLO " + servletcontext);
        
        // assemble and write the upload-files path
        }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * 
     * Basic Servlet Processing method
     */
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
        writeFileTo(parserFolder + "/Grrr.txt");
        
        show(request, response, httpSession);
        /*try {
            
            
            compiler.compileTo3web("AR.doc", "ARError.html", "/tmp",
                    "/d3web", "KnowWE-Headless-App.jar");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        } catch (InterruptedException ine){
            ine.printStackTrace();
        }*/
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

    /**
     * Assemble and Show the Dialog Manager Module UI
     * 
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException 
     */
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

}
