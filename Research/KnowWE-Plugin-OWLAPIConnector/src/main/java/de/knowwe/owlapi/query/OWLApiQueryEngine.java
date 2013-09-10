package de.knowwe.owlapi.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 *
 * @author Stefan Mark
 * @created 04.10.2011
 */
public class OWLApiQueryEngine {

	private final OWLReasoner reasoner;

	private final OWLApiQueryParser parser;

	private final OWLDataFactory factory;

	public OWLApiQueryEngine(ShortFormProvider shortFormProvider) {
		this.reasoner = OWLAPIConnector.getGlobalInstance().getReasoner();
		this.parser = new OWLApiQueryParser(shortFormProvider);
		this.factory = OWLAPIConnector.getGlobalInstance().getManager().getOWLDataFactory();
	}

	public OWLApiQueryParser getParser() {
		return parser;
	}

	/**
	 *
	 *
	 * @created 04.10.2011
	 * @param String expression
	 * @param boolean direct
	 * @return Set<OWLClass>
	 * @throws ParserException
	 */
	public Set<OWLClass> getSuperClasses(String query, boolean direct) throws ParserException {
		if (query == null || query.trim().isEmpty()) {
			return Collections.emptySet();
		}
		OWLClassExpression exp = parser.parseManchesterOWLsyntax(query);
		NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(exp, direct);
		return superClasses.getFlattened();
	}

	/**
	 *
	 *
	 * @created 04.10.2011
	 * @param expression
	 * @param boolean direct Specifies if only direct subclasses should be shown or all
	 *        the subclasses given through the {@link OWLClassExpression}.
	 * @return
	 * @throws ParserException
	 */
	public Set<OWLClass> getSubClasses(String query, boolean direct) throws ParserException {
		if (query == null || query.trim().isEmpty()) {
			return Collections.emptySet();
		}
		OWLClassExpression exp = parser.parseManchesterOWLsyntax(query);
		NodeSet<OWLClass> subclasses = reasoner.getSubClasses(exp, direct);
		return subclasses.getFlattened();
	}

	/**
	 *
	 *
	 * @created 04.10.2011
	 * @param expression
	 * @return
	 * @throws ParserException
	 */
	public Set<OWLClass> getEquivalentClasses(String query) throws ParserException {
		if (query == null || query.trim().isEmpty()) {
			return Collections.emptySet();
		}
		OWLClassExpression exp = parser.parseManchesterOWLsyntax(query);
		Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(exp);
		Set<OWLClass> result;
		if (exp.isAnonymous()) {
			result = equivalentClasses.getEntities();
		}
		else {
			result = equivalentClasses.getEntitiesMinus(exp.asOWLClass());
		}
		return result;
	}

	/**
	 *
	 *
	 * @created 04.10.2011
	 * @param expression
	 * @return
	 * @throws ParserException
	 */
	public Set<OWLNamedIndividual> getIndividuals(String query, boolean direct) throws ParserException {
		if (query == null || query.trim().isEmpty()) {
			return Collections.emptySet();
		}
		OWLClassExpression exp = parser.parseManchesterOWLsyntax(query);
		NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(exp, direct);
		return individuals.getFlattened();
	}

	public Set<OWLObjectPropertyExpression> getSubProperties(String query, boolean direct) throws ParserException {
		if (query == null || query.trim().isEmpty()) {
			return Collections.emptySet();
		}

		OWLObjectPropertyExpression exp = parser.parseOWLObjectPropertyExpression(query);
		NodeSet<OWLObjectPropertyExpression> properties = reasoner.getSubObjectProperties(exp,
				direct);
		return properties.getFlattened();

	}

	/**
	 * Looks up possible explanations for the given {@link OWLClassExpression}
	 * that describe the {@link OWLEntity}.
	 *
	 * @created 16.10.2011
	 * @param OWLClassExpression exp
	 * @param OWLEntity entity
	 * @return Set<OWLAxiom> A set of found explanations
	 */
	public Set<OWLAxiom> getSubClassesExplanations(OWLClassExpression exp, OWLEntity entity) {
		if (entity.isOWLClass()) {
			OWLClass cls = entity.asOWLClass();
			OWLAxiom axiom = factory.getOWLSubClassOfAxiom(cls, exp);
			return getExplanations(axiom);
		}
		else {
			return Collections.emptySet();
		}
	}

	/**
	 * Looks up possible explanations for the given {@link OWLClassExpression}
	 * that describe the {@link OWLEntity}.
	 *
	 * @created 16.10.2011
	 * @param OWLClassExpression exp
	 * @param OWLEntity entity
	 * @return Set<OWLAxiom> A set of found explanations
	 */
	public Set<OWLAxiom> getSuperClassesExplanations(OWLClassExpression exp, OWLEntity entity) {
		if (entity.isOWLClass()) {
			OWLClass cls = entity.asOWLClass();
			OWLAxiom axiom = factory.getOWLSubClassOfAxiom(exp, cls);
			return getExplanations(axiom);
		}
		else {
			return Collections.emptySet();
		}
	}

	/**
	 * Looks up possible explanations for the given {@link OWLClassExpression}
	 * that describe the {@link OWLEntity}.
	 *
	 * @created 16.10.2011
	 * @param OWLClassExpression exp
	 * @param OWLEntity entity
	 * @return Set<OWLAxiom> A set of found explanations
	 */
	public Set<OWLAxiom> getEquivalentClassesExplanations(OWLClassExpression exp, OWLEntity entity) {
		if (entity.isOWLClass()) {
			OWLClass cls = entity.asOWLClass();
			OWLAxiom axiom = factory.getOWLEquivalentClassesAxiom(exp, cls);
			return getExplanations(axiom);
		}
		else {
			return Collections.emptySet();
		}
	}

