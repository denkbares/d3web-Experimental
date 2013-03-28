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

import com.oreilly.servlet.MultipartRequest;
import de.d3web.proket.d3web.input.D3webXMLParserOrig;
import de.d3web.proket.d3web.utils.StringTemplateUtils;
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
import de.uniwue.exceparser.ExcelParserApplication;
import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
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
    protected D3webXMLParserOrig d3webParser;
    private static String FILESEP = System.getProperty("file.separator");
    protected static final String REPLACECONTENT = "##replacecontent##";
    protected static final String REPLACEID = "##replaceid##";
    protected static final String LINESEP = System.getProperty("line.separator");
    private String docname = "";
    private MultipartRequest mpRequest;
    private File currentUploadFile;

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
	String realStPath = servletcontext
		+ "WEB-INF/classes/stringtemp/html";
	GLOBSET.setServletBasePath(servletcontext);
	StringTemplateUtils.initializeStringTemplateStructure(realStPath);

	// TODO: make property in specs
	GLOBSET.setWebAppWarName("/UploadParseTool");
	//GLOBSET.setWebAppWarName("");

	String webappname =
		GLOBSET.getWebAppWarName().equals("") ? "" : GLOBSET.getWebAppWarName() + "_";


	// the storage for files which lies outside the webapp. E.g. for parsed KBs
	String base = servletcontext.substring(0, servletcontext.length() - 1);
	// cut two inner dirs as we want this to be all outside the inner webapp
	base = base.replace(base.substring(base.lastIndexOf(FILESEP), base.length()), "");
	base = base.replace(base.substring(base.lastIndexOf(FILESEP), base.length()), "");

	String storeOutsideWUMPPath =
		base + FILESEP + webappname.replaceFirst("/", "") + GLOBSET.getWUMPFolderName();

	File storageDir = Utils.checkCreateDir(storeOutsideWUMPPath);
	storageDir.canExecute();
	storageDir.canWrite();
	storageDir.canRead();

	GLOBSET.setStoreOutsideWUMPPath(storeOutsideWUMPPath);

	GLOBSET.setUploadFilesBasePath(GLOBSET.getServletBasePath() + GLOBSET.getUploadFilesFolderName());


	// initialize the d3web parser
	d3webParser = new D3webXMLParserOrig();

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

	System.err.println("base: " + base);
	System.err.println("FILESEP: " + FILESEP);
	System.err.println("webappname: " + webappname);
	System.err.println("WUMPFOLDERNAME: " + GLOBSET.getWUMPFolderName());
	System.err.println("storeOutsideWUMPPath: " + storeOutsideWUMPPath);

	System.err.println("storeOutsideWUMPPath: " + storeOutsideWUMPPath);
	System.err.println("OutsideWUMPPath: " + GLOBSET.getStoreOutsideWUMPPath());
	System.err.println("UploadFilesBasePath: " + GLOBSET.getUploadFilesBasePath());
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
	//httpSession.setMaxInactiveInterval(20 * 60);

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
	    assembleDialogLink(request, response, httpSession);
	} 	else if (action.equalsIgnoreCase("assembleDialogStartDirectly")) {
	    assembleDialogLinkForDirectCall(request, response, httpSession);
	}	
	else if (action.equalsIgnoreCase("storeDialogToUsersList")) {
	    storeDialogToUsersList(request, response, httpSession);
	} else if (action.equalsIgnoreCase("deleteSelectedKB")) {
	    deleteSelectedKB(request, response, httpSession);
	} else if (action.equalsIgnoreCase("finalizeExceptionReport")) {
	    finalizeExceptionReport(request, response, httpSession);
	}
	if (action.equals("uploadAndOverwrite")) {
	    try {
		/**
		 * Parse request using MultiparRequest since it is a
		 * multipart/form-data throws IOException if request is not
		 * multipart *
		 */
		MultipartRequest multipartRequest =
			this.mpRequest;

		if (multipartRequest.getParameter("saveKB") != null
			&& request.getParameter("overwrite") != null) {

		    // if the saveKB button had been clicked, a KB is to be uploaded
		    uploadKBOverwrite(request, response);

		} else if (multipartRequest.getParameter("saveSPEC") != null) {
		    // TODO 
		}
	    } catch (IOException ex) {
		// in Exception case, i.e., no file provided, show upload plainly
		ex.printStackTrace();
		ex.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
	    }
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

	st.setAttribute("kbuploadfieldlabel", "Wissensbasis-Datei wählen (.doc/.xlsx/.d3web)");
	st.setAttribute("specuploadfieldlabel", "UI Spezifikation wählen (.xml)");

	String cssString = FileUtils.getString(css);
	st.setAttribute("css", cssString);

	boolean withoutExceptions = false;
	if (!fillD3webList().toString().contains("EXCEPTION")) {
	    withoutExceptions = true;
	}
	st.setAttribute("fileselectopts_" + "d3web", fillD3webList().toString());

	if (!fillSpecsOptionsList().toString().contains("EXCEPTION")) {
	    withoutExceptions = true;
	}
	st.setAttribute("fileselectopts_" + "specs", fillSpecsOptionsList().toString());


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
    }

    /**
     * Fill the dialog list HTML
     *
     * @param st the StringTemplate to be filled
     * @return true if filling the dialog list was successful, false otherwise
     * (e.g. in case of an exception)
     */
    private StringBuilder fillDialogList() {

	String path = GLOBSET.getUploadFilesBasePath();
	String dialogs = path + FILESEP + "dialogs.txt";

	File dialogsF = Utils.checkCreateFile(dialogs);

	StringBuilder bui = new StringBuilder();
	System.err.println("Fill Dialog list - try to get and display stored dialogs...");
	try {
	    BufferedReader br;

	    String line = "";
	    br = new BufferedReader(
		    new InputStreamReader(
		    new FileInputStream(dialogsF)));
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

	} catch (IOException e1) {
	    e1.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
	    bui.append("EXCEPTION");
	} catch (Exception e) {
	    e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
	    bui.append("EXCEPTION");
	}
	return bui;
    }

    private StringBuilder fillD3webList() {

	System.err.println("Fill d3web files list - try to get and display files...");
	// assemble path where files are stored
	String dir =
		GLOBSET.getStoreOutsideWUMPPath() + FILESEP + "d3web";
	Utils.checkCreateDir(dir);
	System.err.println("\t - d3web filepath on server: " + dir);

	// get all files under the given path
	List<File> files = Utils.getFileList(dir);

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

	}
	return bui;
    }

    private StringBuilder fillSpecsOptionsList() {

	System.err.println("Fill specs files list - try to get and display files...");
	// assemble path where files are stored
	String dir =
		GLOBSET.getUploadFilesBasePath() + FILESEP + "specs";
	Utils.checkCreateDir(dir);
	System.err.println("\t - specs filepath on server: " + dir);

	// get all files under the given path
	List<File> files = Utils.getFileList(dir);

	StringBuilder bui = new StringBuilder();
	if (files.size() > 0) {

	    for (File file : files) {
		if (!file.getName().equals("empty.txt")) {
		    bui.append("<option");
		    // omitt filetype ending
		    String filename =
			    file.getName().split("\\.")[0];
		    bui.append(" title='" + filename + "'>");
		    bui.append(filename);
		    bui.append("</option>");
		}
	    }

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
	//String path = GLOBSET.getUploadFilesBasePath() + FILESEP
	//+ "d3web/";
	String path = GLOBSET.getStoreOutsideWUMPPath() + FILESEP + "d3web" + FILESEP;

	System.err.println("\t - kb filepath on server: " + path);
	File fileToDelete = new File(path + kbName + ".d3web");
	if (fileToDelete.exists()) {
	    deleteSuccess = fileToDelete.delete();
	}

	PrintWriter writer = response.getWriter();
	if (deleteSuccess) {

	    writer.append(REPLACEID + "d3webSelect");
	    writer.append(REPLACECONTENT);
	    writer.append("<select id='d3webSelect' class='d3webSelect' size='12' name='d3webfiles'>");
	    writer.append(fillD3webList());
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
    protected void assembleDialogLink(HttpServletRequest request,
	    HttpServletResponse response,
	    HttpSession httpSession) throws IOException {

	System.err.println("Assemble Dialog - try to assemble dialog string from selected KB & UI");

	String d3webKBName = request.getParameter("kb").toString();
	System.err.println("\t - KB name: " + d3webKBName);

	String specName = request.getParameter("spec").toString();
	System.err.println("\t - UI spec name: " + specName);

	String path =
		GLOBSET.getUploadFilesBasePath()
		+ "/specs/" + specName + ".xml";

	System.err.println("\t - dialog xml path and file name: " + path);

	File specFile = new File(path);

	// assemble dialog link
	String dialogLink = "";

	httpSession.setAttribute("WUMP", "true");
	httpSession.setAttribute("initFromUpload", "init");

	if (specName != null) {

	    // assemble ITree Servlet Link
	    if (specName.equalsIgnoreCase(DialogType.ITREE.toString())) {

		dialogLink = GLOBSET.getWebAppWarName()
			+ "/ITreeDialog?src="
			+ specFile.getName().replace(".xml", "")
			+ "&dialogID=" + d3webKBName + "AND" + specName;


	    } else if (specName.contains(DialogType.QUESTIONARYCONS.toString())) {


		dialogLink = GLOBSET.getWebAppWarName()
			+ "/StandardDialog?src="
			+ specFile.getName().replace(".xml", "")
			+ "&dialogID=" + d3webKBName + "AND" + specName;

		//dialogLink = GLOBSET.getWebAppWarName() + "/StandardDialog?src="
		//      + specFile.getName().replace(".xml", "")
		//    + "&dialogID=" + d3webKBName + "AND" + specName;

	    }
	    System.err.println("\t - assembled dialog link: " + dialogLink);
	}

	// send link text back to JS
	PrintWriter writer = response.getWriter();
	if (!dialogLink.equals("")) {
	    writer.write(dialogLink);
	} else {
	    writer.write("ERROR--AssembleDialog");
	}
	writer.close();
    }
    
    
     protected void assembleDialogLinkForDirectCall(HttpServletRequest request,
	    HttpServletResponse response,
	    HttpSession httpSession) throws IOException {

	System.err.println("Assemble Dialog - try to assemble dialog string from selected KB & UI");

	String d3webKBName = request.getParameter("kb").toString();
	System.err.println("\t - KB name: " + d3webKBName);

	String specName = request.getParameter("spec").toString();
	System.err.println("\t - UI spec name: " + specName);

	String path =
		GLOBSET.getUploadFilesBasePath()
		+ "/specs/" + specName + ".xml";

	System.err.println("\t - dialog xml path and file name: " + path);

	File specFile = new File(path);

	// assemble dialog link
	String dialogLink = "";

	httpSession.setAttribute("WUMP", "true");
	httpSession.setAttribute("initFromUpload", "init");

	if (specName != null) {

	    // assemble ITree Servlet Link
	    if (specName.equalsIgnoreCase(DialogType.ITREE.toString())) {

		dialogLink = 
			 "/ITreeDialog?src="
			+ specFile.getName().replace(".xml", "")
			+ "&dialogID=" + d3webKBName + "AND" + specName;


	    } else if (specName.contains(DialogType.QUESTIONARYCONS.toString())) {


		dialogLink = 
			 "/StandardDialog?src="
			+ specFile.getName().replace(".xml", "")
			+ "&dialogID=" + d3webKBName + "AND" + specName;

		//dialogLink = GLOBSET.getWebAppWarName() + "/StandardDialog?src="
		//      + specFile.getName().replace(".xml", "")
		//    + "&dialogID=" + d3webKBName + "AND" + specName;

	    }
	    System.err.println("\t - assembled dialog link: " + dialogLink);
	}

	// send link text back to JS
	PrintWriter writer = response.getWriter();
	if (!dialogLink.equals("")) {
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

	docname = request.getParameter("docname").toString().replace("%", " ");
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
	    String d3webSelectOpts = fillD3webList().toString();
	    writer.append("success");
	    writer.append(";;;" + d3webSelectOpts);
	} else if (parseResult.equals("ParseError")) {
	    // don't need system filesep here because its gonna be web path
	    String errFilePath =
		    GLOBSET.getWebAppWarName() + "/"
		    + GLOBSET.getUploadFilesFolderName() + "/"
		    + docname + "_Error.html";
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
	//Html2KnowWECompiler compiler = new converter.Html2KnowWECompiler();


	converter.compiler.Compiler compiler = null;
	try {
	    compiler = new converter.compiler.Compiler();
	    /*
	     * if (true) { throw new Exception(); }
	     */
	} catch (Exception e) {
	    e.printStackTrace(SystemLoggerUtils.getEventLoggerStream());
	}

	String upPath = GLOBSET.getUploadFilesBasePath();
	System.err.println("\t - upload base path: " + upPath);

	// TODO: clean up intermediate files!!

	String doc = upPath + FILESEP + docName;
	File docToParse = new File(doc);

	String err = upPath + FILESEP + docName + "_Error.html";
	File errFile = new File(err);
	errFile.canExecute();
	errFile.canWrite();
	errFile.canRead();

	String tmp = upPath + FILESEP + "tmp" + FILESEP;
	File tmpF = Utils.checkCreateDir(tmp);
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


	System.err.println("\t - Files for converter and parser: ");
	System.err.println("\t\t - document path and name: " + doc);
	System.err.println("\t\t - error report path and name: " + err);
	System.err.println("\t\t - temporary file path and name: " + tmp);
	System.err.println("\t\t - d3web goal path: " + d3web);
	System.err.println("\t\t - knowwe headless app path: " + knowwe);

	// TODO: write Exceptions to Exception Doc
	try {

	    System.out.println("DiaMan: " + docToParse.getName());
	    // integration of Georg D. Parser with clojure here ->
	    if (docToParse.getName().endsWith(".xlsx")) {

		boolean compiled = ExcelParserApplication.compile(doc, err, tmp, d3web, knowwe);

		if (compiled) {

		    // OK everything fine. Then rename d3web file and store latest one
		    // in webapp/session
		    docName = docName.replace(".doc", "").replace(".xlsx", "").replace(".csv", "");
		    checkAndRenameD3webFile(docName, d3wF, http);
		    errFile.delete();
		    status = "ParseSuccess";

		} else {

		    // something went wrong during compilation. 
		    System.err.println("\t - Compilation error occurred!");
		    status = "ParseError";
		}

	    } else {

		boolean compileError = compiler.compile(doc, err, tmp, d3web, knowwe);

		System.err.println("\t - Compilation error?: " + compileError);
		if (compileError) {
		    // something went wrong during compilation. 
		    System.err.println("\t - Compilation error occurred!");
		    status = "ParseError";
		} else {

		    // OK everything fine. Then rename d3web file and store latest one
		    // in webapp/session
		    docName = docName.replace(".doc", "").replace(".xlsx", "").replace(".csv", "");
		    checkAndRenameD3webFile(docName, d3wF, http);
		    errFile.delete();
		    status = "ParseSuccess";
		}

		Runtime rt = Runtime.getRuntime();
		try {
		    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
			// need to kill it on ?!
		    } else {
			//System.err.println("\t - try to kill soffice on MAC systems...");
			String execute = "killall soffice.bin";
			//System.err.println("\t - executing kill command: " + execute);
			rt.exec(execute);
		    }
		} catch (IOException ioe) {
		    ioe.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
		    status = "ParseException";
		}
	    }

	    System.err.println("\t - Doc Parsed: " + docToParse.getAbsolutePath());

	    System.gc();
	    docToParse.setWritable(true);
	    docToParse.delete();

	    System.err.println("\t - EXISTS?: " + docToParse.exists());

	    // MUSS FÜR ELMARS PARSER WIEDER REIN!
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
     * TODO: How to handle existing files!? Get the latest compiled d3web file
     * (which should always be the one single file in this dir), rename it, copy
     * it to outside storage, and delete it here.
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
	    for (File f : fileList) {

		if (!f.getName().equals("empty.txt")) {
		    File toRename = f;
		    System.err.println("\t - latest created d3web file: " + toRename.getName());
		    String targetD3webName = targetName + ".d3web";
		    System.err.println("\t - goal d3web filename: " + targetD3webName);
		    String targetDir =
			    GLOBSET.getStoreOutsideWUMPPath() + FILESEP + "d3web";
		    Utils.checkCreateDir(targetDir);
		    String targetD3web = targetDir + FILESEP + targetD3webName;
		    File tf = new File(targetD3web);

		    if (tf.exists()) {
			tf.delete();
		    }
		    toRename.renameTo(new File(targetD3web));

		    toRename.delete();

		    // if (tf != null
		    //       && tf.getName().equals(targetD3webName)) {
		    //http.setAttribute("latestD3web", targetD3web);
		    // }
		}
	    }


	}
    }

    /**
     * Comparator for comparing 2 File Objects by ther lastModified() value
     */
    /*
     * private class LastModifiedComparator implements Comparator {
     *
     * public int compare(Object f1, Object f2) { if (f1 instanceof File && f2
     * instanceof File) { Long l1 = new Long(((File) f1).lastModified()); Long
     * l2 = new Long(((File) f2).lastModified()); return l1.compareTo(l2); }
     * return 0; } }
     */
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

	    String outsideStorageDir =
		    GLOBSET.getStoreOutsideWUMPPath() + FILESEP + "syslogs";

	    // TODO: check: problem with creation of outside storage dir?!
	    Utils.checkCreateDir(outsideStorageDir);
	    System.err.println("\t - OutsideStorageDir: " + new File(outsideStorageDir).getAbsolutePath());

	    GregorianCalendar greg = new GregorianCalendar();

	    String filenameToStore = String.valueOf(greg.get(GregorianCalendar.YEAR))
		    + String.valueOf(greg.get(GregorianCalendar.MONTH))
		    + String.valueOf(greg.get(GregorianCalendar.DATE))
		    + String.valueOf(greg.get(GregorianCalendar.HOUR_OF_DAY))
		    + "_"
		    + String.valueOf(greg.get(GregorianCalendar.HOUR_OF_DAY))
		    + String.valueOf(greg.get(GregorianCalendar.MINUTE))
		    + String.valueOf(greg.get(GregorianCalendar.SECOND))
		    + "_" + finalExReportFile.getName();


	    String toRenameTo = new File(outsideStorageDir).getAbsolutePath() + FILESEP + filenameToStore;
	    System.err.println("\t - To Rename To path and filename: " + toRenameTo);


	    File fileToCopy = Utils.checkCreateFile(toRenameTo);


	    /*
	     * Read existing File Contents in temporary exception report
	     */
	    BufferedReader br;
	    String line = "";
	    br = new BufferedReader(new InputStreamReader(
		    new FileInputStream(logfile)));
	    bui = new StringBuilder();
	    while ((line = br.readLine()) != null) {
		bui.append(line);
		bui.append(LINESEP);
	    }
	    br.close();

	    boolean copied = logfile.renameTo(fileToCopy);

	    if (!copied) {
		SystemLoggerUtils.getExceptionLoggerStream().println(
			"File " + finalExReportFile.getName() + " could not be "
			+ "copied to " + toRenameTo);
	    }

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

	    Writer w = new OutputStreamWriter(new FileOutputStream(finalExReportFile), "UTF8");
	    BufferedWriter output = new BufferedWriter(w);
	    output.write(finalReportString);
	    output.newLine();
	    output.close();

	    String path =
		    GLOBSET.getWebAppWarName() + "/"
		    + GLOBSET.getSyslogsRelativePathForWeb() + "/"
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
	Date d = new Date();
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
		+ "Fehlermeldung vom " + d.toString() + ", aufgetreten bei der "
		+ "Verarbeitung des Dokuments <b>" + docname + "</b>:</h3>");
	finalBui.append("<h5>");
	finalBui.append(exceptionString);
	finalBui.append("</h5>");
	finalBui.append("</html>");

	return finalBui.toString();
    }

    private void uploadKBOverwrite(HttpServletRequest request, HttpServletResponse response) throws IOException {


	File tmpFile = this.currentUploadFile;
	if (tmpFile != null) {

	    try {
		System.err.println("FileUploadServlet - UploadKB - upload base path: "
			+ GLOBSET.getUploadFilesBasePath());
		File dirToMove = new File(GLOBSET.getUploadFilesBasePath());
		System.err.println("FileUploadServlet - UploadKB - filepath to be written: "
			+ dirToMove.getAbsolutePath());

		String newFileName = tmpFile.getName();
		File fileToMove = new File(dirToMove, newFileName);

		System.err.println("FileUploadServlet - UploadKB - final file: "
			+ fileToMove.getAbsolutePath());

		// if old equally named file already there: delete it
		if (fileToMove.exists()) {
		    System.err.println("FileUploadServlet - try to delete: "
			    + fileToMove.getAbsolutePath());
		    fileToMove.setWritable(true);
		    fileToMove.delete();
		    System.err.println("FileUploadServlet - deleted file: "
			    + fileToMove.getAbsolutePath() + " - " + fileToMove.exists());
		}

		tmpFile.renameTo(new File(dirToMove, newFileName));

		HttpSession httpSession = request.getSession();

		httpSession.setAttribute("latestDoc", newFileName);

		tmpFile.setWritable(true);
		tmpFile.delete();
		//response.sendRedirect(GLOBSET.getWebAppWarName()
		//      + "/DialogManager?upKB=done&upfilename=" + newFileName.replace("%", " "));
		PrintWriter writer = response.getWriter();
		writer.append(newFileName.replace("%", " "));

		this.currentUploadFile = null;

	    } catch (Exception e) {
		PrintWriter writer = response.getWriter();
		writer.append("NOPARSE");
		e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
	    }
	}
    }

    /**
     * File Upload Functionality
     *
     */
    /**
     * Bundle the requests
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	processRequest(request, response);
    }

    // Process the upload request
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	response.setContentType("text/html;charset=UTF-8");
	try {
	    /**
	     * Parse request using MultiparRequest since it is a
	     * multipart/form-data throws IOException if request is not
	     * multipart *
	     */
	    MultipartRequest multipartRequest = new MultipartRequest(request, getServletContext().getRealPath(FILESEP + "tmp" + FILESEP), /*
		     * 1MB
		     */ 1024 * 1024);


	    if (multipartRequest.getParameter("saveKB") != null) {

		this.mpRequest = multipartRequest;
		// if the saveKB button had been clicked, a KB is to be uploaded
		uploadKB(request, response, multipartRequest);

	    } else if (multipartRequest.getParameter("saveSPEC") != null) {

		this.mpRequest = multipartRequest;
		// if the saveSPEC button had been clicked, a Spec is to be uploaded
		uploadSpec(request, response, multipartRequest);

	    }
	} catch (IOException ex) {
	    // in Exception case, i.e., no file provided, show upload plainly
	    displayUpload(request, response);
	}
    }

    /**
     * Processing a KB (d3web) upload
     *
     * @param request
     * @param response
     * @param multipartRequest
     * @throws IOException
     */
    private void uploadKB(HttpServletRequest request, HttpServletResponse response, MultipartRequest multipartRequest) throws IOException {

	File tmpFile = multipartRequest.getFile("uploadKB");
	multipartRequest = null;

	this.currentUploadFile = tmpFile;

	if (currentUploadFile != null) {

	    if (!tmpFile.getName().endsWith(".doc")
		    //  && !tmpFile.getName().endsWith(".d3web")
		    //&& !tmpFile.getName().endsWith(".zip")
		    //&& !tmpFile.getName().endsWith(".xls")
		    && !tmpFile.getName().endsWith(".xlsx")
		    && !currentUploadFile.getName().endsWith(".csv")) {

		response.sendRedirect(GLOBSET.getWebAppWarName()
			+ "/DialogManager?upERR=nokb");
	    } else {

		if (currentUploadFile.getName().endsWith(".d3web")) {
		    // TODO add handling of existing files here also!

		    // directly store it into the "real" outside storage path
		    // for d3web and refresh list view
		    String targetD3webName = currentUploadFile.getName();
		    String targetDir =
			    GLOBSET.getStoreOutsideWUMPPath() + FILESEP + "d3web";
		    Utils.checkCreateDir(targetDir);
		    String targetD3web = targetDir + FILESEP + targetD3webName;
		    File tf = new File(targetD3web);
		    currentUploadFile.renameTo(tf);
		    response.sendRedirect(GLOBSET.getWebAppWarName()
			    + "/DialogManager?upKB=done&upfilename=" + targetD3webName.replace("%", " "));

		    //tmpFile.delete();

		} else {


		    try {
			System.err.println("FileUploadServlet - UploadKB - upload base path: "
				+ GLOBSET.getUploadFilesBasePath());
			File dirToMove = new File(GLOBSET.getUploadFilesBasePath());
			System.err.println("FileUploadServlet - UploadKB - filepath to be written: "
				+ dirToMove.getAbsolutePath());

			String newFileName = currentUploadFile.getName();
			File fileToMove = new File(dirToMove, newFileName);

			System.err.println("FileUploadServlet - UploadKB - final file: "
				+ fileToMove.getAbsolutePath());


			/*
			 * for checking if file already in repository OUTSIDE
			 * webapp
			 */
			String targetDir =
				GLOBSET.getStoreOutsideWUMPPath() + FILESEP + "d3web";
			Utils.checkCreateDir(targetDir);
			// remove file ending
			String fnD3web =
				newFileName.replace(".xlsx", ".d3web").replace(".csv", ".d3web").replace(".doc", ".d3web");
			String targetD3web = targetDir + FILESEP + fnD3web;
			File tf = new File(targetD3web);

			// if old equally named file already there: delete it
			if (tf.exists()) {

			    // ask whether to delete file in dialog manager
			    response.sendRedirect(GLOBSET.getWebAppWarName()
				    + "/DialogManager?fileExists=true&upfilename=" + newFileName.replace("%", " "));

			} else {

			    currentUploadFile.renameTo(new File(dirToMove, newFileName));

			    HttpSession httpSession = request.getSession();

			    httpSession.setAttribute("latestDoc", newFileName);

			    currentUploadFile.setWritable(true);
			    currentUploadFile.delete();
			    //System.out.println("CURRENT UPLOAD FILE: EXISTS? " + currentUploadFile.exists());

			    response.sendRedirect(GLOBSET.getWebAppWarName()
				    + "/DialogManager?upKB=done&upfilename=" + newFileName.replace("%", " "));
			    currentUploadFile = null;
			}

		    } catch (Exception e) {
			e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
		    }
		}

	    }
	} else {
	    response.sendRedirect(GLOBSET.getWebAppWarName()
		    + "/DialogManager?upERR=nofile");
	}


    }

    /**
     * Processing a specification (XML) upload
     *
     * @param request
     * @param response
     * @param multipartRequest
     * @throws IOException
     */
    private void uploadSpec(HttpServletRequest request,
	    HttpServletResponse response,
	    MultipartRequest multipartRequest) throws IOException {

	File tmpFile = multipartRequest.getFile("uploadSPEC");
	if (tmpFile != null) {

	    if (!tmpFile.getName().endsWith(".xml")) {
		response.sendRedirect(GLOBSET.getWebAppWarName()
			+ "/DialogManager?upERR=noxml");
	    }
	    File dirToMove = new File(GLOBSET.getUploadFilesBasePath());
	    String newFileName = tmpFile.getName();
	    File fileToMove = new File(dirToMove, newFileName);
	    tmpFile.renameTo(fileToMove);

	    HttpSession httpSession = request.getSession();
	    httpSession.setAttribute("latestSpec", fileToMove);

	    tmpFile.delete();
	    response.sendRedirect(GLOBSET.getWebAppWarName()
		    + "/DialogManager?upSPEC=done");
	} else {
	    response.sendRedirect(GLOBSET.getWebAppWarName()
		    + "/DialogManager?upERR=nofile");
	}


    }

    private void displayUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	/**
	 * Display upload.jsp as response. It is a good convention to use JSPs
	 * as Views (i.e. to show response) and Servlets as Controllers (i.e. to
	 * catch request and process)
	 */
	getServletContext().getRequestDispatcher("/DialogManager").forward(request, response);
    }
}
