package de.d3web.proket.d3web.run;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.*;
import javax.servlet.ServletContext;

/**
 * Servlet for handling the download of a .txt file representation of a dialogs
 * summary
 *
 * @author Martina Freiberg @date 03.02.2012
 */
public class DownloadTxtFileServlet extends HttpServlet {
    
    private final String SUMMARY_FILENAME = "summary.txt";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String fileflag = request.getParameter("flag");
        
        response.setContentType("application/octet-stream");
       
        if(fileflag.equals("summary")){
             response.setHeader("Content-Disposition", "attachment;filename=" + SUMMARY_FILENAME);
        }
        
        ServletContext ctx = getServletContext();
        
        // TODO add method for creating the summary txt file here!
        InputStream is = ctx.getResourceAsStream("/tester.txt");

        int read = 0;
        byte[] bytes = new byte[1024];
        OutputStream os = response.getOutputStream();

        while ((read = is.read(bytes)) != -1) {
            os.write(bytes, 0, read);
        }
        os.flush();
        os.close();
    }
}