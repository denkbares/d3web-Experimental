package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.DataTypeFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.DataRangeExpression;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
import de.knowwe.owlapi.OWLAPIAbstractKnowledgeUnitCompileScript;
import de.knowwe.owlapi.OWLAPISubtreeHandler;


public class DatatypeCompileScript extends OWLAPIAbstractKnowledgeUnitCompileScript<DataTypeFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync with
	 * RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public DatatypeCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<DataTypeFrame> section, Collection<Message> messages) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLDatatype d = null;
		OWLAxiom axiom = null;

		// handle definition, then the rest
		DataTypeFrame type = (DataTypeFrame) section.get();
		if (type.hasDefinition(section)) {
			Section<?> def = type.getDefinition(section);
			d = (OWLDatatype) AxiomFactory.getOWLAPIEntity(def, OWLDatatype.class);
			axiom = AxiomFactory.getOWLAPIEntityDeclaration(d);
			if (axiom != null) {
				EventManager.getInstance().fireEvent(
						new OWLApiAxiomCacheUpdateEvent(axiom, section));
				axioms.add(axiom);
			}
		}

		if (type.hasEquivalentTo(section)) { // Handle EquivalentTo
			Section<?> desc = type.getEquivalentTo(section);
			Section<DataRangeExpression> dre = Sections.findSuccessor(desc,
					DataRangeExpression.class);

			if (dre == null) {
				messages.add(Messages.syntaxError("EquivalentTo is empty!"));
			}
			else {
				Map<OWLDataRange, Section<? extends Type>> exp = AxiomFactory.createDataRangeExpression(
						dre, messages);

				for (OWLDataRange e : exp.keySet()) {
					axiom = AxiomFactory.createOWLDataTypeEquivalentTo(d, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}

		// Handle optional Annotations
		if (ManchesterSyntaxUtil.hasAnnotations(section)) {
			List<Section<Annotation>> annotations = ManchesterSyntaxUtil.getAnnotations(section);
			for (Section<Annotation> annotation : annotations) {
				axiom = AxiomFactory.createAnnotations(annotation, d.getIRI(), messages);
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
