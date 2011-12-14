package de.knowwe.onte.action;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.renderer.ManchesterOWLSyntaxHTMLColorRenderer;
import de.knowwe.kdom.renderer.OnteRenderingUtils;
import de.knowwe.onte.editor.OntologyBrowserVisitor;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * Queries the ontology for stored information about an entity and returns the
 * result. Used to show information about an OWL entity in the ontology brwoser.
 *
 * @author Stefan Mark
 * @created 14.12.2011
 */
public class GetEntityInformationAction extends AbstractAction {

	public static final String PARAM_ENTITY_NAME = "entity";
	public static final String PARAM_ENTITY_TYPE = "type";

	@Override
	public void execute(UserActionContext context) throws IOException {

		StringBuilder html = new StringBuilder();
		String providedEntity = context.getParameter(PARAM_ENTITY_NAME);
		String providedType = context.getParameter(PARAM_ENTITY_TYPE);

		Class<?> cls = determineProvidedType(providedType);
		if (cls != null) {
			OWLEntity entity = AxiomFactory.getOWLAPIEntity(providedEntity, cls);

			html.append(
					"<p style=\"height:30px;\"><a style=\"float:right;\" class=\"onte-button left small\" href=\"")
					.append(OnteRenderingUtils.getHyperlink(entity.getIRI().getFragment())).append(
							"\">");
			html.append(
					"<img class=\"tree-expand\" src=\"KnowWEExtension/images/onte/document-import-2.png\"></a></p>");

			loadAnnotations(entity, html);

			if (entity instanceof OWLClass) {
				loadClassInformation((OWLClass) entity, html);
			}
			else if (entity instanceof OWLObjectProperty) {
				loadObjectPropertyInformation((OWLObjectProperty) entity, html);
			}
			else if (entity instanceof OWLDataProperty) {
				// TODO
			}
			else if(entity instanceof OWLNamedIndividual) {
				// TODO
			}
			context.getWriter().write(html.toString());
		}
	}

	private Class<?> determineProvidedType(String type) {
		if (type.equals("OWLClass")) {
			return OWLClass.class;
		}
		else if (type.equals("OWLObjectProperty")) {
			return OWLObjectProperty.class;
		}
		return null;
	}

	/**
	 * Render class information for the current entity.
	 *
	 * @created 08.12.2011
	 * @param cls
	 * @param html
	 */
	private void loadObjectPropertyInformation(OWLObjectProperty ope, StringBuilder html) {
		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLOntology ontology = connector.getOntology();

		Set<OWLObject> owlObjects = new HashSet<OWLObject>();
		Set<OWLClassExpression> owlClassExpressions = ope.getDomains(connector.getOntology());
		if (!owlClassExpressions.isEmpty()) {
			html.append("<div class=\"onte-box\"><strong>Domain</strong></div>");
			owlObjects.addAll(owlClassExpressions);
			renderResult(owlObjects, html);
		}

		owlClassExpressions = ope.getRanges(connector.getOntology());
		if (!owlClassExpressions.isEmpty()) {
			html.append("<div class=\"onte-box\"><strong>Range</strong></div>");
			owlObjects.clear();
			owlObjects.addAll(owlClassExpressions);
			renderResult(owlObjects, html);
		}

		Set<OWLObjectPropertyExpression> owlObjectPropertyExpressions = ope.getInverses(ontology);
		if (!owlObjectPropertyExpressions.isEmpty()) {
			html.append("<div class=\"onte-box\"><strong>InverseOf</strong></div>");
			owlObjects.clear();
			owlObjects.addAll(owlObjectPropertyExpressions);
			renderResult(owlObjects, html);
		}

		html.append("<div class=\"onte-box\"><strong>Characteristics</strong></div>");

		if (ope.isAsymmetric(ontology)) {
			html.append(ManchesterSyntaxKeywords.ASYMMETRIC.getKeyword() + " ");
		}
		if (ope.isSymmetric(ontology)) {
			html.append(ManchesterSyntaxKeywords.SYMMETRIC.getKeyword() + " ");
		}
		if (ope.isFunctional(ontology)) {
			html.append(ManchesterSyntaxKeywords.FUNCTIONAL.getKeyword() + " ");
		}
		if (ope.isInverseFunctional(ontology)) {
			html.append(ManchesterSyntaxKeywords.INVERSE_FUNCTIONAL.getKeyword() + " ");
		}
		if (ope.isTransitive(ontology)) {
			html.append(ManchesterSyntaxKeywords.TRANSITIVE.getKeyword() + " ");
		}
		if (ope.isReflexive(ontology)) {
			html.append(ManchesterSyntaxKeywords.REFLEXIVE.getKeyword() + " ");
		}
		if (ope.isIrreflexive(ontology)) {
			html.append(ManchesterSyntaxKeywords.IRREFLEXIVE.getKeyword() + " ");
		}
	}

