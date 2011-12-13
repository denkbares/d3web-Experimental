package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.ObjectPropertyFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Characteristics;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIAbstractKnowledgeUnitCompileScript;
import de.knowwe.owlapi.OWLAPISubtreeHandler;


public class ObjectPropertyCompileScript extends OWLAPIAbstractKnowledgeUnitCompileScript<ObjectPropertyFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync with
	 * RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public ObjectPropertyCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<ObjectPropertyFrame> section, Collection<Message> messages) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLObjectProperty p = null;
		OWLAxiom axiom = null;

		// handle definition, then the rest
		ObjectPropertyFrame type = section.get();
		if (type.hasObjectPropertyDefinition(section)) {
			Section<?> def = type.getObjectPropertyDefinition(section);
			p = (OWLObjectProperty) AxiomFactory.getOWLAPIEntity(def, OWLObjectProperty.class);
			axiom = AxiomFactory.getOWLAPIEntityDeclaration(p);
			if (axiom != null) {
				EventManager.getInstance().fireEvent(
						new OWLApiAxiomCacheUpdateEvent(axiom, section));
				axioms.add(axiom);
			}
		}

		if (type.hasRange(section)) { // Handle Range
			Section<?> desc = type.getRange(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("Range is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createRange(p, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasDomain(section)) { // Handle Domain
			Section<?> desc = type.getDomain(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("Domain is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createDomain(p, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}
		if (type.hasInverseOf(section)) { // Handle InverseOf
			Section<?> desc = type.getInverseOf(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("InverseOf is empty!"));
			}
			else {
				Set<OWLObjectProperty> props = AxiomFactory.createObjectPropertyExpression(mce);

				for (OWLObjectProperty px : props) {
					axiom = AxiomFactory.createInverseOf(p, px);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
					}
				}
			}
		}
		if (type.hasSubPropertyOf(section)) { // Handle SubPropertyOf
			Section<?> desc = type.getSubPropertyOf(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("SubPropertyOf is empty!"));
			}
			else {
				Set<OWLObjectProperty> props = AxiomFactory.createObjectPropertyExpression(mce);

				for (OWLObjectProperty px : props) {
					axiom = AxiomFactory.createSubPropertyOf(p, px);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
					}
				}
			}
		}
		// FIXME not possible with the OWLApi ???
		if (type.hasEquivalentTo(section)) { // Handle EquivalentTo

		}

		if (type.hasDisjointWith(section)) { // handle DisjointWith
			Section<?> desc = type.getDisjointWith(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("DisJointWith is empty!"));
			}
			else {
				Set<OWLObjectProperty> props = AxiomFactory.createObjectPropertyExpression(mce);

				for (OWLObjectProperty px : props) {
					axiom = AxiomFactory.createDisjointWith(p, px);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasCharacteristics(section)) { // Handle Characteristics
			Section<Characteristics> c = type.getCharacteristics(section);
			List<Section<?>> terms = c.get().getCharacteristics(c);

			if (terms.isEmpty()) {
				messages.add(Messages.syntaxError("No Characteristics found!"));
			}
			else {

				for (Section<?> term : terms) {
					axiom = AxiomFactory.createCharacteristics(term, p, messages);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, term));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasSubPropertyChain(section)) { // Handle SubPropertyChain
			Section<?> chain = type.getSubPropertyChain(section);

			List<Section<ObjectPropertyExpression>> objectProperties = Sections.findSuccessorsOfType(
					chain,
					ObjectPropertyExpression.class);
			if (objectProperties.isEmpty()) {
				messages.add(Messages.syntaxError(
						"no object properties found! SubpropertyChain expects  aleast two."));
			}
			else {
				axiom = AxiomFactory.createSubPropertyChain(p, objectProperties);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, chain));
					axioms.add(axiom);
				}
			}
		}

		if (ManchesterSyntaxUtil.hasAnnotations(section)) { // Handle
															// Annotations
			List<Section<Annotation>> annotations = ManchesterSyntaxUtil.getAnnotations(section);
			for (Section<Annotation> annotation : annotations) {
				axiom = AxiomFactory.createAnnotations(annotation, p.getIRI(), messages);
				if (axiom != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(axiom, annotation));
					axioms.add(axiom);
				}
			}
		}
		return axioms;
	}

}
