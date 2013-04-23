/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.revisions.manager;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.revisions.DatedRevision;
import de.knowwe.revisions.UploadedRevision;

/**
 * This class caches all already used revisions. If a new Date is asked, it
 * automatically creates a revision instance. For every Revision (thus every
 * date) e.g. a ArticleManager can be created and returned.
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class RevisionManager {

	// TODO: get rid of revisions no longer needed

	private static HashMap<HttpSession, RevisionManager> instances = new HashMap<HttpSession, RevisionManager>();

	private HashMap<Date, DatedRevision> revisions;
	private UploadedRevision uploadedRevision;
	private String web;

	/**
	 * @param articleManagers
	 */
	public RevisionManager(String web) {
		revisions = new HashMap<Date, DatedRevision>();
		this.web = web;
	}

	/**
	 * Get the RevisionManager for the specified HttpSession and Web
	 * 
	 * @created 22.04.2013
	 * @param context
	 * @return
	 */
	public static RevisionManager getRM(HttpSession session, String web) {
		if (!instances.containsKey(session)) {
			instances.put(session, new RevisionManager(web));
		}
		return instances.get(session);
	}

	/**
	 * Get the RevisionManager for the Contexts HttpSession and Web
	 * 
	 * @created 23.04.2013
	 * @param context
	 * @return
	 */
	public static RevisionManager getRM(UserActionContext context) {
		return getRM(context.getSession(), context.getWeb());
	}

	public static void removeRM(HttpSession session) {
		instances.remove(session);
	}

	public DatedRevision getRevision(Date date) {
		createRevisionIfNotExists(date);
		return revisions.get(date);
	}

	public ArticleManager getArticleManager(Date date) {
		createRevisionIfNotExists(date);
		return getRevision(date).getArticleManager();
	}

	/**
	 * 
	 * @created 22.04.2013
	 * @param date
	 */
	private void createRevisionIfNotExists(Date date) {
		if (!revisions.containsKey(date)) {
			revisions.put(date, new DatedRevision(date, web));
		}
	}

	public HashMap<String, Integer> getArticleVersions(Date date) {
		createRevisionIfNotExists(date);
		return getRevision(date).getArticleVersions();
	}

	// public HashMap<String, int[]> getRevisionDiff(Date date1, Date date2) {
	// HashMap<String, int[]> result = new HashMap<String, int[]>();
	// HashMap<String, Integer> rev1 = getArticleVersions(date1);
	// HashMap<String, Integer> rev2 = getArticleVersions(date2);
	//
	// for (String title : rev2.keySet()) {
	// int[] versions = new int[2];
	// versions[1] = rev2.get(title);
	// if (rev1.containsKey(title)) {
	// versions[0] = rev1.get(title);
	// }
	// else {
	// versions[0] = -2;
	// }
	// result.put(title, versions);
	// }
	// return result;
	// }

	public HashMap<String, Integer> compareWithCurrentState(Date date) {
		createRevisionIfNotExists(date);
		return getRevision(date).compareWithCurrentState();
	}

	public void setUploadedRevision(UploadedRevision r) {
		uploadedRevision = r;
	}

	public UploadedRevision getUploadedRevision() {
		return uploadedRevision;
	}
}