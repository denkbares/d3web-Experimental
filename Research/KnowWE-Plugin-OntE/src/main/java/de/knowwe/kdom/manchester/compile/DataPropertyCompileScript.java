package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.DataPropertyFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Characteristics;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIAbstractKnowledgeUnitCompileScript;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

public class DataPropertyCompileScript extends OWLAPIAbstractKnowledgeUnitCompileScript<DataPropertyFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync with
	 * RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public DataPropertyCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<DataPropertyFrame> section, Collection<Message> messages) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLDataProperty p = null;
		OWLAxiom axiom = null;

		// handle definition, then the rest
		DataPropertyFrame type = section.get();
		if (type.hasDataPropertyDefinition(section)) {
			Section<?> def = type.getDataPropertyDefinition(section);
			p = (OWLDataProperty) AxiomFactory.getOWLAPIEntity(def, OWLDataProperty.class);
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

			// if (mce == null) {
			// messages.add(Messages.syntaxError("Range is empty!"));
			// }
			// else {
			// // TODO: implement DataRange AbstractTypes
			// Map<OWLClassExpression, Section<? extends Type>> exp =
			// AxiomFactory.createDescriptionExpression(
			// mce, messages);
			//
			// for (OWLClassExpression e : exp.keySet()) {
			// axiom = AxiomFactory.createDataPropertyRange(p, e);
			// if (axiom != null) {
			// EventManager.getInstance().fireEvent(
			// new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
			// axioms.add(axiom);
			// }
			// }
			// }
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
					axiom = AxiomFactory.createDataPropertyDomain(p, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
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
				Set<OWLDataProperty> props = AxiomFactory.createDataPropertyExpression(mce);

				for (OWLDataProperty px : props) {
					axiom = AxiomFactory.createDataSubPropertyOf(p, px);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasEquivalentTo(section)) { // Handle EquivalentTo
			Section<?> desc = type.getEquivalentTo(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			if (mce == null) {
				messages.add(Messages.syntaxError("EquivalentTo is empty!"));
			}
			else {
				Set<OWLDataProperty> props = AxiomFactory.createDataPropertyExpression(mce);

				for (OWLDataProperty px : props) {
					axiom = AxiomFactory.createDataPropertyEquivalentTo(p, px);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasDisjointWith(section)) { // handle DisjointWith
			Section<?> desc = type.getDisjointWith(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("DisJointWith is empty!"));
			}
			else {
				Set<OWLDataProperty> props = AxiomFactory.createDataPropertyExpression(mce);

				for (OWLDataProperty px : props) {
					axiom = AxiomFactory.createDataPropertyDisjointWith(p, px);
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
					axiom = AxiomFactory.createDataPropertyCharacteristics(term, p, messages);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, term));
						axioms.add(axiom);
					}
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
