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
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import de.d3web.proket.utils.GlobalSettings;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet used for uploading files. Currently, .xml and .doc files. - TODO:
 * check filetypes/extensions!
 *
 * @author Martina Freiberg @date September 2012
 */
public class FileUploadServlet extends HttpServlet {

    // TODO read this from specs file possibly
    //private static String WEBAPP_NAME = "/";
    private static String PATHSEP = System.getProperty("file.separator");
    protected final GlobalSettings GLOBSET = GlobalSettings.getInstance();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

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
            MultipartRequest multipartRequest = new MultipartRequest(request, getServletContext().getRealPath(PATHSEP + "tmp" + PATHSEP), /*
                     * 1MB
                     */ 1024 * 1024, new DefaultFileRenamePolicy());
            if (multipartRequest.getParameter("saveKB") != null) {

                // if the saveKB button had been clicked, a KB is to be uploaded
                uploadKB(request, response, multipartRequest);

            } else if (multipartRequest.getParameter("saveSPEC") != null) {

                // if the saveSPEC button had been clicked, a Spec is to be uploaded
                uploadSpec(request, response, multipartRequest);

            } else {
                throw new IOException("Display Upload Dialogue");
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
        if (tmpFile != null) {

            if (!tmpFile.getName().endsWith(".doc")
                    && !tmpFile.getName().endsWith(".d3web")
                    && !tmpFile.getName().endsWith(".zip")
                    && !tmpFile.getName().endsWith(".xls")
                    && !tmpFile.getName().endsWith(".xlsx")) {
                
                response.sendRedirect(GLOBSET.getWebAppWarName()
                        + "/DialogManager?upERR=nokb");
            } else {

                File dirToMove = new File(GLOBSET.getUploadFilesBasePath());
                String newFileName = tmpFile.getName();
                File fileToMove = new File(dirToMove, newFileName);
                tmpFile.renameTo(fileToMove);

                HttpSession httpSession = request.getSession();
                // TODO: move this to processDOC Method where doc is parsed into d3web, later
                //TODO: flexible    httpSession.setAttribute("latestD3web", newFileName);
                httpSession.setAttribute("latestDoc", newFileName);

                tmpFile.delete();
                response.sendRedirect(GLOBSET.getWebAppWarName()
                        + "/DialogManager?upKB=done&upfilename=" + newFileName);

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

    /**
     * Just redirect to the plain upload page for redisplaying
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void displayUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * Display upload.jsp as response. It is a good convention to use JSPs
         * as Views (i.e. to show response) and Servlets as Controllers (i.e. to
         * catch request and process)
         */
        getServletContext().getRequestDispatcher("/DialogManager").forward(request, response);
    }

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
}
