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

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.output.render.MediastinitisDefaultRootD3webRenderer;
import de.d3web.proket.output.container.ContainerCollection;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

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
public class MediastinitisDialog extends D3webDialog {

	private static final long serialVersionUID = 4798072917307992413L;

        @Override
        public void init(ServletConfig config) throws ServletException{
            super.init(config);
            
            if (usrDat == null) {
            
            // get parent folder for storing cases
            usrDat = new HashMap<String, List<String>>();

            String csvFile = GLOBSET.getServletBasePath()
                    + "/users/usrdat.csv";
            CSVReader csvr = null;
            String[] nextLine = null;

            try {
                csvr = new CSVReader(new FileReader(csvFile));
                // go through file
                while ((nextLine = csvr.readNext()) != null) {
                    // skip first line
                    if (!nextLine[0].startsWith("usr")) {
                        // if username and pw could be found, return true
                        List<String> values = new ArrayList<String>();
                        for (String word : nextLine) {
                            values.add(word);
                        }
                        usrDat.put(nextLine[0], values);
                    }
                }
                System.out.println(usrDat);
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        }
        
	@Override
	protected String getSource(HttpServletRequest request) {
		return "Mediastinitis";
	}

	/**
	 * Basic servlet method for displaying the dialog.
	 * 
	 * @created 28.01.2011
	 * @param request
	 * @param response
	 * @param d3webSession
	 * @throws IOException
	 */
	@Override
	public void show(HttpServletRequest request, HttpServletResponse response,
			HttpSession httpSession)
			throws IOException {

		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		MediastinitisDefaultRootD3webRenderer d3webr =
				(MediastinitisDefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(
						null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);

		// set clinic to user name, since its the only clinic it can see cases
		// of
		String user = (String) httpSession.getAttribute("user");
                
		if (user != null && user != "") {
			String definingObject = D3webConnector.getInstance().getD3webParser().getRequired();
                        
			Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(
					definingObject);
			Fact fact = d3webSess.getBlackboard().getValueFact(to);
			if (to != null && (fact == null || !fact.getValue().getValue().toString().equals(user))) {
				D3webUtils.setValue(definingObject, user, d3webSess);
			}
		}

		cc = d3webr.renderRoot(cc, d3webSess, httpSession);

		writer.print(cc.html.toString()); // deliver the rendered output

		writer.close(); // and close
	}
}