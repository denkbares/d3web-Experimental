package de.d3web.we.core.semantic.rdf2go;

import java.util.List;

import org.ontoware.rdf2go.model.Statement;

import de.d3web.we.event.Event;

public class RemoveStatementsEvent extends Event {

	private final List<Statement> statements;

	public RemoveStatementsEvent(List<Statement> list) {
		statements = list;
	}

	public List<Statement> getStatements() {
		return statements;
	}

}
