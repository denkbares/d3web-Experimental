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

import de.d3web.proket.d3web.input.D3webXMLParser;
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
public class DialogManager extends HttpServlet {

    GlobalSettings GLOBSET = GlobalSettings.getInstance();
    protected D3webXMLParser d3webParser;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DialogManager() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // write the servletcontext path
        String servletcontext = config.getServletContext().getRealPath("/");
        GLOBSET.setServletBasePath(servletcontext);
        
        // assemble and write the upload-files path
        String uploadFilesBase = GLOBSET.getServletBasePath() + "UPFiles";
        GLOBSET.setUploadFilesBasePath(uploadFilesBase);
    
        // initialize the d3web parser
        d3webParser = new D3webXMLParser();
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

        response.setContentType("text/html; charset=UTF-8");
        HttpSession httpSession = request.getSession(true);
        httpSession.setMaxInactiveInterval(20 * 60);

        // get required action for progressing to the respective functionality
        String action = request.getParameter("action");
        if (action == null) {
            action = "show";        // per default: show the DialogManager Base UI
        }

        if (action.equalsIgnoreCase("processDocFile")) {
            processDocFile(request, response, httpSession);
        } else if (action.equalsIgnoreCase("show")) {
            show(request, response, httpSession);
        } else if (action.equalsIgnoreCase("assembleDialog")) {
            assembleDialog(request, response, httpSession);
        } else if (action.equalsIgnoreCase("storeDialogToUsersList")) {
            storeDialogToUsersList(request, response, httpSession);
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

        // First assemble the stringtemplate base paths within the servlet
        ServletContext context = request.getSession().getServletContext();
        String realStPath = context.getRealPath(request.getContextPath())
                + "/WEB-INF/classes/stringtemp/html";

        // this is the topmost ST directory
        StringTemplateGroup stg =
                new StringTemplateGroup("stGroup", realStPath);

        // the subdirectory which contains the specific framing template
        // for the document loader
        StringTemplateGroup stg_sub =
                new StringTemplateGroup("stGroup", realStPath + "/docLoad");

        // need to tell the template dirs of their inheritance, so within
        // st files we just can normally call other templates
        stg_sub.setSuperGroup(stg);

        // Retrieve the basic DialogManager template
        StringTemplate st = stg_sub.getInstanceOf("docload");

        // get the css file for styling the DialogManager Module
        File css = FileUtils.getResourceFile("/stringtemp/css/docLoadStyle.st");

        String cssString = FileUtils.getString(css);
        st.setAttribute("css", cssString);

        // output
        PrintWriter writer = response.getWriter();
        writer.write(st.toString());

        writer.close();
    }

    /**
     * Parse the .doc file with KB specification into the d3web format.
     * 
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException 
     */
    protected void processDocFile(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {
        
        System.out.println("Parse Doc File on Server");
        System.out.println("Write d3web to respective folder");

        PrintWriter writer = response.getWriter();
        writer.write("error");

        writer.close();
    }

    /**
     * Assemble the dialog links for the respective Servlets.
     * Currently using the latest uploaded file (XML) and a fixed d3web.
     * TODO: adapt when parser generates d3web.
     * 
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException 
     */
    protected void assembleDialog(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        // TODO: later the latest d3web and spec are provided by the JS
        String d3web = 
                httpSession.getAttribute("latestD3web") != null
                ? httpSession.getAttribute("latestD3web").toString() : "";
       
        // get latest loaded .d3web and XML specs file
        String spec = 
                httpSession.getAttribute("latestSpec") != null
                ? httpSession.getAttribute("latestSpec").toString() : "";
      
        /* assemble dialog link */
        String dialogLink = "";
        // get the Dialog Type
        if (!spec.equals("")) {
            String type = retrieveDialogTypeFromSpec(spec);
            
            // assemble ITree Servlet Link
            if (type.equalsIgnoreCase(DialogType.ITREE.toString())) {

                dialogLink = "/ITreeDialog?src=" + spec.replace(".xml", "");
            } //TODO else... 
        }

        // send link text back to JS
        PrintWriter writer = response.getWriter();
        if (!dialogLink.equals("")) {
            writer.write(dialogLink);
        } else {
            writer.write("error");
        }
        writer.close();
    }

    /**
     * Retrieve the Dialog Type from the XML specification
     * 
     * @param spec the Specification
     * @return the Dialog Type 
     */
    private String retrieveDialogTypeFromSpec(String spec) {

        d3webParser.setSourceToParse(spec);
        d3webParser.parse();
        return d3webParser.getUIPrefix();
    }

    /**
     * Stores a dialog link to the list of dialogs (currently: globa,
     * one day: per user)
     * 
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException 
     */
    protected void storeDialogToUsersList(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        // if a dialog link is given (only if confirmed by user)
        if (request.getParameter("dialogLink") != null
                && !request.getParameter("dialogLink").toString().equals("")) {

            String linkToStore = request.getParameter("dialogLink").toString();
          
            // TODO: adapt this when adding user management wrt folders
            File dfile =
                    new File(GLOBSET.getUploadFilesBasePath() + "/dialogs.txt");
            PrintWriter writer = response.getWriter();

            // write the link to the dialog list file
            try {

                FileWriter fw = new FileWriter(dfile, true);
                BufferedWriter output = new BufferedWriter(fw);
                output.write(linkToStore);
                output.newLine();
                output.close();
                writer.write("OK");
                
            } catch (IOException e1) {
                writer.write("error");
                e1.printStackTrace(); 
            }
        }
    }
}
