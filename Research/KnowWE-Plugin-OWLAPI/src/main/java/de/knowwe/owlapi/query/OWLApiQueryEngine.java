package de.knowwe.owlapi.query;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
}
