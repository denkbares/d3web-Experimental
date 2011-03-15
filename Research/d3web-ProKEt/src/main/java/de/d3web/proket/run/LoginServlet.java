package de.d3web.proket.run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.com.bytecode.opencsv.CSVReader;
import de.d3web.proket.utils.GlobalSettings;

public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {

		res.setContentType("text/html");

		// fetch the information sent via the request string
		String u = req.getParameter("u");
		String p = req.getParameter("p");

		String folderPath = req.getSession().getServletContext().getRealPath("/cases");
		GlobalSettings.getInstance().setCaseFolder(folderPath);

		// get the response writer for communicating back via Ajax
		PrintWriter writer = res.getWriter();

		// no valid login data - deny access
		if (!permitUser(u, p)) {

			// Whatever is written on the writer here, is read from the Ajax
			// call in login.js, that initiated the login check.
			writer.append("nosuccess");
		}
		else {

			// in case writer says success, append "success" so it can handled
			// correspondingly by the calling Ajax (i.e., remove login dialog)

			Cookie cookie = new Cookie(u, "loggedin");

			// here we can set the expire time for the login cookie
			// 60 sec * 30 min = 1800
			// cookie.setMaxAge(1800);
			cookie.setMaxAge(120); // only for testing
			res.addCookie(cookie);

			/*
			 * // Try redirecting the client to the page he first tried to
			 * access try { String target = (String)
			 * session.getAttribute("login.target"); if (target != null) {
			 * res.sendRedirect(target); return; } } catch (Exception ignored) {
			 * }
			 * 
			 * // Couldn't redirect to the target. Redirect to the site's home
			 * // page. res.sendRedirect("/");
			 */
		}
	}

	/**
	 * Check, whether the user has permissions to log in
	 * 
	 * @created 15.03.2011
	 * @param user The user name.
	 * @param password The password.
	 * @return True, if permissions are correct.
	 */
	private boolean permitUser(String user, String password) {

		// cases folder
		String caseFolder = GlobalSettings.getInstance().getCaseFolder();
		String csvFile = caseFolder + "/usrdat.csv";
		CSVReader csvr = null;
		String[] nextLine = null;

		try {
			csvr = new CSVReader(new FileReader(csvFile));
			// go through file
			while ((nextLine = csvr.readNext()) != null) {
				// skip first line
				if (!nextLine[0].startsWith("usr")) {
					// if username and pw could be found, return true
					if (nextLine[0].equals(user) && nextLine[1].equals(password)) {
						return true;
					}
				}
			}

		}
		catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
		}
		catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}

		return false; // trust no one per default
	}
}