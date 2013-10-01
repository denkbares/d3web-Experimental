package de.knowwe.fingerprint;

import java.util.LinkedList;
import java.util.List;

public class Diff {

	private boolean equal = true;
	private final List<String> messages = new LinkedList<String>();

	public boolean isEqual() {
		return equal;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void fail(String message) {
		this.equal = false;
		messages.add(message);
	}

}
