package de.knowwe.rdf2go;

import java.util.List;

import org.ontoware.rdf2go.model.Statement;

import de.d3web.we.event.Event;

public class InsertStatementsEvent extends Event {

	private final List<Statement> statements;

	public InsertStatementsEvent(List<Statement> list) {
		statements = list;
	}

	public List<Statement> getStatements() {
		return statements;
	}
}
