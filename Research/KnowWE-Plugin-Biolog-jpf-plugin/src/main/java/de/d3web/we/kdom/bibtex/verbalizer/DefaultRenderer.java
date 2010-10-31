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
package de.d3web.we.kdom.bibtex.verbalizer;

import java.util.Map;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat;

/**
 * @author Fabian Haupt
 *
 */
public class DefaultRenderer implements BibTexRenderer {

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#getSupportedClassesForVerbalization()
	 */
	@Override
	public Class[] getSupportedClassesForVerbalization() {
		Class[] supportedClasses = { Object.class };
		return supportedClasses;
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#getSupportedRenderingTargets()
	 */
	@Override
	public RenderingFormat[] getSupportedRenderingTargets() {
		return RenderingFormat.values();
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#render(java.lang.Object, de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat, java.util.Map)
	 */
	@Override
	public Object render(Object o, RenderingFormat targetFormat,
			Map<String, Object> parameter) {
		if (targetFormat == RenderingFormat.HTML)
			return renderObjectToHTML(o);
		if (targetFormat == RenderingFormat.PLAIN_TEXT)
			return renderObjectToPlainText(o);
		if (targetFormat == RenderingFormat.SWRCOWL)
			return renderObjectToSWRCOwl(o);

		// as this is the defaultVerbalizer (that should render everything) this
		// shall never happen!
		return null;
	}
	
	
	/**
	 * returns o.toString()
	 * 
	 * @param o
	 *            object to be rendered as plain text
	 * @return o.toString()
	 */
	protected String renderObjectToPlainText(Object o) {
		return o.toString();
	}


	/**
	 * return o.toString()
	 * 
	 * @param o
	 *            object to be rendered as html
	 * @return o.toString()
	 */
	protected String renderObjectToHTML(Object o) {
		return o.toString();
	}

	protected IntermediateOwlObject renderObjectToSWRCOwl(Object o){
		return new IntermediateOwlObject();
	}
	
}
