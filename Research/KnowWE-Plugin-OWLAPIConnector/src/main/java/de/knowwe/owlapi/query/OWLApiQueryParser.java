package de.knowwe.owlapi.query;

import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 04.10.2011
 */
public class OWLApiQueryParser {

	private OWLDataFactory factory = null;

	private OWLOntology ontology = null;

	private OWLEntityChecker owlEntityChecker = null;

	/**
	 * Constructor.
	 * 
	 * @param shortFormProvider
	 */
	public OWLApiQueryParser(ShortFormProvider shortFormProvider) {

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		ontology = connector.getOntology();

		Set<OWLOntology> importsClosure = ontology.getImportsClosure();
		BidirectionalShortFormProvider bidiProvider = new BidirectionalShortFormProviderAdapter(
				connector.getManager(),
				importsClosure,
				shortFormProvider);

		factory = OWLAPIConnector.getGlobalInstance().getManager().getOWLDataFactory();
		owlEntityChecker = new ShortFormEntityChecker(bidiProvider);
	}

	public OWLApiQueryParser() {
		this(new SimpleShortFormProvider());
	}

	/**
	 * Parses a string given in Manchester OWL Syntax into a
	 * {@link OWLClassExpression}. The OWLClassexpression can then further used
	 * to obtain results from the loaded ontology.
	 * 
	 * @param String query The to parse String
	 * @throws ParserException
	 */
	public OWLClassExpression parseManchesterOWLsyntax(String query) throws ParserException {

		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, query);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.parseClassExpression();
	}

	/**
	 * Parses a string given in Manchester OWL Syntax into a
	 * {@link OWLClassExpression}. The OWLClassexpression can then further used
	 * to obtain results from the loaded ontology.
	 * 
	 * @param String query The to parse String
	 * @throws ParserException
	 */
	public OWLObjectPropertyExpression parseOWLObjectPropertyExpression(String query) throws ParserException {

		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, query);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.parseObjectPropertyExpression();
	}

	/**
	 * Parses a string given in Manchester OWL Syntax into a {@link OWLAxiom}.
	 * The OWLAxiom can then further used to obtain results from the loaded
	 * ontology.
	 * 
	 * @param String query The to parse String
	 * @throws ParserException
	 */
	public OWLAxiom parseStringToOWLAxiom(String query) throws ParserException {

		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, query);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.parseAxiom();
	}

	public boolean isClassName(String termIdentifier) {
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory,
				termIdentifier);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.isClassName(termIdentifier);
	}

	public boolean isObjectPropertyName(String termIdentifier) {
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory,
				termIdentifier);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.isObjectPropertyName(termIdentifier);
	}

	public boolean isDataPropertyName(String termIdentifier) {
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory,
				termIdentifier);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.isDataPropertyName(termIdentifier);
	}

	public boolean isIndividualName(String termIdentifier) {
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory,
				termIdentifier);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(owlEntityChecker);

		return parser.isIndividualName(termIdentifier);
	}

}
