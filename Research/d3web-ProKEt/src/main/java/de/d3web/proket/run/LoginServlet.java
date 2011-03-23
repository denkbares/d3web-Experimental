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
import javax.servlet.http.HttpSession;

import au.com.bytecode.opencsv.CSVReader;
import de.d3web.proket.utils.GlobalSettings;

public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {

		res.setContentType("text/html");

		// fetch the information sent via the request string from login
		String u = req.getParameter("u");
		String p = req.getParameter("p");

		// TODO check if needed here
		String folderPath = req.getSession().getServletContext().getRealPath("/cases");
		GlobalSettings.getInstance().setCaseFolder(folderPath);

		// get the response writer for communicating back via Ajax
		PrintWriter writer = res.getWriter();

		// get current HTTPSession
		HttpSession sess = req.getSession(true);

		// System.out.println(u + " " + sess.getAttribute("user"));

		/*
		 * check whether login user is already running a session (e.g. diff
		 * browser or the like)
		 */
		if (!sess.isNew()) {


			Cookie c = new Cookie(u, "loggedin");
			c.setMaxAge(30 * 60);
			res.addCookie(c);

			// set user attribute for the HttpSession
			sess.setAttribute("user", u);

			// same user append causes JS just to redirect to dialog
			writer.append("sameUser");
		}

		// other user than before
		else {

			// if no valid login
			if (!permitUser(u, p)) {

				// causes JS to display error message
				writer.append("nosuccess");
				return;
			}

			// set user attribute for the HttpSession
			sess.setAttribute("user", u);

			// set a cookie for JS so login is not displayed when
			// refreshing due to sending values etc
			// cookie expires after 60 sec of inactivity
			Cookie c = new Cookie(u, "loggedin");
			c.setMaxAge(30 * 60);
			res.addCookie(c);

			// causes JS to start new session and d3web case finally
			writer.append("newUser");
			// System.out.println("new");
		}
	}

	/**
	 * Check, whether the user has permissions to log in. Permissions are stored
	 * in userdat.csv in cases parent folder
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