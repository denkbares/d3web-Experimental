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
	
	private final Map<String, List<URI>> selectQueryData = new HashMap<String, List<URI>>();
	private final Map<String, Boolean> askQueryData = new HashMap<String, Boolean>();

	private Rdf2GoCore core = null;

	public SparqlCache(Rdf2GoCore core) {
		this.core = core;
	}

	public List<URI> executeSingleVariableSparqlSelectQuery(String query) {
		if (selectQueryData.containsKey(query)) {
			return selectQueryData.get(query);
		}
		else {
			QueryResultTable resultTable = core.sparqlSelect(query);
			String variable = resultTable.getVariables().get(0);
			List<URI> resultSet = new ArrayList<URI>();
			for (QueryRow queryRow : resultTable) {
				Node value = queryRow.getValue(variable);
				try {
					URI uri = value.asURI();
					resultSet.add(uri);
				}
				catch (ClassCastException e) {
					// if it's not any URI (e.g. BlankNode) we ignore the node
				}
			}
			selectQueryData.put(query, resultSet);
			return resultSet;
		}
	}

	public boolean executeSparqlAskQuery(String query) {
		if (askQueryData.containsKey(query)) {
			return askQueryData.get(query);
		}
		else {
			boolean result = core.sparqlAsk(query);
			askQueryData.put(query, new Boolean(result));
			return result;
		}
	}


	public void clear() {
		selectQueryData.clear();
		askQueryData.clear();
	}

}
