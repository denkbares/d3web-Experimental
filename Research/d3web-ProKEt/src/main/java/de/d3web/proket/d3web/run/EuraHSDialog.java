/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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

import de.d3web.core.knowledge.TerminologyObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.output.render.EuraHSDefaultRootD3webRenderer;
import de.d3web.proket.d3web.ue.JSONLogger;
import de.d3web.proket.d3web.utils.Encryptor;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import java.util.*;

/**
 * Servlet for creating and using dialogs with d3web binding. Binding is more of
 * a loose binding: if no d3web etc session exists, a new d3web session is
 * created and knowledge base and specs are read from the corresponding XML
 * specfication.
 *
 * Basically, when the user selects answers in the dialog, those are transferred
 * back via AJAX calls and processed by this servlet. Here, values are
 * propagated to the d3web session (and later re-read by the renderers).
 *
 * Both browser refresh and pressing the "new case"/"neuer Fall" Button in the
 * dialog leads to the creation of a new d3web session, i.e. all values set so
 * far are discarded, and an "empty" problem solving session begins.
 *
 * @author Martina Freiberg
 *
 * @date 14.01.2011; Update: 28/01/2011
 *
 */
public class EuraHSDialog extends D3webDialog {

    private static final long serialVersionUID = -4790211381203716706L;
    private static final String EURAHS_FIRSTQUESTION = 
            "Please enter a EuraHS registration number (NO PATIENT NAME)";
    
    private static ArrayList<String> INITDROPQS = new ArrayList<String>() {

        {
            add("Production company of the mesh");
            add("Production company of the first mesh");
            add("Production company of the second mesh");
            add("Production company of the suture material, non-mesh repair");
            add("Production company of the suture material");
            add("Production company of the tracker device");
            add("Production company of the glue");
            add("Production company of the fixation device");
        }
    };

    @Override
    protected String getSource(HttpServletRequest request, HttpSession http) {
        return "Hernia";
    }

    /**
     * Basic servlet method for displaying the dialog.
     *
     * @created 28.01.2011
     *
     * @param request
     * @param response
     * @param d3webSession
     * @throws IOException
     */
    @Override
    protected void show(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession)
            throws IOException {

        PrintWriter writer = response.getWriter();

        // new ContainerCollection needed each time to get an updated dialog
        ContainerCollection cc = new ContainerCollection();

        Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);

        // not needed for EuraHS, would work to change dropdown text default
        // to other Locale
        //int loc = Integer.parseInt((String) httpSession.getAttribute("locale").toString());

        Session sNew = initDropdownChoiceQuestions(d3webSess, 2);
        httpSession.setAttribute(D3WEB_SESSION, sNew);
        httpSession.setAttribute("initsetquestions", INITDROPQS);


        // get the root renderer --> call getRenderer with null
        EuraHSDefaultRootD3webRenderer d3webr = (EuraHSDefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(null);


        cc = d3webr.renderRoot(cc, d3webSess, httpSession, request);

        writer.print(cc.html.toString()); // deliver the rendered output

        writer.close(); // and close
    }

    /**
     * Handle login of new user
     *
     * @created 29.04.2011
     *
     * @param req
     * @param res
     * @param httpSession
     * @throws IOException
     */
    @Override
    protected void loginUsrDat(HttpServletRequest req,
            HttpServletResponse res, HttpSession httpSession)
            throws IOException {

        // fetch the information sent via the request string from login
        String u = req.getParameter("u");

        // get the response writer for communicating back via Ajax
        PrintWriter writer = res.getWriter();

        httpSession.setMaxInactiveInterval(60 * 60);

        // if no valid login
        // if (!permitUser(u, p)) {
        //
        // // causes JS to display error message
        // writer.append("nosuccess");
        // return;
        // }

        // set user attribute for the HttpSession
        httpSession.setAttribute("user", u);

        httpSession.setAttribute("lastLoaded", "");

        /*
         * in case we should have more than one user per clinic, we distinguish
         * them by adding "_1"... to the clinic name, i.e. WUE_1, WUE_2 etc.
         * Thus we need to extract part of the login name that also denotes the
         * clinic name and thus the subfolder, where cases are stored that
         * should be visible for THIS user.
         */
        int splitter = u.indexOf("_");
        if (splitter != -1) {
            String toReplace = u.substring(splitter, u.length());
            String userSubfolder = u.replace(toReplace, "");
            httpSession.setAttribute("user", userSubfolder);
        }

        // causes JS to start new session and d3web case finally
        writer.append("newUser");
    }

