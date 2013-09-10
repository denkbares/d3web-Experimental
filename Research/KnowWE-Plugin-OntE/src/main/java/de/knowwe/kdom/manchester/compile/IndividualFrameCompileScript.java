package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frame.IndividualFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIKnowledgeUnitCompileScript;

public class IndividualFrameCompileScript extends OWLAPIKnowledgeUnitCompileScript<IndividualFrame> {

	public IndividualFrameCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<IndividualFrame> section, Collection<Message> messages) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLNamedIndividual i = null;

		// handle definition, then the rest
		IndividualFrame type = section.get();
		if (type.hasIndividualDefinition(section)) {
			Section<?> def = type.getIndividualDefinition(section);

			i = (OWLNamedIndividual) AxiomFactory.getOWLAPIEntity(def, OWLNamedIndividual.class);
			OWLAxiom decl = AxiomFactory.getOWLAPIEntityDeclaration(i);
			axioms.add(decl);
		}

		if (type.hasAnnotations(section)) { // Handle Annotations
			for (Section<Annotation> annotation : type.getAnnotations(section)) {
				IRI annotatetObject = i.asOWLNamedIndividual().getIRI();
				OWLAxiom axiom = AxiomFactory.createAnnotations(annotation, annotatetObject,
						messages);
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
				messages.add(Messages.syntaxError("Facts keyword specified, but no facts found!"));
			}

			for (Section<?> fact : facts) {
				OWLAxiom axiom = AxiomFactory.createFact(fact, i, messages);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, fact));
					axioms.add(axiom);
				}
			}
		}

		if (type.hasSameAs(section)) { // Handle SameAs
			List<Section<OWLTermReferenceManchester>> nodes = type.getSameAs(section);

			if (nodes.isEmpty()) {
				messages.add(Messages.syntaxError("SameAs found, but no individuals specified!"));
			}

			for (Section<OWLTermReferenceManchester> node : nodes) {
				OWLIndividual sameInd = (OWLIndividual) AxiomFactory.getOWLAPIEntity(node,
						OWLIndividual.class);
				OWLAxiom axiom = AxiomFactory.createSameIndividualsAxiom(i, sameInd);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, node));
					axioms.add(axiom);
				}
			}
		}

		if (type.hasDifferentFrom(section)) { // Handle DifferentFrom
			List<Section<OWLTermReferenceManchester>> nodes = type.getDifferentFrom(section);

			if (nodes.isEmpty()) {
				messages.add(Messages.syntaxError("DifferentFrom found, but no individuals specified!"));
			}

			for (Section<OWLTermReferenceManchester> node : nodes) {
				OWLIndividual two = (OWLIndividual) AxiomFactory.getOWLAPIEntity(node,
						OWLIndividual.class);
				OWLAxiom axiom = AxiomFactory.createDifferentFromIndividualsAxiom(i, two);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, node));
					axioms.add(axiom);
				}
			}
		}

		if (type.hasTypes(section)) { // Handle Types
			Section<?> types = type.getTypes(section);

			Section<ManchesterClassExpression> mce = Sections.findChildOfType(types,
					ManchesterClassExpression.class);

			if (mce.isEmpty()) {
				messages.add(Messages.syntaxError("Types found, but no concepts specified!"));
			}

			Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
					mce, messages);

			for (OWLClassExpression e : exp.keySet()) {
				OWLAxiom axiom = AxiomFactory.createNamedIndividualAxiom(e, i);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
					axioms.add(axiom);
				}
			}
		}
		return axioms;
	}

}
