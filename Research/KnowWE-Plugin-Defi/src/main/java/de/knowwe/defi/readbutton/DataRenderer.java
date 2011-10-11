/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.readbutton;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


/**
 * 
 * @author dupke
 * @created 23.03.2011
 */
public class DataRenderer<T extends AbstractType> extends KnowWEDomRenderer<T> {
	
	@Override
	public void render(KnowWEArticle article, Section<T> sec, UserContext user, StringBuilder string) {

		String readpages = DefaultMarkupType.getAnnotation(sec, "readpages");
		String username = user.getUserName();
		String pageName = article.getTitle();

		if (pageName.toLowerCase().equals(username.toLowerCase() + "_data")
				&& user.userIsAsserted()) {
			string.append(KnowWEUtils.maskHTML("<p>Hier werden alle als bewerteten Lektionen aufgelistet</p>"));
			
			if(readpages == null) {
				string.append(KnowWEUtils.maskHTML("<p>Noch keine Lektionen bewertet.</p>"));
			} else {
				string.append(KnowWEUtils.maskHTML("<ul>"));
				for (String s : readpages.split(";")) {
					string.append(KnowWEUtils.maskHTML("<li>" + s.split("::")[0] + ": Button("
							+ s.split("::")[1] +
							") hat den " + s.split("::")[2] + ". Wert gespeichert</li>"));
				}
				string.append(KnowWEUtils.maskHTML("</ul>"));
			}
		}
	}

}
