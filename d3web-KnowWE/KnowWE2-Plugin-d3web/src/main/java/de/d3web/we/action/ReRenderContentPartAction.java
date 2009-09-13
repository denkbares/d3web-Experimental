/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.we.action;

import java.util.List;

import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.javaEnv.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rules.Rule;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class ReRenderContentPartAction implements KnowWEAction  {

	@Override
	public String perform(KnowWEParameterMap map) {		
		return this.refreshKDOMElement(map.getWeb(), map.get("ArticleTopic"),
									   map.getWikiContext(), map.get("KdomNodeId"));
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
	private String refreshKDOMElement(String web, String topic, KnowWEUserContext user, String nodeID) {
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager( web );
		KnowWEArticle article = mgr.getArticle( topic );
		
		Section root = article.getSection();
		Section secWithNodeID = getSectionFromCurrentID( nodeID, root );
		
		// TODO secWithNodeID should be RuleCondLine
//		if (secWithNodeID.getObjectType() instanceof Rule) {
//			secWithNodeID = secWithNodeID.findChildOfType(RuleCondLine.class);
//			RuleConditionHighlightingRenderer renderer = RuleConditionHighlightingRenderer.getInstance();
//			renderer.setReRenderFlag(true);
//			return KnowWEEnvironment.unmaskHTML(renderer.
//					render(secWithNodeID, user, web, topic));
//		}
		

	
		if( secWithNodeID != null ) {
			
			StringBuilder b = new StringBuilder();
			DelegateRenderer.getInstance().render(secWithNodeID, user, b);
			
			if (secWithNodeID.getObjectType() instanceof Rule) {
				return "<span id=\"\">"
						+ KnowWEEnvironment.unmaskHTML(b.toString())
						+ "</span>";
			}

			String result = b.toString();
			result = KnowWEEnvironment.unmaskHTML(result);
			return KnowWEEnvironment.unmaskHTML(result);
		}
		return null;
	}
	
	/**
	 * Searches for a section with the node id from the <code>SetQuickEditFlagAction</code>.
	 * The resulting section will be re-rendered and updated in the view.
	 * 
	 * @param nodeID
	 * @param root
	 * @param found
	 */
	private Section getSectionFromCurrentID( String nodeID, Section root ) {		
	    if( root.getId().equals( nodeID ))
	    	return root;
	 
		Section found = null;
		List<Section> children = root.getChildren();
		for (Section section : children) {
			found = getSectionFromCurrentID( nodeID, section );
			if( found != null) return found;
		}
		return found;
	}	
}
