/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.action;

import java.util.List;
import java.util.ResourceBundle;

import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserSettingsManager;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.d3web.we.wikiConnector.KnowWEWikiConnector;

/**
 * 
 */
public class SetQuickEditFlagAction extends DeprecatedAbstractKnowWEAction {

	private ResourceBundle rb;

	@Override
	public String perform(KnowWEParameterMap parameterMap) {

		rb = KnowWEEnvironment.getInstance().getKwikiBundle(parameterMap.getRequest());

		String web = parameterMap.getWeb();
		String nodeID = parameterMap.get(KnowWEAttributes.TARGET);

		String topic = parameterMap.getTopic();
		String user = parameterMap.getUser();
		String inPre = parameterMap.get("inPre");

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();

		if (connector.userCanEditPage(topic, parameterMap.getRequest())) {
			boolean existsPageLock = connector.isPageLocked(topic);
			boolean lockedByCurrentUser = connector.isPageLockedCurrentUser(
					topic, user);

			if (existsPageLock) {
				if (lockedByCurrentUser) {
					connector.undoPageLocked(topic);
				}
				else {
					return getMessage(topic);
				}
			}
			else {
				connector.setPageLocked(topic, user);
			}
			UserSettingsManager.getInstance().setQuickEditFlag(nodeID, user, topic, inPre);
			String result = this.rerenderKDOMElement(web, topic, parameterMap.getWikiContext(),
					nodeID);

			// Pushing the result through the JSPWiki rendering pipeline when QE
			// is being closed and QE isn't within a pre-environment
			if (!UserSettingsManager.getInstance().hasQuickEditFlagSet(nodeID, user, topic)
					&& !UserSettingsManager.getInstance().quickEditIsInPre(nodeID, user, topic)) {
				result = KnowWEUtils.maskHTML(result);
				result = KnowWEEnvironment.getInstance().getWikiConnector().renderWikiSyntax(
						result, parameterMap);
			}
			return "@replace@" + KnowWEUtils.unmaskHTML(result);
		}
		else {
			return getMessage(topic);
		}
	}

	/**
	 * Searches the element the user set the QuickEditFlag to and renders it.
	 * The result is returned for refreshing the view.
	 * 
	 * @param web
	 * @param topic
	 * @param user
	 * @param nodeID
	 * @return
	 */
	private String rerenderKDOMElement(String web, String topic, KnowWEUserContext user, String nodeID) {
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		KnowWEArticle article = mgr.getArticle(topic);

		if (article == null) return "<p class=\"error box\"> "
				+ rb.getString("KnowWE.qedit.noarticle") + " </p>";

		Section root = article.getSection();
		Section secWithNodeID = getSectionFromCurrentID(nodeID, root);

		if (secWithNodeID != null) {
			KnowWEDomRenderer renderer = secWithNodeID.getObjectType().getRenderer();
			StringBuilder b = new StringBuilder();
			if (renderer != null) {
				renderer.render(article, secWithNodeID, user, b);
			}
			else {

				DelegateRenderer.getInstance().render(article, secWithNodeID, user, b);
			}
			String result = b.toString();
			return KnowWEUtils.unmaskHTML((result));
		}
		return "<p class=\"error box\"> " + rb.getString("KnowWE.qedit.nokdomidfound") + "</p>";
	}

	/**
	 * Searches for a section with the node id from the
	 * <code>SetQuickEditFlagAction</code>. The resulting section will be
	 * re-rendered and updated in the view.
	 * 
	 * @param nodeID
	 * @param root
	 * @param found
	 */
	private Section getSectionFromCurrentID(String nodeID, Section root) {
		if (root.getID().equals(nodeID)) return root;

		Section found = null;
		List<Section> children = root.getChildren();
		for (Section section : children) {
			found = getSectionFromCurrentID(nodeID, section);
			if (found != null) return found;
		}
		return found;
	}

	/**
	 * Returns a short info message which informs the user, that he can not edit
	 * the page and therefore is not allowed to use the quick-edit mode.
	 * 
	 * @param articlename
	 * @return
	 */
	private String getMessage(String articlename) {
		String error = rb.getString("KnowWE.qedit.editingdenied");
		return "@info@" + error;
	}
}
