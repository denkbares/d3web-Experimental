/*
 * Copyright (C) 2012 denkbares GmbH, Germany
 */
package com.denkbares.ciconnector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Sebastian Furth
 * @created 17.12.2012
 */
public class CIInfo {

	private final String plugin;
	private String description;
	private Map<String, String> stat = new HashMap<String, String>();
	private List<Change> changes = new LinkedList<Change>();

	public CIInfo(String plugin) {
		if (plugin == null) {
			throw new NullPointerException();
		}
		this.plugin = plugin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getStat() {
		return stat;
	}

	public void addStat(String id, String value) {
		this.stat.put(id, value);
	}

	public List<Change> getChanges() {
		return changes;
	}

	public void addChange(Change change) {
		this.changes.add(change);
	}

	public String getPlugin() {
		return plugin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changes == null) ? 0 : changes.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
		result = prime * result + ((stat == null) ? 0 : stat.hashCode());
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
		CIInfo other = (CIInfo) obj;
		if (changes == null) {
			if (other.changes != null) {
				return false;
			}
		}
		else if (!changes.equals(other.changes)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		}
		else if (!description.equals(other.description)) {
			return false;
		}
		if (plugin == null) {
			if (other.plugin != null) {
				return false;
			}
		}
		else if (!plugin.equals(other.plugin)) {
			return false;
		}
		if (stat == null) {
			if (other.stat != null) {
				return false;
			}
		}
		else if (!stat.equals(other.stat)) {
			return false;
		}
		return true;
	}

}
