package de.knowwe.owlapi.query;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 *
 * @author Stefan Mark
 * @created 04.10.2011
 */
public class OWLApiQueryEngine {

	private final OWLReasoner reasoner;

	private final OWLApiQueryParser parser;

	public OWLApiQueryEngine(ShortFormProvider shortFormProvider) {
		this.reasoner = OWLAPIConnector.getGlobalInstance().getReasoner();
		this.parser = new OWLApiQueryParser(shortFormProvider);
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
		NodeSet<OWLClass> superClasses = reasoner.getSubClasses(exp, direct);
		return superClasses.getFlattened();
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
}
