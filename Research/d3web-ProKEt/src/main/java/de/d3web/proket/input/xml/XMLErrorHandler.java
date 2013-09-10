/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.input.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML error handler.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class XMLErrorHandler implements ErrorHandler {
	@Override
	public void error(SAXParseException exception) throws SAXException {
		throw new SAXException(String.format("%s (Line: %s)",
				exception.getMessage(), exception.getLineNumber()));
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		throw new SAXException(String.format("%s (Line: %s)",
				exception.getMessage(), exception.getLineNumber()));
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		throw new SAXException(String.format("%s (Line: %s)",
				exception.getMessage(), exception.getLineNumber()));
	}
}