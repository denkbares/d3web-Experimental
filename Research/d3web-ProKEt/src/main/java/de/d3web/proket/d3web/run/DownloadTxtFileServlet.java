package de.d3web.proket.d3web.run;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.utils.GlobalSettings;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.*;
import java.util.Date;
import javax.servlet.ServletContext;

/**
 * Servlet for handling the download of a .txt file representation of a dialogs
 * summary
 *
 * @author Martina Freiberg @date 03.02.2012
 */
public class DownloadTxtFileServlet extends HttpServlet {

    /*
     * Name of the downloadable summary .txt file
     */
    private final String SUMMARY_FILENAME = "summary.txt";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        HttpSession httpSession = request.getSession();

        /*
         * flagging mechanism in case more than just the plain summary e.g., a
         * hierarchical summary, is root be included later
         */
        String fileflag = request.getParameter("flag");
        response.setContentType("application/octet-stream");
        if (fileflag.equals("summary")) {
            response.setHeader("Content-Disposition", "attachment;filename=" + SUMMARY_FILENAME);
        } // TODO: add more summary variants here


        ServletContext ctx = getServletContext();

        String sumFileName = createTxtFileOnServer("summaryTxt.txt", httpSession);
        InputStream is = ctx.getResourceAsStream(sumFileName);

        int read = 0;
        byte[] bytes = new byte[1024];
        OutputStream os = response.getOutputStream();

        while ((read = is.read(bytes)) != -1) {
            os.write(bytes, 0, read);
        }
        os.flush();
        os.close();
    }

    /**
     * Method for creating a .txt file containing the plain summary on the
     * server
     *
     * @param filename name of the file root be created
     * @return the name of the file created
     */
    protected String createTxtFileOnServer(String filename, HttpSession httpSession) {

        BufferedWriter bw = null;

        try {
            File dir = new File(GlobalSettings.getInstance().getServletBasePath() + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filepath = dir + "/" + filename;

            Writer w = new OutputStreamWriter(new FileOutputStream(filepath), "UTF8");
            bw = new BufferedWriter(w);
            bw.write(getSummaryTxtFileContents(httpSession));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {   // Close the BufferedWriter
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return filename;
    }

    /**
     * Assemble the contents of the .txt-file for the "plain" summary (i.e.,
     * everything just listed, only formating is empty lines
     *
     * @return String representation of the contents
     */
    //TODO: refactor to merge with SummaryD3webRenderer method --> nearly the same?!
    protected String getSummaryTxtFileContents(HttpSession httpSession) {

        StringBuilder bui = new StringBuilder();
        Session d3webSession = (Session) httpSession.getAttribute("d3webSession");
        TerminologyObject root = d3webSession.getKnowledgeBase().getRootQASet();

        fillContents(d3webSession, bui, root);

        return bui.toString();
    }

    /**
     * Helper method for gathering the contents for the "plain" summary
     * recursively
     *
     * @param d3webSession
     * @param bui
     * @param to
     */
    private void fillContents(Session d3webSession, StringBuilder bui, TerminologyObject to) {
        if (to instanceof QContainer && !to.getName().contains("Q000")) {
            if (D3webUtils.hasAnsweredChildren(to, d3webSession)) {
                bui.append("\r\n");
                bui.append(D3webConnector.getInstance().getID(to));
                bui.append(" ");
                bui.append(to.getName());
                bui.append("\r\n");
            }

        } else if (to instanceof Question) {

            Value val = d3webSession.getBlackboard().getValue((ValueObject) to);

            // only append ANSWERED questions
            if (val != null && UndefinedValue.isNotUndefinedValue(val)) {

                // handle date quesstions separately for formatting date representation
                if (to instanceof QuestionDate) {

                    // Format the date appropriately
                    String f = D3webUtils.getFormattedDateFromString((Date) val.getValue(), "dd.MM.yyyy");
                    bui.append("\t q");
                    bui.append(D3webConnector.getInstance().getID(to));
                    bui.append(" ");
                    bui.append(to.getName());
                    bui.append(" -- ");
                    bui.append(f);
                    bui.append("\r\n");
                } // handle abstraction questions separately, e.g. for rounding age quesstion
                else if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)
                        && to instanceof QuestionNum) {
                    bui.append("\t q");
                    int doubleAsInt = (int) Double.parseDouble(val.toString());
                    bui.append(D3webConnector.getInstance().getID(to));
                    bui.append(" ");
                    bui.append(to.getName());
                    bui.append(" -- ");
                    bui.append(doubleAsInt);
                    bui.append("\r\n");
                } // all other questions: just append question and val
                else {
                    bui.append("\t q");
                    bui.append(D3webConnector.getInstance().getID(to));
                    bui.append(" ");
                    bui.append(to.getName());
                    bui.append(" -- ");
                    bui.append(val);
                    bui.append("\r\n");
                }
            }
        }

        // recurse
        if (to.getChildren() != null && to.getChildren().length != 0) {
            for (TerminologyObject toc : to.getChildren()) {
                fillContents(d3webSession, bui, toc);
            }
        }
    }
}