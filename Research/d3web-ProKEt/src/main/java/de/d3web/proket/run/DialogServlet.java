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
package de.d3web.proket.run;

import de.d3web.proket.d3web.run.ServletLogUtils;
import de.d3web.proket.d3web.ue.log.JSONLogger;
import de.d3web.proket.data.DialogTree;
import de.d3web.proket.input.xml.IParser;
import de.d3web.proket.input.xml.ParseException;
import de.d3web.proket.input.xml.XMLParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.output.render.IRenderer;
import de.d3web.proket.output.render.Renderer;
import de.d3web.proket.utils.GlobalSettings;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet for rendering non-d3web dialogs.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class DialogServlet extends HttpServlet {

    private static final long serialVersionUID = -1514789465295324518L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DialogServlet() {
        super();
    }

    /**
     * Init method -- put all the stuff here, that should be done ONCE for the
     * Servlet at the beginning, such as initialising paths, DB connections,
     * etc.
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String servletcontext = config.getServletContext().getRealPath("/");
        GlobalSettings.getInstance().setServletBasePath(servletcontext);

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

        // in case nothing other is provided, "show" is the default action
        String action = request.getParameter("action");
        if (action == null) {
            action = "show";
        }

        if (action.equalsIgnoreCase("show")) {
            show(httpSession, response, request);
            return;
        } else if (action.equalsIgnoreCase("logInit")) {
            logInitially(request, response, httpSession);
            return;
        }
    }

    /**
     * Handle the actual display of the Servlet content
     *
     * @param session
     * @param response
     * @param request
     * @throws IOException
     */
    protected void show(HttpSession session,
            HttpServletResponse response,
            HttpServletRequest request)
            throws IOException {

        PrintWriter writer = response.getWriter();
        DialogTree dialogTree = parseInput(request);

        IRenderer rootRenderer = Renderer.getRenderer(dialogTree.getRoot());
        ContainerCollection cc = rootRenderer.renderRoot(dialogTree);
        String html = cc.html.toString();

        // deliver the rendered output
        writer.print(html);
        writer.close();
    }

    /**
     * Initialization of the logging mechanisms. If nothing has been logged
     * before, first browser and user info need to be gathered by JS, thus
     * return with "firsttime" in writer. 
     * Afterwards, JS calls this action again, now has the browser and user
     * info, and can start logging.
     * // TODO: do this refactoring (logInit not existing anymore) also for
     * // D3webServlets.
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException 
     */
    protected void logInitially(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) throws IOException {

        PrintWriter writer = response.getWriter();

        /*
         * check if initial log is already done; if not append firsttime. This
         * is the flag for JS to first retrieve the browser and user, and then
         * call this method/action again to go to "else" */
        if (!GlobalSettings.getInstance().initLogged()) {
            writer.append("firsttime");
            GlobalSettings.getInstance().setInitLogged(true);
        } 
        
        /* in this case, the logging initialisation, i.e. retrieval of browser
         * etc info has been done successfully and now those values can be
         * processed further */
        else {
            JSONLogger logger = new JSONLogger();
            Date now = new Date();
            
            // initialize logging
            ServletLogUtils.initForPrototypeDialogs(logger, now, httpSession);
            
            String browser =
                    request.getParameter("browser").replace("+", " ");
            String user =
                    request.getParameter("user").replace("+", " ");
            String start =
                    request.getParameter("timestring").replace("+", " ");
            ServletLogUtils.logBaseInfo(browser, user, start);
        }
    }

    /**
     * Use the XMLParser to parse the prototype specification files.
     * Therefore, src parameter (if specified) is used for determining which
     * prototype spec to use, otherwise a default src is used. Returns a
     * DialogTree representation of the dialog.
     * @param request
     * @param response
     * @param writer
     * @return the DialogTree representation of the parsed XML-specified dialog.
     */
    private static DialogTree parseInput(HttpServletRequest request) {

        /*
         * if src-Parameter is given, take it, if not, "Standarddialog" is the
         * default source
         */
        String source = "Standarddialog";
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }
       
        // parse the input source
        XMLParser parser = new XMLParser(source);
        
        // load the dialog into memory
        return parser.getTree();
    }
}