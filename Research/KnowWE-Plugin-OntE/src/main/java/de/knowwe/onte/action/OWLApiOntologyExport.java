/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.onte.action;

import java.io.IOException;
import java.util.Calendar;

import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;
import de.knowwe.util.OntologyFormats;

/**
 * Serializes the local OWLOntology in the given format. Possible formats are
 * RDF/XML, OWL/XML, Turtle and Manchester OWL syntax.
 *
 * @author Stefan Mark
 * @created 16.10.2011
 */
public class OWLApiOntologyExport extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String format = context.getParameter("format");
		String filename = context.getParameter("filename");
		String serialized = "";

		if (format != null) {
			format = format.toLowerCase().trim();
			try {
				serialized = OWLApiTagHandlerUtil.getSerializedOntology(format);
			}
			catch (OWLOntologyStorageException e) {
				serialized = "error:";
			}
		}
		if (filename == null) {
			filename = String.valueOf(Calendar.getInstance().getTimeInMillis());
		}

		String mimetype = null;
		if (format.equals(OntologyFormats.RDFXML.getFormat())
				|| format.equals(OntologyFormats.OWLXML.getFormat())) {
			mimetype = "application/rdf+xml";
		}
		else if (format.equals(OntologyFormats.TURTLE.getFormat())) {
			mimetype = "text/turtle";
		}
		else if (format.equals(OntologyFormats.MANCHESTER.getFormat())) {
			mimetype = "text/owl-manchester";
		}

		context.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		context.setContentLength(serialized.length());
		context.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".owl\"");
		context.getWriter().write(serialized);
	}
}
