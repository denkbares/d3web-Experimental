package de.knowwe.ophtovisD3.utils;

import java.util.LinkedList;


public class Connections {
	
	LinkedList<String []> incoming;
	LinkedList<String []> outgoing;
	
	
	public Connections(LinkedList<String []> in , LinkedList<String []> out) {
		incoming=in;
		outgoing=out;
	}
}
