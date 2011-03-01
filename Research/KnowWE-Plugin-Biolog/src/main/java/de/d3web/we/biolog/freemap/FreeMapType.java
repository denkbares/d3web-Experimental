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

package de.d3web.we.biolog.freemap;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.xml.AbstractXMLType;
import de.d3web.we.kdom.xml.XMLContent;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * 
 * This type recognizes FreeMap-xml-files when being pastet into the wiki pages.
 * The xml-structure starts with a <map>-tag at root level.
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class FreeMapType extends AbstractXMLType{
	
	public FreeMapType() {
		super("map");
	}
	
	@Override
	protected void init() {
		this.childrenTypes.add(new MapContent());
		this.setCustomRenderer(new FreeMapTypeRenderer());
	}
	
	class MapContent extends XMLContent {

		@Override
		protected void init() {
			this.childrenTypes.add(FreeMapNode.getInstance());
			
		}
		
	}
	
	class FreeMapTypeRenderer extends KnowWEDomRenderer {

		@Override
		public void render(KnowWEArticle article, Section sec,
				KnowWEUserContext user, StringBuilder string) {
			string.append("{{{");
			DelegateRenderer.getInstance().render(article, sec, user, string);
			string.append("}}}");
			
		}
		
		
	}

}
