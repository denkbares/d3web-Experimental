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

import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat;


/**
 * The is the common interface of all BibTexRenderers.
 * 
 * 
 * @author fhaupt
 *
 */
public interface BibTexRenderer {

	//some parameter, that can be used in the parameter hash
	//we save them here to get a common interface
	public static final String INDENT = "indent";
	public static final String IS_SINGLE_LINE = "isSingleLine";
	public static final String IS_NEGATIVE = "isNegative";
	public static final String CONTEXT = "context";
	public static final String ID_VISIBLE = "idVisible";
	public static final String LOCALE = "locale";
	public static final String USE_QUOTES = "useQuotes";

	/**
	 * Returns a verbalization (String representation) of the given object in
	 * the target format usind additional parameters.
	 * 
	 * 
	 * @param o
	 *            the Object to be verbalized
	 * @param targetFormat
	 *            The output format of the verbalization (HTML/XML/PlainText...)
	 * @param parameter
	 *            additional parameters used to adapt the verbalization (e.g.,
	 *            singleLine, etc...)
	 * @return A String representation of given object o in the target format
	 */
	public Object render(Object o, RenderingFormat targetFormat, Map<String, Object> parameter);

	/**
	 * Returns the classes a verbalizer can render
	 * @return a array of all classes a verbalizer can render
	 */
	public Class[] getSupportedClassesForVerbalization();

	
	/**
	 * Returns all target formats a verbalizer can use. 
	 * @return the target formats a verbalizer can use.
	 */
	public RenderingFormat[] getSupportedRenderingTargets();
}
