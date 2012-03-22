/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.usersupport.tables;

import de.d3web.we.kdom.rules.action.SetQuestionValue;
import de.d3web.we.object.ScoreValue;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * Normal cell of an {@link InnerTable}. Can
 * contain {@link SetQuestionValue} and {@link ScoreValue}
 * 
 * @author Johannes Dienst
 * @created 28.11.2011
 */
public class TableNormalCell extends TableCell
{

	public static final Renderer INDIVIDUAL_RENDERER = StyleRenderer.CONTENT;

	public TableNormalCell()
	{
		super();
		this.addChildType(new SetQuestionValue());
		this.addChildType(new ScoreValue());
	}
}