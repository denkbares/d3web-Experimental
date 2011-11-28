package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.SyntaxError;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.IndividualFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIKnowledgeUnitCompileScript;
import de.knowwe.owlapi.OWLAPISubtreeHandler;


public class IndividualFrameCompileScript extends OWLAPIKnowledgeUnitCompileScript<IndividualFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync with
	 * RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public IndividualFrameCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<IndividualFrame> section, Collection<KDOMReportMessage> messages) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLIndividual i = null;
		OWLAxiom axiom = null;

		// handle definition, then the rest
		IndividualFrame type = section.get();
		if (type.hasIndividualDefinition(section)) {
			Section<?> def = type.getIndividualDefinition(section);
			i = (OWLIndividual) AxiomFactory.getOWLAPIEntity(def, OWLIndividual.class);
		}

		if (type.hasAnnotations(section)) { // Handle Annotations
			for (Section<Annotation> annotation : type.getAnnotations(section)) {
				IRI annotatetObject = i.asOWLNamedIndividual().getIRI();
				axiom = AxiomFactory.createAnnotations(annotation, annotatetObject, messages);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, annotation));
					axioms.add(axiom);
				}
			}
		}

		if (type.hasFacts(section)) { // Handle Facts
			List<Section<?>> facts = type.getFacts(section);

			if (facts.isEmpty()) {
				messages.add(new SyntaxError("Facts keyword specified, but no facts found!"));
			}

			for (Section<?> fact : facts) {
				axiom = AxiomFactory.createFact(fact, i, messages);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, fact));
					axioms.add(axiom);
				}
				// handleOptionalAnnotations(fact, i); // Optional annotations
			}
		}

		if (type.hasSameAs(section)) { // Handle SameAs
			List<Section<OWLTermReferenceManchester>> nodes = type.getSameAs(section);

			if (nodes.isEmpty()) {
				messages.add(new SyntaxError("SameAs found, but no individuals specified!"));
			}

			for (Section<OWLTermReferenceManchester> node : nodes) {
				OWLIndividual sameInd = (OWLIndividual) AxiomFactory.getOWLAPIEntity(node,
						OWLIndividual.class);
				axiom = AxiomFactory.createSameIndividualsAxiom(i, sameInd);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, node));
					axioms.add(axiom);
				}
				// handleOptionalAnnotations(node, i); // Optional annotations
			}
		}

		if (type.hasDifferentFrom(section)) { // Handle DifferentFrom
			List<Section<OWLTermReferenceManchester>> nodes = type.getDifferentFrom(section);

			if (nodes.isEmpty()) {
				messages.add(new SyntaxError("DifferentFrom found, but no individuals specified!"));
			}

			for (Section<OWLTermReferenceManchester> node : nodes) {
				OWLIndividual two = (OWLIndividual) AxiomFactory.getOWLAPIEntity(node,
						OWLIndividual.class);
				axiom = AxiomFactory.createDifferentFromIndividualsAxiom(i, two);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, node));
					axioms.add(axiom);
				}
				// handleOptionalAnnotations(node, i); // Optional annotations
			}
		}

		if (type.hasTypes(section)) { // Handle Types
			Section<?> types = type.getTypes(section);

			Section<ManchesterClassExpression> mce = Sections.findChildOfType(types,
					ManchesterClassExpression.class);

			if (mce.isEmpty()) {
				messages.add(new SyntaxError("Types found, but no concepts specified!"));
			}

			Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
					mce, messages);

			for (OWLClassExpression e : exp.keySet()) {
				axiom = AxiomFactory.createNamedIndividualAxiom(e, i);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
					axioms.add(axiom);
				}
			}
			// FIXME handleOptionalAnnotations(types, i); optional Annotations
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
	private Set<OWLAxiom> handleOptionalAnnotations(Section<? extends Type> section, OWLIndividual i, Collection<KDOMReportMessage> messages) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		if (ManchesterSyntaxUtil.hasAnnotations(section)) {
			List<Section<Annotation>> annotations = ManchesterSyntaxUtil.getAnnotations(section);
			IRI annotatetObject = i.asOWLNamedIndividual().getIRI();
			for (Section<Annotation> annotation : annotations) {
				OWLAxiom a = AxiomFactory.createAnnotations(annotation, annotatetObject, messages);
				if (a != null) {
					axioms.add(a);
				}
			}
		}
		return axioms;
	}

}
