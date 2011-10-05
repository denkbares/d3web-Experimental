package de.knowwe.owlapi.query;

import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 *
 *
 * @author Stefan Mark
 * @created 04.10.2011
 */
public class OWLApiQueryParser {

	private final BidirectionalShortFormProvider bidiProvider;

	public OWLApiQueryParser(ShortFormProvider shortFormProvider) {

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLOntology ontology = connector.getOntology();

		Set<OWLOntology> importsClosure = ontology.getImportsClosure();
		bidiProvider = new BidirectionalShortFormProviderAdapter(connector.getManager(),
				importsClosure,
				shortFormProvider);
	}

	/**
	 * Parses a string given in Manchester OWL Syntax into a
	 * {@link OWLClassExpression}. The OWLClassexpression can then further used
	 * to obtain results from the loaded ontology.
	 * 
	 * @param String query The entered query String
	 * @throws ParserException
	 */
	public OWLClassExpression parseManchesterOWLsyntax(String query) throws ParserException {

		OWLDataFactory factory = OWLAPIConnector.getGlobalInstance().getManager().getOWLDataFactory();
		OWLOntology ontology = OWLAPIConnector.getGlobalInstance().getOntology();

		OWLEntityChecker owlEntityChecker = new ShortFormEntityChecker(bidiProvider);

		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, query);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.parseClassExpression();
	}
}
