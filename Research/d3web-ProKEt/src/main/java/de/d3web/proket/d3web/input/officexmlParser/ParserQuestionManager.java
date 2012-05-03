package de.d3web.proket.d3web.input.officexmlParser;


import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import de.uniwue.abstracttools.BinaryRelation;
import de.uniwue.abstracttools.ListUtils;
import de.uniwue.abstracttools.Pair;
import de.uniwue.abstracttools.StringUtils;

public class ParserQuestionManager {

	private Hashtable<String, ParserQuestion> questions;
	
	
	public ParserQuestionManager() {
		questions = new Hashtable<String, ParserQuestion>();
	}
	
	
	public void addQuestion(ParserQuestion q) {
		questions.put(getQuestionIndex(q.getContent()), q);
	}
	
	private String getQuestionIndex(String c) {
		String s = StringUtils.replaceEveryOccurrence(c, " ", "");
		s = StringUtils.replaceEveryOccurrence(s, "-", "");
		s = s.toLowerCase();
		return s;
	}
	
	public ParserQuestion getQuestion(String content) {
		return questions.get(getQuestionIndex(content));
	}
	
	
	public ParserQuestion getRoot() {
		if (questions.size() >0) {
			HashSet<ParserQuestion> roots = questions.values().iterator().next().getRoots();
			if (roots.size() > 0) return roots.iterator().next();
			else return null;
		} else return null;
	}
	
	public String getXmlEncoding() {
		return getTreeFormatXml();
	}
	
	private String getTreeFormatXml() {
		String s = "<?xml version='1.0' encoding='UTF-8'?>\n";
		s = s + "<dialog sub-type='front' type='legal' css='legal, nofoot' header='K&#252;ndigungsschutz-Beratung -- Hierarchischer-Dialog' and-or-type='AND' uequest='OWN' study='true' logging='true' feedback='true'>";
		ParserQuestion r = getRoot();
		Pair<String, Integer> result = getSubTreeXml(r, 0, -1);
		s = s + result.getFirst();
		s = s + "</dialog>";
		s = runCharacterEncoding(s); 
		return s;
	}
	
	private Pair<String, Integer> getSubTreeXml(ParserQuestion q, int idCounter, int parentId) {
		int id = ++idCounter;
		String s = q.getXml(parentId, id);
		for (ParserQuestion c : q.getChildren()) {
			Pair<String, Integer> r = getSubTreeXml(c, idCounter, id);
			idCounter = r.getLast();
			s = s + r.getFirst();
		}
		return new Pair<String, Integer>(s, idCounter);
	}
	
	
		
	
	private static String runCharacterEncoding(String s) {
		s = replaceCharsWithUnicodeStrings(s);
		return s;
	}
	
	
	private static String replaceCharsWithUnicodeStrings(String s) {
		s = s.replaceAll("ä", "&#228;");
		s = s.replaceAll("ü", "&#252;");
		s = s.replaceAll("ö", "&#246;");
		s = s.replaceAll("Ü", "&#220;");
		s = s.replaceAll("Ä", "&#196;");
		s = s.replaceAll("Ö", "&#214;");
		s = s.replaceAll("ß", "&#223;");
		s = s.replaceAll("§", "&#167;");
		s = s.replaceAll("<li>", "&#60;li&#62;");
		s = s.replaceAll("</li>", "&#60;/li&#62;");
		s = s.replaceAll("<ol>", "&#60;ol&#62;");
		s = s.replaceAll("</ol>", "&#60;/ol&#62;");
		s = s.replaceAll("\\.\\.\\.", "&#8230;");
		return s;
	}

	
	
}