	/**
	 * Looks up possible explanations for the given {@link OWLClassExpression}
	 * that describe the {@link OWLEntity}.
	 *
	 * @created 16.10.2011
	 * @param OWLClassExpression exp
	 * @param OWLEntity entity
	 * @return Set<OWLAxiom> A set of found explanations
	 */
	public Set<OWLAxiom> getIndividualExplanations(OWLClassExpression exp, OWLEntity entity) {
		if (entity.isOWLNamedIndividual()) {
			OWLNamedIndividual ind = entity.asOWLNamedIndividual();
			OWLAxiom axiom = factory.getOWLClassAssertionAxiom(exp, ind);
			return getExplanations(axiom);
		}
		else {
			return Collections.emptySet();
		}
	}

	/**
	 * Transforms a given {@link OWLClass} into an {@link OWLClassExpression}
	 * and looks for possible explanations that could explain the given
	 * {@link OWLAxiom}. Returns an explanation for a given {@link OWLAxiom}.
	 *
	 * @created 27.09.2011
	 * @param OWLAxiom axiom
	 * @return Set<OWLAxiom> A set of found explanations for the given OWLAxiom
	 */
	public Set<OWLAxiom> getExplanations(OWLAxiom axiom) {

		DebuggerClassExpressionGenerator visitor = new DebuggerClassExpressionGenerator(factory);
		axiom.accept(visitor);
		OWLClassExpression expression = visitor.getDebuggerClassExpression();

		reasoner.precomputeInferences();

		BlackBoxExplanation explain = new BlackBoxExplanation(
				OWLAPIConnector.getGlobalInstance().getOntology(),
				OWLAPIConnector.getGlobalInstance().getFactory(),
				reasoner);
		Set<OWLAxiom> axioms = explain.getExplanation(expression);
		return axioms;
	}

	/**
	 *
	 *
	 * @created 16.10.2011
	 * @param query
	 * @return
	 * @throws ParserException
	 */
	public OWLClassExpression getOWLClassExpression(String query) throws ParserException {
		return parser.parseManchesterOWLsyntax(query);
	}

	/**
	 *
	 *
	 * @created 07.12.2011
	 * @param entities
	 * @return
	 */
	public void getInferredObjectPropertyHierarchy(Map<OWLEntity, Set<OWLEntity>> entities, OWLObjectProperty property, OWLObjectProperty father) {

		if (father != null && !property.isBottomEntity()) {
			if (!entities.containsKey(father)) {
				entities.put(father, new TreeSet<OWLEntity>());
			}
			if (!entities.get(father).contains(property)) {
				entities.get(father).add(property);
			}
		}

		Set<OWLObjectPropertyExpression> children =
				reasoner.getSubObjectProperties(property, true).getFlattened();
		for (OWLObjectPropertyExpression child : children) {
			if (!child.getNamedProperty().equals(property) && !child.isAnonymous()) {
				getInferredObjectPropertyHierarchy(entities, child.getNamedProperty(), property);
			}
		}
	}

	/**
	 *
	 *
	 * @created 07.12.2011
	 * @param entities
	 * @return
	 */
	public void getInferredDataPropertyHierarchy(Map<OWLEntity, Set<OWLEntity>> entities, OWLDataProperty property, OWLDataProperty father) {

		if (father != null && !property.isBottomEntity()) {
			if (!entities.containsKey(father)) {
				entities.put(father, new TreeSet<OWLEntity>());
			}
			if (!entities.get(father).contains(property)) {
				entities.get(father).add(property);
			}
		}

		Set<OWLDataProperty> children =
				reasoner.getSubDataProperties(property, true).getFlattened();
		for (OWLDataProperty child : children) {
			if (!child.isAnonymous() && !child.equals(property)) {
				getInferredDataPropertyHierarchy(entities, child, property);
			}
		}
	}

	/**
	 *
	 *
	 * @created 07.12.2011
	 * @param entities
	 * @param owlClass
	 * @param owlClassFather
	 * @return
	 */
	public void getInferredClassHierarchie(Map<OWLEntity, Set<OWLEntity>> entities, OWLClass owlClass, OWLClass owlClassFather) {

		if (reasoner.isSatisfiable(owlClass)) {
			if (owlClassFather != null) {
				if (!entities.containsKey(owlClassFather)) {
					entities.put(owlClassFather, new TreeSet<OWLEntity>());
				}
				entities.get(owlClassFather).add(owlClass);
			}

			Set<OWLClass> children = reasoner.getSubClasses(owlClass, true).getFlattened();
			for (OWLClass child : children) {
				if (!child.equals(owlClass)) {
					getInferredClassHierarchie(entities, child, owlClass);
				}
			}
		}
	}

	public void getIndividuals(Set<OWLEntity> entities, OWLClass owlClass, OWLClass owlClassFather) {
		Map<OWLEntity, Set<OWLEntity>> concepts = new HashMap<OWLEntity, Set<OWLEntity>>();
		getInferredClassHierarchie(concepts, owlClass, owlClassFather);

		for (OWLEntity owlEntity : concepts.keySet()) {
			Set<OWLEntity> values = concepts.get(owlEntity);
			for (OWLEntity value : values) {
				NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(
						(OWLClass) value, true);

				if (!individualsNodeSet.isEmpty()) {
					Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

					for (OWLNamedIndividual i : individuals) {
						entities.add(i);
					}
				}
			}
		}
	}
}
