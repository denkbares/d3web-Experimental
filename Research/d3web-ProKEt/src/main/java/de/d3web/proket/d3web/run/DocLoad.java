/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * ControlCenter Servlet.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class DocLoad extends HttpServlet {

    GlobalSettings defset = GlobalSettings.getInstance();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocLoad() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        HttpSession httpSession = request.getSession(true);
        httpSession.setMaxInactiveInterval(20*60);
        
        if(request.getParameter("upStatus") != null){
            String status = request.getParameter("upStatus");
            if(status.equals("done")){
                
            }
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "show";        // per default: show the DocLoad Base UI
        }
        
        if (action.equalsIgnoreCase("processDocFile")) {
            processDocFile(request, response, httpSession);
        } else if (action.equalsIgnoreCase("show")){
            show(request, response, httpSession);
        }

    }
    
    
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
        
         /*
         * Retrieve the basic DocLoad template
         */
        StringTemplate st = stg_sub.getInstanceOf("docload");

        // get the css file for styling the DocLoad Module
	File css = FileUtils.getResourceFile("/stringtemp/css/docLoadStyle.st");
        
        String cssString = FileUtils.getString(css); 
        st.setAttribute("css", cssString);
        
        // output
        PrintWriter writer = response.getWriter();
        writer.write(st.toString());

        writer.close();
    }
    
    
    protected void processDocFile(HttpServletRequest request, 
            HttpServletResponse response,
            HttpSession httpSession) throws IOException{
        System.out.println("Upload Doc File");
        System.out.println("Parse Doc File on Server");
        System.out.println("Handle Parsing Result");
        
        PrintWriter writer = response.getWriter();
        writer.write("error");

        writer.close();
    }
}
