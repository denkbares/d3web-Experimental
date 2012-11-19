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

import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import converter.Html2KnowWECompiler;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.utils.Utils;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.utils.SystemLoggerUtils;
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
import de.d3web.proket.utils.TemplateUtils;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
public class DialogManager extends HttpServlet {

    GlobalSettings GLOBSET = GlobalSettings.getInstance();
    protected D3webXMLParser d3webParser;
    private static String FILESEP = System.getProperty("file.separator");
    protected static final String REPLACECONTENT = "##replacecontent##";
    protected static final String REPLACEID = "##replaceid##";
    protected static final String LINESEP = System.getProperty("line.separator");

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

        GLOBSET.setWebAppWarName("/JuriLibreOffice");
        GLOBSET.setWebAppWarName("");

        // initialize the d3web parser
        d3webParser = new D3webXMLParser();

        // umleiten des System.err
        try {

            // Code that writes to System.out or System.err
            PrintStream ps = SystemLoggerUtils.getEventLoggerStream();
            // we need to event log via the system.err as this is most easily
            // also accessible for outside projects/code
            System.setErr(ps);

        } catch (Exception e) {
            // all Exceptions go to the real Errorlog, however
            e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        }
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

        if (action.equalsIgnoreCase("parseKBDoc")) {
            parseKBDocument(request, httpSession, response);
        } else if (action.equalsIgnoreCase("show")) {
            show(request, response, httpSession);
        } else if (action.equalsIgnoreCase("assembleDialog")) {
            assembleDialog(request, response, httpSession);
        } else if (action.equalsIgnoreCase("storeDialogToUsersList")) {
            storeDialogToUsersList(request, response, httpSession);
        } else if (action.equalsIgnoreCase("deleteSelectedKB")) {
            deleteSelectedKB(request, response, httpSession);
        } else if (action.equalsIgnoreCase("finalizeExceptionReport")) {
            finalizeExceptionReport(request, response, httpSession);
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

        System.err.println("Show the Servlet - try to get and fill template and styles...");
        StringTemplate st =
                TemplateUtils.getStringTemplate("dialogManager/dialogManager", "html");


        // get the css file for styling the DialogManager Module
        File css = FileUtils.getResourceFile("/stringtemp/css/diaManStyle.st");

        st.setAttribute("kbuploadfieldlabel", "Wissensbasis-Datei wählen (.doc/.d3web)");
        st.setAttribute("specuploadfieldlabel", "UI Spezifikation wählen (.xml)");

        String cssString = FileUtils.getString(css);
        st.setAttribute("css", cssString);

        boolean withoutExceptions = false;
        if (!fillFilesList("d3web").toString().contains("EXCEPTION")) {
            withoutExceptions = true;
        }
        st.setAttribute("fileselectopts_" + "d3web", fillFilesList("d3web").toString());

        if (!fillFilesList("specs").toString().contains("EXCEPTION")) {
            withoutExceptions = true;
        }
        st.setAttribute("fileselectopts_" + "specs", fillFilesList("specs").toString());


        if (!fillDialogList().toString().contains("EXCEPTION")) {
            withoutExceptions = true;
        }
        st.setAttribute("dialogLinkList", fillDialogList().toString());


        PrintWriter writer = response.getWriter();
        if (!withoutExceptions) {
            st.setAttribute("statusmessage", "An internal program exception occured. Please see Exception Report!");
            // TODO write Exception to Exception Report
        }
        // output
        writer.write(st.toString());
        writer.close();
        System.err.println("Show the Servlet - SUCCESS");
    }

