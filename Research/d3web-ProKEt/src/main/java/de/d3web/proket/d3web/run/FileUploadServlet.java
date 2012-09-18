/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.run;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mafre
 */
public class FileUploadServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.setContentType("text/html;charset=UTF-8");
        try {
            /**
             * Parse request using MultiparRequest since it is a
             * multipart/form-data throws IOException if request is not
             * multipart
             *
             * So, if form is not yet submitted Upload servlet will show
             * upload.jsp as response if form is submitted Upload servlet will
             * process the request at upload() function
             */
            MultipartRequest multipartRequest = new MultipartRequest(request, getServletContext().getRealPath("/tmp/"), /*
                     * 1MB
                     */ 1024 * 1024, new DefaultFileRenamePolicy());
            if (multipartRequest.getParameter("saveKB") != null) {
                uploadKB(request, response, multipartRequest);
            } else if (multipartRequest.getParameter("saveSPEC") != null) {
                uploadSpec(request, response, multipartRequest);
            } else {
                throw new IOException("Display Upload Dialogue");
            }
        } catch (IOException ex) {
            displayUpload(request, response);
        }
    }

    private void uploadKB(HttpServletRequest request, HttpServletResponse response, MultipartRequest multipartRequest) throws IOException {

        File tmpFile = multipartRequest.getFile("uploadKB");

        File dirToMove = new File(getServletContext().getRealPath("/UPFiles"));
        String newFileName = tmpFile.getName();
        File fileToMove = new File(dirToMove, newFileName);
        tmpFile.renameTo(fileToMove);

        tmpFile.delete();
        response.sendRedirect("/DocLoad?upKB=done");
    }
    
     private void uploadSpec(HttpServletRequest request, HttpServletResponse response, MultipartRequest multipartRequest) throws IOException {

        File tmpFile = multipartRequest.getFile("uploadSPEC");

        File dirToMove = new File(getServletContext().getRealPath("/UPFiles"));
        String newFileName = tmpFile.getName();
        File fileToMove = new File(dirToMove, newFileName);
        tmpFile.renameTo(fileToMove);

        tmpFile.delete();
        response.sendRedirect("/DocLoad?upSPEC=done");
    }

    private void displayUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * Display upload.jsp as response. It is a good convention to use JSPs
         * as Views (i.e. to show response) and Servlets as Controllers (i.e. to
         * catch request and process)
         */
        getServletContext().getRequestDispatcher("/DocLoad").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        processRequest(request, response);
    }
}
