package de.knowwe.owlapi.query.pellet;

import java.util.HashMap;
import java.util.Map;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 *
 *
 * @author Stefan Mark
 * @created 06.01.2012
 */
public class PelletSparqlUtils {

	public static Map<String, String> getDefaultNamespaces() {

		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		namespaces.put("owl:", "http://www.w3.org/2002/07/owl#");
		namespaces.put("rdfs:", "http://www.w3.org/2000/01/rdf-schema#");
		namespaces.put("xsd:", "http://www.w3.org/2001/XMLSchema#");
		namespaces.put(":", OWLAPIConnector.getGlobalInstance().getGlobalBaseIRI().toString());

		return namespaces;
	}

	public static String getDefaultNamespacesVerbalized() {

		Map<String, String> namespaces = getDefaultNamespaces();
		StringBuilder prefix = new StringBuilder();

		for (String key : namespaces.keySet()) {
			prefix.append("PREFIX ").append(key).append("<");
			prefix.append(namespaces.get(key)).append(">\n");
		}
		return prefix.toString();
	}

	/**
	 * Execute the SELECT SPARQL query and return the results. Note: loads the
	 * local ontology into a Jena model and executes the queries witin this
	 * model!
	 *
	 * @created 06.01.2012
	 * @param query
	 * @return
	 */
	public static ResultSet selectQuery(String query) {
		OWLOntology ontology = OWLAPIConnector.getGlobalInstance().getOntology();

		PelletReasoner reasoner =
				PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);

		KnowledgeBase kb = reasoner.getKB();
		kb.classify();
		PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind(kb);
		InfModel model = ModelFactory.createInfModel(graph);

		Query q = QueryFactory.create(query);
		QueryExecution qe = SparqlDLExecutionFactory.create(q, model);
		ResultSet rs = qe.execSelect();
		return rs;
	}

	/**
	 * Execute the SELECT SPARQL query and return the results. Note: loads the
	 * local ontology into a Jena model and executes the queries witin this
	 * model!
	 *
	 * @created 06.01.2012
	 * @param query
	 * @return
	 */
	public static boolean askQuery(String query) {
		OWLOntology ontology = OWLAPIConnector.getGlobalInstance().getOntology();

		PelletReasoner reasoner =
				PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);

		KnowledgeBase kb = reasoner.getKB();
		kb.classify();
		PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind(kb);
		InfModel model = ModelFactory.createInfModel(graph);

		Query q = QueryFactory.create(query);
		QueryExecution qe = SparqlDLExecutionFactory.create(q, model);
		return qe.execAsk();
	}
}
