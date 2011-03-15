package de.d3web.proket.run;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {

		res.setContentType("text/html");

		// fetch the information sent via the request string
		String u = req.getParameter("usr");
		String p = req.getParameter("pw");

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
			writer.append("success");
			// Valid login. Make a note in the session object.
			/*
			 * HttpSession session = req.getSession();
			 * session.setAttribute("logon.isDone", usr + "_" + pw);
			 * 
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

		System.out.println("user: " + user);
		System.out.println("pw: " + password);

		// Lookup user and pw here:


		return false; // trust everyone
	}
}