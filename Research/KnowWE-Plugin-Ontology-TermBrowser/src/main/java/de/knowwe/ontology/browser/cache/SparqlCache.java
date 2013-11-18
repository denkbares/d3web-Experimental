package de.knowwe.ontology.browser.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.rdf2go.Rdf2GoCore;

public class SparqlCache {
	
	private final Map<String, List<URI>> data = new HashMap<String, List<URI>>();

	private Rdf2GoCore core = null;

	public SparqlCache(Rdf2GoCore core) {
		this.core = core;
	}

	public List<URI> executeSingleVariableSparqlSelectQuery(String query) {
		if (data.containsKey(query)) {
			return data.get(query);
		}
		else {
			QueryResultTable resultTable = core.sparqlSelect(query);
			String variable = resultTable.getVariables().get(0);
			List<URI> resultSet = new ArrayList<URI>();
			for (QueryRow queryRow : resultTable) {
				Node value = queryRow.getValue(variable);
				resultSet.add(value.asURI());
			}
			data.put(query, resultSet);
			return resultSet;
		}
	}


	public void clear() {
		data.clear();
	}

}
