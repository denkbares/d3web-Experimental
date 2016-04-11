package de.knowwe.ophtovisD3.utils;

import java.util.LinkedList;

public class Connections {

	private final LinkedList<String[]> incoming;
	private final LinkedList<String[]> outgoing;

	public Connections(LinkedList<String[]> in, LinkedList<String[]> out) {
		incoming = in;
		outgoing = out;
	}
}
