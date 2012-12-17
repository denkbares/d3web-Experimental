/*
 * Copyright (C) 2012 denkbares GmbH, Germany
 */
package com.denkbares.ciconnector;

/**
 * 
 * @author Sebastian Furth
 * @created 17.12.2012
 */
public class Change {

	private final String revision;
	private final String timestamp;
	private final String author;
	private final String commitText;

	public Change(String revision, String timestamp, String author, String commitText) {
		if (revision == null || timestamp == null || author == null || commitText == null) {
			throw new NullPointerException();
		}
		this.revision = revision.trim();
		this.timestamp = timestamp.trim();
		this.author = author.trim();
		this.commitText = commitText.trim();
	}

	public String getRevision() {
		return revision;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getAuthor() {
		return author;
	}

	public String getCommitText() {
		return commitText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((commitText == null) ? 0 : commitText.hashCode());
		result = prime * result + ((revision == null) ? 0 : revision.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Change other = (Change) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		}
		else if (!author.equals(other.author)) {
			return false;
		}
		if (commitText == null) {
			if (other.commitText != null) {
				return false;
			}
		}
		else if (!commitText.equals(other.commitText)) {
			return false;
		}
		if (revision == null) {
			if (other.revision != null) {
				return false;
			}
		}
		else if (!revision.equals(other.revision)) {
			return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		}
		else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
	}

}