    @Override
    protected void resetD3webSession(HttpSession httpSession) {
        super.resetD3webSession(httpSession);
        Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);
        String definingObject = D3webConnector.getInstance().getD3webParser().getRequired();
        Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                definingObject);
        Fact fact = d3webSess.getBlackboard().getValueFact(to);
        if (to != null && fact == null) {
            D3webUtils.setValue(definingObject, generateCaseNumber(definingObject), d3webSess);
        }
    }

    public static synchronized String generateCaseNumber(String persistenceFileName) {
        String fileName = GlobalSettings.getInstance().getCaseFolder() + File.separator + ".."
                + File.separator + persistenceFileName + ".txt";
        String persistence = readTxtFile(fileName);
        int numberOfCases = 0;
        try {
            numberOfCases = Integer.parseInt(persistence);
        } catch (NumberFormatException ne) {
        }
        numberOfCases++;
        writeTxtFile(fileName, String.valueOf(numberOfCases));
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return date + "-" + numberOfCases;
    }

    private static String readTxtFile(String fileName) {
        StringBuffer inContent = new StringBuffer();
        File file = new File(fileName);
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file)));
                int char1 = bufferedReader.read();
                while (char1 != -1) {
                    inContent.append((char) char1);
                    char1 = bufferedReader.read();
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inContent.toString();
    }

    private static void writeTxtFile(String fileName, String content) {
        try {
            File file = new File(fileName).getCanonicalFile();
            file.getParentFile().mkdirs();
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Session initDropdownChoiceQuestions(Session d3webSess, int loc) {

        for (String iddq : INITDROPQS) {

            Fact f = d3webSess.getBlackboard().getValueFact(
                    d3webSess.getKnowledgeBase().getManager().searchQuestion(iddq));
            if (f == null) {

                String selectChoicePrompt = "Please select...";
                /*switch (loc) {
                    case 1: // german
                        selectChoicePrompt = "Bitte auswählen...";
                        break;
                    case 2: // english
                        selectChoicePrompt = "Please select...";
                        break;
                    case 3: // spanish
                        selectChoicePrompt = "";
                        break;
                    case 4: // italian
                        selectChoicePrompt = "";
                        break;
                    case 5: // french
                        selectChoicePrompt = "";
                        break;
                    case 6: // polish
                        selectChoicePrompt = "";
                        break;
                }
                if (selectChoicePrompt == null) {
                    selectChoicePrompt = "Please select...";
                }*/
                D3webUtils.setValue(iddq, selectChoicePrompt, d3webSess);
            }

        }

        return d3webSess;
    }

    /**
     * Saving a case, using encoded filename for anonymization
     *
     * @created 34.08.2012
     *
     * @param request ServletRequest
     * @param response ServletResponse
     */
    protected void saveCase(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        PrintWriter writer = null;
        writer = response.getWriter();

        String userFilename = request.getParameter("userfn");
        String user = (String) httpSession.getAttribute("user");
        String lastLoaded = (String) httpSession.getAttribute("lastLoaded");
        String forceString = request.getParameter("force");

        // force wird im JS gesetzt, falls der User unter bereits vorhandenem
        // Namen speichern will und das nochmal bestätigt.
        boolean force = forceString != null && forceString.equals("true");

        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);

        // if: really overwrite existing OR case not exists OR case exists but
        // has been loaded for modification
        if (force
                || !PersistenceD3webUtils.existsCaseAnon(user, userFilename)
                || (PersistenceD3webUtils.existsCaseAnon(user, userFilename)
                && lastLoaded != null && lastLoaded.equals(userFilename))) {

            PersistenceD3webUtils.saveCaseAnonymized(
                    user,
                    userFilename,
                    d3webSession);

            httpSession.setAttribute("lastLoaded", userFilename);
        } else {
            writer.append("exists");
        }
    }

    @Override
    protected void loadCaseClear(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) {
        String filename = request.getParameter("fn");
        String user = (String) httpSession.getAttribute("user");
        loadCaseClearUserFilename(request, httpSession, user, filename);
    }

    private void loadCaseClearUserFilename(HttpServletRequest request, HttpSession httpSession,
            String user, String filename) {

        Session session = null;
        String anonFilename = Encryptor.getAnonymizedFilename(filename);
        if (PersistenceD3webUtils.existsCase(user, anonFilename)) {
            session = PersistenceD3webUtils.loadUserCase(user, anonFilename);
            httpSession.setAttribute(D3WEB_SESSION, session);
            httpSession.setAttribute("lastLoaded", anonFilename);
            D3webConnector.getInstance().setSession(session);

            JSONLogger logger =
                    (JSONLogger) httpSession.getAttribute("logger");

            if (uesettings.isLogging()) {
                ServletLogUtils.resetLogfileName(session.getId(), logger);
                String time;
                if (request.getParameter("timestring") != null) {
                    time = request.getParameter("timestring").replace("+", " ");
                } else {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat(super.SDF_DEFAULT);
                    time = sdf.format(date);
                }
                ServletLogUtils.logResume(time, session.getId(), logger);
            }
        }
    }
    


}
