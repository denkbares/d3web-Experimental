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

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.MiscFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

/**
 * <p>
 * Walk over the KDOM and looks for {@link MiscFrame} and creates correct OWL
 * axioms out of the found KDOM nodes. Those nodes are then added to the
 * ontology.
 * </p>
 * <p>
 * Currently handles: DisjointClasses, EquivalentClasses, SameIndividual,
 * DifferentIndividuals, EquivalentProperties, DisjointProperties. Also
 * {@link Annotations} are possible in each of the mentioned frames.
 * </p>
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
public class MiscFrameSubtreeHandler extends OWLAPISubtreeHandler<MiscFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync
	 * with RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public MiscFrameSubtreeHandler() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<MiscFrame> s, Collection<KDOMReportMessage> messages) {

		OWLAxiom a = null;
		MiscFrame type = (MiscFrame) s.get();

		Set<OWLAnnotation> annotations = handleOptionalAnnotations(s);

		if (type.isDifferentIndividuals(s) || type.isSameIndividuals(s)) {
			a = AxiomFactory.createMiscFrameIndividuals(s, annotations);
		}
		else if (type.isDisjointClasses(s) || type.isEquivalentClasses(s)) {
			a = AxiomFactory.createMiscFrameClasses(s, annotations);
		}
		else if (type.isDisjointProperties(s) || type.isEquivalentProperties(s)) {
			a = AxiomFactory.createMiscFrameObjectProperties(s, annotations);
		}
		else if (type.isDisjointDataProperties(s) || type.isEquivalentDataProperties(s)) {
			a = AxiomFactory.createMiscFrameDataProperties(s, annotations);
		}

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		if (a != null) {
			axioms.add(a);
		}
		return axioms;
	}

	/**
	 * Handles the optional {@link Annotations} inside each description.
	 *
	 * @created 29.09.2011
	 * @param Section<? extends Type> section
	 * @return A Set with {@link OWLAnnotationAxiom}
	 */
	private Set<OWLAnnotation> handleOptionalAnnotations(Section<? extends Type> section) {

		Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();

		if (ManchesterSyntaxUtil.hasAnnotations(section)) {
			List<Section<Annotation>> annotationSections = ManchesterSyntaxUtil.getAnnotations(section);
			for (Section<Annotation> annotation : annotationSections) {
				OWLAnnotation a = AxiomFactory.createOWLAnnotation(annotation);
				if (a != null) {
					annotations.add(a);
				}
			}
		}
		return annotations;
	}
}
