package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIAbstractKnowledgeUnitCompileScript;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

public class ClassFrameCompileScript extends OWLAPIAbstractKnowledgeUnitCompileScript<ClassFrame> {

	/**
	 * Constructor for the {@link SubtreeHandler}. Here you can set if a sync
	 * with {@link RDF2Go} should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public ClassFrameCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<ClassFrame> section, Collection<Message> messages) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLClass clazz = null;
		OWLAxiom axiom = null;

		// handle class definition, then the rest
		ClassFrame type = section.get();
		if (type.hasClassDefinition(section)) {
			Section<?> def = type.getClassDefinition(section);
			clazz = (OWLClass) AxiomFactory.getOWLAPIEntity(def, OWLClass.class);
			axiom = AxiomFactory.getOWLAPIEntityDeclaration(clazz);
			if (axiom != null) {
				EventManager.getInstance().fireEvent(
						new OWLApiAxiomCacheUpdateEvent(axiom, section));
				axioms.add(axiom);
			}
		}

		if (type.hasAnnotations(section)) { // Handle Annotations
			List<Section<Annotation>> annotations = type.getAnnotations(section);
			if (!annotations.isEmpty()) {
				for (Section<Annotation> annotation : annotations) {
					axiom = AxiomFactory.createAnnotations(annotation, clazz.getIRI(), messages);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, annotation));
						axioms.add(axiom);
					}
				}
			}
			else {
				messages.add(Messages.syntaxError(
						"Annotations missing! Please specify atleast one annotation or delete the keyword."));
			}
		}

		if (type.hasSubClassOf(section)) { // Handle SubClassOf
			Section<?> desc = type.getSubClassOf(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("SubClassOf is empty!"));
			}
			else {

				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);
				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createOWLSubClassOf(clazz, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
						// axioms.add(AxiomFactory.getClassDeclaration(e.asOWLClass()));
					}
				}
			}
		}

		if (type.hasDisjointWith(section)) { // Handle DisjointWith
			Section<?> desc = type.getDisjointWith(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("DisJointWith is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createOWLDisjointWith(clazz, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
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
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createOWLEquivalentTo(clazz, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasDisjointUnionOf(section)) { // Handle DisjointunionOf
			Section<?> desc = type.getDisjointUnionOf(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("DisjointUnionOf is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					// FIXME not yet implemented
					// axiom = AxiomFactory.createOWLDisjointUnionOf(clazz, e);
					axiom = null;
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}

		// TODO: really necessary new compile script should handle this ???
		// needs testing
		// Note: use OWLEntityCollector instead of below?
		// necessary to avoid errors through the OWLApi due not defined entities
		// Set<OWLAxiom> addAxioms = new HashSet<OWLAxiom>();
		// for (OWLAxiom a : axioms) {
		// Set<OWLClass> classes = a.getClassesInSignature();
		// for (OWLClass owlClass : classes) {
		// OWLAxiom t = AxiomFactory.getOWLAPIEntityDeclaration(owlClass);
		// addAxioms.add(t);
		// }
		//
		// Set<OWLObjectProperty> properties =
		// a.getObjectPropertiesInSignature();
		// for (OWLObjectProperty p : properties) {
		// OWLAxiom t = AxiomFactory.getOWLAPIEntityDeclaration(p);
		// addAxioms.add(t);
		// }
		// }
		// addAxioms.addAll(axioms);
		return axioms;
	}
}
