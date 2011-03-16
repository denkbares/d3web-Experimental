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
package de.knowwe.util;

import java.util.ArrayList;
import java.util.Collection;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;

public class DelegateDestroyHandler extends GeneralSubtreeHandler<Type> {

	private final SubtreeHandler<Type> handler;

	public DelegateDestroyHandler(SubtreeHandler h) {
		this.handler = h;
	}

	@Override
	public void destroy(KnowWEArticle article, Section<Type> s) {
		handler.destroy(article, s);
	}

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Type> s) {
		return new ArrayList<KDOMReportMessage>(0);
	}

}