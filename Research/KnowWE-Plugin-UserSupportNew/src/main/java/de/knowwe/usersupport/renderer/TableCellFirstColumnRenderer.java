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
package de.knowwe.usersupport.renderer;

import java.util.Collection;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;


/**
 * 
 * Colors {@link TableCellFirstColumns} according to the {@link Message}s.
 * 
 * @author Johannes Dienst
 * @created 17.02.2012
 */
public class TableCellFirstColumnRenderer implements Renderer
{

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string)
	{
		Article article = KnowWEUtils.getCompilingArticles(sec).iterator().next();

		Collection<Message> messages = Messages.getMessagesFromSubtree(article, sec);
		StyleRenderer styler = new StyleRenderer("color:rgb(152, 180, 12)");
		if (!Messages.getErrors(messages).isEmpty())
		{
			styler = StyleRenderer.getRenderer("color:rgb(152, 180, 12)", "#FAAC58");
		}
		else if (!Messages.getWarnings(messages).isEmpty())
		{
			styler = StyleRenderer.getRenderer("color:rgb(152, 180, 12)", "#F5A9BC");
		}
		//		else if (!Messages.getNotices(messages).isEmpty())
		//		{
		//			return PoiUtils.getNoticeCellStyle(wb);
		//		}
		styler.render(sec, user, string);
	}

}