	/**
	 * Render class information for the current entity.
	 *
	 * @created 08.12.2011
	 * @param cls
	 * @param html
	 */
	private void loadClassInformation(OWLClass cls, StringBuilder html) {
		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		Set<OWLClassAxiom> axioms = connector.getOntology().getAxioms(cls);
		OntologyBrowserVisitor visitor = new OntologyBrowserVisitor(cls);

		for (OWLClassAxiom owlClassAxiom : axioms) {
			owlClassAxiom.accept(visitor);
		}

		// ... create EquivalentTo ...
		if (!visitor.getEquivalents().isEmpty()) {
			html.append("<div class=\"onte-box\"><strong>EquivalentTo</strong></div>");
			renderResult(visitor.getEquivalents(), html);
		}

		// ... create SubClassOf ...
		if (!visitor.getSubclasses().isEmpty()) {
			html.append("<div class=\"onte-box\"><strong>SubClassOf</strong></div>");
			renderResult(visitor.getSubclasses(), html);
		}

		// ... create DisjointWith ...
		if (!visitor.getDisjoints().isEmpty()) {
			html.append("<div class=\"onte-box\"><strong>DisjointWith</strong></div>");
			renderResult(visitor.getDisjoints(), html);
		}

	}

	/**
	 * Render the found results for a given frame.
	 *
	 * @created 08.12.2011
	 * @param owlClassExpressions
	 * @param html
	 */
	private void renderResult(Set<OWLObject> owlObjects, StringBuilder html) {
		html.append("<ul>");
		Iterator<OWLObject> it = owlObjects.iterator();
		while (it.hasNext()) {

			String verbalizedAxiom = OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(it.next());
			ManchesterOWLSyntaxHTMLColorRenderer renderer = new ManchesterOWLSyntaxHTMLColorRenderer();

			html.append("<li>");
			renderer.colorize(verbalizedAxiom, html, null, null);
			html.append("</li>");
		}
		html.append("</ul>");
	}

	/**
	 * Load annotations for a given entity.
	 *
	 * @created 05.12.2011
	 * @param OWLEntity entity
	 * @param StringBuilder html
	 */
	private void loadAnnotations(OWLEntity entity, StringBuilder html) {

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();

		html.append("<div class=\"onte-box\"><strong>Annotations</strong></div>");
		html.append("<ul>");

		// first labels ...
		IRI annotation = OWLRDFVocabulary.RDFS_LABEL.getIRI();
		Set<OWLAnnotation> annotations = entity.getAnnotations(connector.getOntology(),
				connector.getManager().getOWLDataFactory().getOWLAnnotationProperty(annotation));

		for (OWLAnnotation owlAnnotation : annotations) {
			html.append("<li><dl><dt>Label:</dt><dd>")
					.append(owlAnnotation.getValue()).append("</dd></li>");
		}

		// and now comments ...
		annotation = OWLRDFVocabulary.RDFS_COMMENT.getIRI();
		annotations = entity.getAnnotations(connector.getOntology(),
				connector.getManager().getOWLDataFactory().getOWLAnnotationProperty(annotation));

		for (OWLAnnotation owlAnnotation : annotations) {
			html.append("<li><dl><dt>Comment:</dt><dd>")
					.append(owlAnnotation.getValue()).append("</dd></li>");
		}
		html.append("</ul>");
	}
}
