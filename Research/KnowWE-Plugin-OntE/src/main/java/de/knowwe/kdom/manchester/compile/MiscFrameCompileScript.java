package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.MiscFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIAbstractKnowledgeUnitCompileScript;
import de.knowwe.owlapi.OWLAPISubtreeHandler;


public class MiscFrameCompileScript extends OWLAPIAbstractKnowledgeUnitCompileScript<MiscFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync with
	 * RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public MiscFrameCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<MiscFrame> section, Collection<Message> messages) {
		OWLAxiom a = null;
		MiscFrame type = (MiscFrame) section.get();

		Set<OWLAnnotation> annotations = handleOptionalAnnotations(section, messages);

		if (type.isDifferentIndividuals(section) || type.isSameIndividuals(section)) {
			a = AxiomFactory.createMiscFrameIndividuals(section, annotations, messages);
		}
		else if (type.isDisjointClasses(section) || type.isEquivalentClasses(section)) {
			a = AxiomFactory.createMiscFrameClasses(section, annotations, messages);
		}
		else if (type.isDisjointProperties(section) || type.isEquivalentProperties(section)) {
			a = AxiomFactory.createMiscFrameObjectProperties(section, annotations, messages);
		}
		else if (type.isDisjointDataProperties(section) || type.isEquivalentDataProperties(section)) {
			a = AxiomFactory.createMiscFrameDataProperties(section, annotations, messages);
		}

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		if (a != null) {
			EventManager.getInstance().fireEvent(new OWLApiAxiomCacheUpdateEvent(a, section));
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
	private Set<OWLAnnotation> handleOptionalAnnotations(Section<? extends Type> section, Collection<Message> messages) {

		Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();

		if (ManchesterSyntaxUtil.hasAnnotations(section)) {
			List<Section<Annotation>> annotationSections = ManchesterSyntaxUtil.getAnnotations(section);
			for (Section<Annotation> annotation : annotationSections) {
				OWLAnnotation a = AxiomFactory.createOWLAnnotation(annotation, messages);
				if (a != null) {
					EventManager.getInstance().fireEvent(
							new OWLApiAxiomCacheUpdateEvent(a, section));
					annotations.add(a);
				}
			}
		}
		return annotations;
	}

}