    /**
     * Fill the dialog list HTML
     *
     * @param st the StringTemplate to be filled
     * @return true if filling the dialog list was successful, false otherwise
     * (e.g. in case of an exception)
     */
    private StringBuilder fillDialogList() {

        StringBuilder bui = new StringBuilder();
        System.err.println("Fill Dialog list - try to get and display stored dialogs...");
        try {
            BufferedReader br;

            String line = "";
            br = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(
                    GLOBSET.getUploadFilesBasePath() + FILESEP + "dialogs.txt")));
            line = "";
            while ((line = br.readLine()) != null) {
                bui.append("<div class='row'>");
                bui.append("<a href='");
                bui.append(line);
                bui.append("' target='_blank'>");
                bui.append(line);
                bui.append("</a></div>");
            }
            br.close();
            System.err.println("Fill Dialog list - SUCCESS");


        } catch (IOException e1) {
            e1.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
            bui.append("EXCEPTION");
        } catch (Exception e) {
            e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
            bui.append("EXCEPTION");
        }
        return bui;
    }

    private StringBuilder fillFilesList(String subfolder) {

        System.err.println("Fill " + subfolder + " files list - try to get and display " + subfolder + " files...");
        // assemble path where files are stored
        String path = GlobalSettings.getInstance().getUploadFilesBasePath()
                + "/" + subfolder;
        System.err.println("\t - " + subfolder + " filepath on server: " + path);

        // get all files under the given path
        List<File> files = Utils.getFileList(path);
        System.err.println("\t - " + subfolder + " files on server: " + files.toString());

        StringBuilder bui = new StringBuilder();
        if (files.size() > 0) {

            for (File file : files) {
                if (!file.getName().equals("empty.txt")
                        && !file.getName().equals("wikiCode.d3web")) {
                    bui.append("<option");
                    // omitt filetype ending
                    String filename =
                            file.getName().split("\\.")[0];
                    bui.append(" title='" + filename + "'>");
                    bui.append(filename);
                    bui.append("</option>");
                }
            }
            System.err.println("Fill " + subfolder + " files list - SUCCESS");
        }
        return bui;
    }

    protected void deleteSelectedKB(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        System.err.println("Delete selected KB - try to delete a selected kb from the list...");

        String kbName = request.getParameter("kbToDelete").toString();
        System.err.println("\t - kbName selected: " + kbName);

        boolean deleteSuccess = false;
        String path = GLOBSET.getUploadFilesBasePath() + FILESEP
                + "d3web/";

        System.err.println("\t - kb filepath on server: " + path);
        File fileToDelete = new File(path + kbName + ".d3web");
        if (fileToDelete.exists()) {
            deleteSuccess = fileToDelete.delete();
        }

        PrintWriter writer = response.getWriter();
        if (deleteSuccess) {
            System.err.println("Delete selected KB - SUCCESS");
            
            writer.append(REPLACEID + "d3webSelect");
            writer.append(REPLACECONTENT);
            writer.append("<select id='d3webSelect' class='d3webSelect' size='12' name='d3webfiles'>");
            writer.append(fillFilesList("d3web"));
            writer.append("</select>");
        } else {
            writer.write("ERROR--DeleteSelectedKB");
        }
        writer.close();
    }

    /**
     * Assemble the dialog links for the respective Servlets. Currently using
     * the latest uploaded file (XML) and a fixed d3web. TODO: adapt when parser
     * generates d3web.
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void assembleDialog(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        System.err.println("Assemble Dialog - try to assemble dialog string from selected KB & UI");

        String d3webKBName = request.getParameter("kb").toString();
        System.err.println("\t - KB name: " + d3webKBName);

        String specName = request.getParameter("spec").toString();
        System.err.println("\t - UI spec name: " + specName);

        String path =
                GlobalSettings.getInstance().getUploadFilesBasePath()
                + "/specs/" + specName + ".xml";
        System.err.println("\t - dialog xml path and file name: " + path);

        File specFile = new File(path);

        // assemble dialog link
        String dialogLink = "";


        if (specName != null) {

            // assemble ITree Servlet Link
            if (specName.equalsIgnoreCase(DialogType.ITREE.toString())) {

                dialogLink = GLOBSET.getWebAppWarName() + "/ITreeDialog?src="
                        + specFile.getName().replace(".xml", "")
                        + "&dialogID=" + d3webKBName + "AND" + specName;


            } else if (specName.contains(DialogType.STANDARD.toString())) {
                //dialogLink = GLOBSET.getWebAppWarName() + "/StandardDialog?src="
                //      + specFile.getName().replace(".xml", "")
                //    + "&dialogID=" + d3webKBName + "AND" + specName;
                dialogLink = GLOBSET.getWebAppWarName() + "/ITreeDialog?src="
                        + specFile.getName().replace(".xml", "")
                        + "&dialogID=" + d3webKBName + "AND" + specName;
            }
            System.err.println("\t - assembled dialog link: " + dialogLink);
        }

        // send link text back to JS
        PrintWriter writer = response.getWriter();
        if (!dialogLink.equals("")) {
            System.err.println("Assemble dialog - SUCCESS");
            writer.write(dialogLink);
        } else {
            writer.write("ERROR--AssembleDialog");
        }
        writer.close();
    }

    /**
     * Stores a dialog link to the list of dialogs (currently: globa, one day:
     * per user)
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void storeDialogToUsersList(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        System.err.println("Store dialog - try to store dialog to dialog link list...");

        // if a dialog link is given (only if confirmed by user)
        if (request.getParameter("dialogLink") != null
                && !request.getParameter("dialogLink").toString().equals("")) {

            String linkToStore = request.getParameter("dialogLink").toString();
            System.err.println("\t - link to store: " + linkToStore);

            // TODO: adapt this when adding user management wrt folders
            PrintWriter writer = response.getWriter();

            // add the link to the dialog list file
            try {
                boolean exists = false;
                File dialogsFile =
                        new File(GLOBSET.getUploadFilesBasePath() + FILESEP + "dialogs.txt");
                System.err.println("\t - dialog store file: " + dialogsFile.getName());

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(dialogsFile)));

                StringBuilder bui = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals(linkToStore)) {
                        exists = true;
                    }
                }
                br.close();

                if (!exists) {

                    BufferedWriter output =
                            new BufferedWriter(new FileWriter(dialogsFile, true));
                    output.write(linkToStore);
                    output.newLine();
                    output.close();
                }

                // get everything from file, put it into correct HTML and rerturn
                // it to JS for AJAX update of LinkList
                try {
                    br = new BufferedReader(
                            new InputStreamReader(
                            new FileInputStream(
                            GLOBSET.getUploadFilesBasePath() + FILESEP + "dialogs.txt")));

                    bui = new StringBuilder();
                    line = "";
                    while ((line = br.readLine()) != null) {
                        bui.append("<div class='row'>");
                        bui.append("<a href='");
                        bui.append(line);
                        bui.append("' target='_blank'>");
                        bui.append(line);
                        bui.append("</a></div>");
                    }
                    br.close();
                    writer.append(REPLACEID + "dialoglinklist");
                    writer.append(REPLACECONTENT);
                    writer.append(bui.toString());
                    System.err.println("Store dialog - SUCCESS");

                } catch (IOException e1) {
                    writer.write("EXCEPTION--StoreDialogToList");
                    e1.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
                    // TODO: write Exception to Exception Report file
                }

            } catch (IOException e1) {
                writer.write("EXCEPTION--StoreDialogToList");
                e1.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
                // TODO: write Exception to Exception Report file
            }
        }
    }

    /**
     * Parse the .doc file with KB specification into the d3web format.
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void parseKBDocument(
            HttpServletRequest request, HttpSession http, HttpServletResponse response)
            throws IOException {

        System.err.println("Parse KB Document - try to parse word to d3web... ");
        PrintWriter writer = response.getWriter();

        String docname = request.getParameter("docname").toString();
        System.err.println("\t - docname of doc to be parsed: " + docname);

        String parseResult = "";

        if (docname != null && !docname.equals("")) {
            parseResult = kbDocSuccessfullyParsed(docname, http);
        } else {
            writer.append("parseErrorInvalidDoc");
        }

        if (parseResult.equals("ParseSuccess")) {
            StringTemplate st =
                    TemplateUtils.getStringTemplate("dialogManager/dialogManager", "html");
            String d3webSelectOpts = fillFilesList("d3web").toString();
            writer.append("success");
            writer.append(";;;" + d3webSelectOpts);
        } else if (parseResult.equals("ParseError")) {
            // don't need system filesep here because its gonna be web path
            String errFilePath =
                    GLOBSET.getWebAppWarName() + "/UPFiles/" + docname + "_Error.html";
            System.err.println("\t - error report filename: " + errFilePath);
            writer.append("showErrFile;" + errFilePath);
            System.err.println("Parse KB Document - show error report...");
        } else if (parseResult.equals("ParseException")) {

            writer.append("showExceptionFile");
            System.err.println("Parse KB Document - show exception report...");
        }
    }

    /**
     * Call the converter which compiles the .doc file into d3web
     *
     * @param docName
     * @return TODO: separate compiling to HTML Error report and parsing
     */
    private String kbDocSuccessfullyParsed(String docName, HttpSession http) {

        String status = "ParseException";
        System.err.println("Parse KB - parser call...");
        Html2KnowWECompiler compiler = new converter.Html2KnowWECompiler();

        String upPath = GLOBSET.getUploadFilesBasePath();
        System.err.println("\t - upload base path: " + upPath);

        // TODO: clean up intermediate files!!

        String doc = upPath + FILESEP + docName;

        String err = upPath + FILESEP + docName + "_Error.html";
        File errFile = new File(err);
        errFile.canExecute();
        errFile.canWrite();
        errFile.canRead();

        String tmp = upPath + FILESEP + "tmp" + FILESEP;
        File tmpF = new File(tmp);
        tmpF.canExecute();
        tmpF.canWrite();
        tmpF.canRead();

        String d3web = upPath + FILESEP + "d3web" + FILESEP;
        File d3wF = new File(d3web);
        d3wF.canExecute();
        d3wF.canWrite();
        d3wF.canRead();

        String knowwe = upPath + FILESEP + "KnowWE-Headless-App.jar";
        File kwF = new File(knowwe);
        kwF.canExecute();
        kwF.canWrite();
        kwF.canRead();

        boolean compileError = false;

        System.err.println("\t - Files for converter and parser: ");
        System.err.println("\t\t - document path and name: " + doc);
        System.err.println("\t\t - error report path and name: " + err);
        System.err.println("\t\t - temporary file path and name: " + tmp);
        System.err.println("\t\t - d3web goal path: " + d3web);
        System.err.println("\t\t - knowwe headless app path: " + knowwe);

        // TODO: write Exceptions to Exception Doc
        try {
            compileError = compiler.compileTo3web(doc, err, tmp, d3web, knowwe);
            System.err.println("\t - Compilation error?: " + compileError);
            if (compileError) {
                // something went wrong during compilation. 
                System.err.println("\t - Compilation error occurred!");
                status = "ParseError";
            } else {

                // OK everything fine. Then rename d3web file and store latest one
                // in webapp/session
                checkAndRenameD3webFile(docName.replace(".doc", ""), d3wF, http);

                Runtime rt = Runtime.getRuntime();
                try {
                    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
                        // need to kill it on ?!
                    } else {
                        System.err.println("\t - try to kill soffice on MAC systems...");
                        String execute = "killall soffice.bin";
                        System.err.println("\t - executing kill command: " + execute);
                        rt.exec(execute);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
                    status = "ParseException";
                }

                System.err.println("Parse KB - parser call SUCCESS...");
                status = "ParseSuccess";
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        } catch (SAXException saxe) {
            saxe.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        } catch (IOException ioe) {
            ioe.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        } catch (InterruptedException ine) {
            ine.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        } catch (NullPointerException npe) {
            npe.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        } catch (Exception e) {
            e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        }


        return status;
    }

    /**
     * TODO: How to handle existing files!?
     *
     * @param targetName
     * @param d3webDir
     * @return
     */
    private void checkAndRenameD3webFile(
            String targetName, File d3webDir, HttpSession http) {

        System.err.println("Rename d3web - try to rename d3web file to former .doc filename...");
        File[] fileList = d3webDir.listFiles();
        if (fileList != null && fileList.length > 0) {
            Arrays.sort(fileList, new LastModifiedComparator());
            File lastFile = fileList[fileList.length - 1];
            System.err.println("\t - latest created d3web file: " + lastFile.getName());

            String targetD3webName = targetName + ".d3web";
            System.err.println("\t - goal d3web filename: " + targetD3webName);

            File targetD3web = new File(d3webDir, targetD3webName);
            lastFile.renameTo(targetD3web);
            lastFile.delete();

            if (targetD3web != null
                    && targetD3web.getName().equals(targetD3webName)) {
                //http.setAttribute("latestD3web", targetD3web);
                System.err.println("Rename d3web - SUCCESS");
            }
        }
    }

    /**
     * Comparator for comparing 2 File Objects by ther lastModified() value
     */
    private class LastModifiedComparator implements Comparator {

        public int compare(Object f1, Object f2) {
            if (f1 instanceof File && f2 instanceof File) {
                Long l1 = new Long(((File) f1).lastModified());
                Long l2 = new Long(((File) f2).lastModified());
                return l1.compareTo(l2);
            }
            return 0;
        }
    }

    protected void finalizeExceptionReport(HttpServletRequest request,
            HttpServletResponse response, HttpSession http)
            throws IOException {

        PrintWriter writer = response.getWriter();
        StringBuilder bui;

        // first read existing contents
        try {
            String logdir = GLOBSET.getSyslogsBasePath();
            File logpath = new File(logdir);
            File logfile = new File(logdir, GLOBSET.getExceptionReportTmpFileName());
            File finalExReportFile = new File(logdir, GLOBSET.getExceptionReportFinalFileName());
            if (!logpath.exists()) {
                logpath.mkdirs();
            }
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
            if (!finalExReportFile.exists()) {
                finalExReportFile.createNewFile();
            }

            System.err.println("Finalize Exception Report - " + logfile.getAbsoluteFile());

            /*
             * Read existing File Contents in temporary exception report
             */
            BufferedReader br;
            String line = "";
            br = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(
                    logfile)));
            bui = new StringBuilder();
            while ((line = br.readLine()) != null) {
                bui.append(line);
                bui.append(LINESEP);
            }
            br.close();

            /*
             * Clear all previous contents of the final Exception Report
             */
            FileWriter fw = new FileWriter(finalExReportFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("");
            bw.close();


            /*
             * Write new Exception Report to final report File
             */
            String finalReportString = assembleExceptionReport(bui.toString());
            System.out.println(finalReportString);
            
            Writer w = new OutputStreamWriter(new FileOutputStream(finalExReportFile), "UTF8");
            BufferedWriter output = new BufferedWriter(w);
            output.write(finalReportString);
            output.newLine();
            output.close();

            String path =
                    GLOBSET.getWebAppWarName() + "/" + 
                    GLOBSET.getSyslogsRelativePathForWeb() + "/"
                    + GLOBSET.getExceptionReportFinalFileName();

            writer.append(path);

        } catch (IOException e1) {
            e1.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        } catch (Exception e) {
            e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        }
    }

    /**
     * Assemble the final (HTML) String which makes up the final Exception
     * Report to be displayed to the user
     *
     * @param exceptionString a String containing only the stored exception(s)
     * @return a String containing the entire (HTML) exception report
     */
    private String assembleExceptionReport(String exceptionString) {
        StringBuilder finalBui = new StringBuilder();
        finalBui.append("<html>");
        finalBui.append("<head>");
        finalBui.append("<title>Exception (Ausnahme) Report</title>");
        finalBui.append("</head>");
        finalBui.append("<h3>Entschuldigung - Eine programminterne Exception (Ausnahme) "
                + "ist aufgetreten. <br> "
                + "<span style='color: red;'>Bitte leiten Sie den Inhalt dieses "
                + "Fensters an den Systemadministrator weiter. </span> <br><br>"
                + "Das Problem wird dann baldmöglichst behoben.<br /><br /><br />"
                + "Fehlermeldung: </h3>");
        finalBui.append("<h5>");
        finalBui.append(exceptionString);
        finalBui.append("</h5>");
        finalBui.append("</html>");

        return finalBui.toString();
    }
}
