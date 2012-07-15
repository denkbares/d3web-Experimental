/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.tripleMarkup;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.literal.TurtleObjectLiteralText;
import de.knowwe.rdfs.util.RDFSUtil;

class TripleCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<TripleMarkup> {

	@Override
	public void insertIntoRepository(Section<TripleMarkup> section) {

		List<Section<IRITermRef>> found = new ArrayList<Section<IRITermRef>>();
		Node subURI = null;
		Node predURI = null;
		Node objURI = null;

		Section<SimpleTurtleObjectSection> objectSec = Sections.findSuccessor(
				section, SimpleTurtleObjectSection.class);
		Section<TurtleObjectLiteralText> literalSec = Sections.findSuccessor(
				objectSec, TurtleObjectLiteralText.class);

		Sections.findSuccessorsOfType(section, IRITermRef.class, found);

		if (found.size() >= 2) {
			Section<IRITermRef> subject = found.get(0);
			Section<IRITermRef> predicate = found.get(1);
			subURI = RDFSUtil.getURI(subject);
			predURI = RDFSUtil.getURI(predicate);

			if (found.size() == 3) {

				Section<IRITermRef> object = found.get(2);
				objURI = RDFSUtil.getURI(object);
			}
			else if (literalSec != null) {
				objURI = Rdf2GoCore.getInstance().createLiteral(
						literalSec.getText());
			}

		}

		if (subURI != null && predURI != null && objURI != null) {

			Rdf2GoCore.getInstance().addStatement(section,
					subURI.asResource(), predURI.asURI(), objURI);
		}

	}

}