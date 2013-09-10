/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.baseline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.wikiConnector.WikiConnector;


/**
 * 
 * @author Reinhard Hatko
 * @created 26.10.2012
 */
public class CreateBaselineAction extends AbstractAction {

	public static final String BASELINE_SUFFIX = ".baseline.xml";
	public static final String NAME_CURRENT = "Current";
	private static final String NAME = "baselineName";

	@Override
	public void execute(UserActionContext context) throws IOException {

		WikiConnector connector = Environment.getInstance().getWikiConnector();

		String name = context.getParameter(NAME);
		String username = context.getUserName();
		Baseline baseline = createNewBaseline(name, username, context.getWeb());

		InputStream stream = new ByteArrayInputStream(Baseline.toXML(baseline).getBytes("UTF-8"));
		connector.storeAttachment(context.getTitle(), baseline.getName() + BASELINE_SUFFIX,
				context.getUserName(), stream);

	}

	public static Baseline createCurrentBaseline(UserContext context) {
		return createNewBaseline(NAME_CURRENT, context.getUserName(), context.getWeb());
	}

	/**
	 * 
	 * @created 27.10.2012
	 * @param name2
	 * @param username
	 * @param web
	 * @return
	 */
	public static Baseline createNewBaseline(String name, String username, String web) {

		Baseline baseline = new Baseline(name, System.currentTimeMillis(), username);
		WikiConnector connector = Environment.getInstance().getWikiConnector();
		Map<String, String> allArticles = connector.getAllArticles(web);
		for (String title : allArticles.keySet()) {
			baseline.addArticle(title, connector.getVersion(title));
		}
		return baseline;
	}

}
