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
package de.knowwe.kdom.subtreehandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frames.objectproperty.Characteristics;
import de.knowwe.kdom.manchester.frames.objectproperty.ObjectPropertyFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

/**
 * <p>
 * Walks over the KDOM and looks for {@link ObjectPropertyExpression} and
 * creates correct OWL axioms out of the found KDOM nodes. Those nodes are then
 * added to the ontology.
 * </p>
 * <p>
 * Currently handles: {@link ObjectPropertyDefinition}, {@link Characteristics},
 * {@link Annotations}, {@link InverseOf}, {@link SubPropertyOf}, {@link Domain}
 * and {@link Range}
 * </p>
 *
 * @author Stefan Mark
 * @created 07.09.2011
 */
public class ObjectPropertySubtreeHandler extends OWLAPISubtreeHandler<ObjectPropertyFrame> {

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<ObjectPropertyFrame> s, Collection<KDOMReportMessage> messages) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLObjectProperty p = null;
		OWLAxiom e = null;

		// handle definition, then the rest
		ObjectPropertyFrame type = s.get();
		if (type.hasObjectPropertyDefinition(s)) {
			Section<?> def = type.getObjectPropertyDefinition(s);
			p = (OWLObjectProperty) AxiomFactory.getOWLAPIEntity(def, OWLObjectProperty.class);
			e = AxiomFactory.getOWLAPIEntityDeclaration(p);
			if (e != null) {
				axioms.add(e);
			}
		}

		if (type.hasRange(s)) { // Handle Range
			Section<?> desc = type.getRange(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			// handle children such as OWLTerReferences, Lists, Conjuncts, etc.
			// Set<OWLClassExpression> expressions =
			// AxiomFactory.createDescriptionExpression(mce);
		}

		// done with the definition handle the rest
		for (Section<?> child : s.getChildren()) {
			Type t = child.get();

			if (t instanceof Characteristics) {
				List<Section<Characteristics.CharacteristicsTerm>> l = Sections.findSuccessorsOfType(child,
						Characteristics.CharacteristicsTerm.class);
				if (l != null) {
					for (Section<Characteristics.CharacteristicsTerm> r : l) {
						e = AxiomFactory.createCharacteristics(r, p);
						if (e != null) {
							axioms.add(e);
						}
					}
				} // term missing, please remove definition or one
			}
			else if (t instanceof Annotations) {
				List<Section<Annotation>> items = Sections.findSuccessorsOfType(child,
						Annotation.class);
				for (Section<Annotation> item : items) {
					e = AxiomFactory.createAnnotations(item, p.getIRI());
					if (e != null) {
						axioms.add(e);
					}
				}
			}
		}
		return axioms;
	}

}
