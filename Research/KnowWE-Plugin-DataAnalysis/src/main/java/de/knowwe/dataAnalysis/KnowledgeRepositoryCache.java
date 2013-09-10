package de.knowwe.dataAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnowledgeRepositoryCache {

	private static KnowledgeRepositoryCache instance;

	public static KnowledgeRepositoryCache getInstance() {
		if (instance == null) {
			instance = new KnowledgeRepositoryCache();
		}
		return instance;
	}

	private KnowledgeRepositoryCache() {
	}

	private final List<String[]> insertKnowledge = new ArrayList<String[]>();
	private final List<String[]> removeKnowledge = new ArrayList<String[]>();

	public List<String[]> getCachedInsertKnowledge() {
		return Collections.unmodifiableList(insertKnowledge);
	}

	public List<String[]> getCachedRemoveKnowledge() {
		return Collections.unmodifiableList(removeKnowledge);
	}

	public void clearCaches() {
		insertKnowledge.clear();
		removeKnowledge.clear();
	}

	void addInsertKnowledge(String[] k) {
		insertKnowledge.add(k);
	}

	void addRemoveKnowledge(String[] k) {
		removeKnowledge.add(k);
	}

}
