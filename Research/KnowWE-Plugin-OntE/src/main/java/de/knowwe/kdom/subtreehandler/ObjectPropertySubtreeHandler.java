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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
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
		OWLAxiom axiom = null;

		// handle definition, then the rest
		ObjectPropertyFrame type = s.get();
		if (type.hasObjectPropertyDefinition(s)) {
			Section<?> def = type.getObjectPropertyDefinition(s);
			p = (OWLObjectProperty) AxiomFactory.getOWLAPIEntity(def, OWLObjectProperty.class);
			axiom = AxiomFactory.getOWLAPIEntityDeclaration(p);
			if (axiom != null) {
				axioms.add(axiom);
			}
		}

		if (type.hasRange(s)) { // Handle Range
			Section<?> desc = type.getRange(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);

			for (OWLClassExpression e : exp) {
				axiom = AxiomFactory.createRange(p, e);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if (type.hasDomain(s)) { // Handle Domain
			Section<?> desc = type.getDomain(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);

			for (OWLClassExpression e : exp) {
				axiom = AxiomFactory.createDomain(p, e);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}
		if (type.hasInverseOf(s)) { // Handle InverseOf
			Section<?> desc = type.getInverseOf(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLObjectProperty> props = AxiomFactory.createObjectPropertyExpression(mce);

			for (OWLObjectProperty e : props) {
				axiom = AxiomFactory.createInverseOf(p, e);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}
		if (type.hasSubPropertyOf(s)) { // Handle SubPropertyOf
			Section<?> desc = type.getSubPropertyOf(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLObjectProperty> props = AxiomFactory.createObjectPropertyExpression(mce);

			for (OWLObjectProperty e : props) {
				axiom = AxiomFactory.createSubPropertyOf(p, e);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}
		// FIXME not possible with the OWLApi ???
		if (type.hasEquivalentTo(s)) { // Handle EquivalentTo

		}

		if (type.hasCharacteristics(s)) { // Handle Characteristics
			Section<Characteristics> c = type.getCharacteristics(s);
			List<Section<?>> terms = c.get().getCharacteristics(c);
			for (Section<?> term : terms) {
				axiom = AxiomFactory.createCharacteristics(term, p);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if(ManchesterSyntaxUtil.hasAnnotations(s)) { //Handle Annotations
			List<Section<Annotation>> annotations = ManchesterSyntaxUtil.getAnnotations(s);
			for (Section<Annotation> annotation : annotations) {
				axiom = AxiomFactory.createAnnotations(annotation, p.getIRI());
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}
		return axioms;
	}
}
