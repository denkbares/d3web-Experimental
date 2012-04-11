/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.input.officexmlParser;

import java.util.Hashtable;
import java.util.LinkedList;

import de.uniwue.abstracttools.BinaryRelation;
import de.uniwue.abstracttools.ListUtils;
import de.uniwue.abstracttools.StringUtils;

public class QuestionManager {

	private Hashtable<String, Question> questions;
	
	
	public QuestionManager() {
		questions = new Hashtable<String, Question>();
	}
	
	
	public void addQuestion(Question q) {
		questions.put(getQuestionIndex(q.getContent()), q);
	}
	
	private String getQuestionIndex(String c) {
		String s = StringUtils.replaceEveryOccurrence(c, " ", "");
		s = StringUtils.replaceEveryOccurrence(s, "-", "");
		s = s.toLowerCase();
		return s;
	}
	
	public Question getQuestion(String content) {
		return questions.get(getQuestionIndex(content));
	}
	
	
	public Question getRoot() {
		if (questions.size() >0) {
			return questions.values().iterator().next().getRoot();
		} else return null;
	}
	
	public String getXmlEncoding() {
		String s = "<?xml version='1.0' encoding='UTF-8'?>\n";
		s = s + "<dialog sub-type='front' type='legal' css='legal, nofoot' header='K&#252;ndigungsschutz-Beratung -- Hierarchischer-Dialog' and-or-type='AND' uequest='OWN' study='true' logging='true' feedback='true'>";
		LinkedList<Question> l = new LinkedList<Question>();
		l.addAll(questions.values());
		ListUtils.mergeSort(l, new BinaryRelation(){
			@Override
			public boolean inRelation(Object a, Object b) {
				Question qa = (Question)a;
				Question qb = (Question)b;
				return (qa.getId() < qb.getId());
			}
			
		});
		for (Question q : l) s = s + q.getXml() + "\n";
		s = s + "</dialog>";
                
		s = runCharacterEncoding(s);
                
		return s;
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
                //s = s.replaceAll("<", "&#60;");
                //s = s.replaceAll(">", "&#62;");
		s = s.replaceAll("<li>", "&#60;li&#62;");
		s = s.replaceAll("</li>", "&#60;/li&#62;");
		s = s.replaceAll("<ol>", "&#60;ol&#62;");
		s = s.replaceAll("</ol>", "&#60;/ol&#62;");
		s = s.replaceAll("\\.\\.\\.", "&#8230;");
		return s;
	}

	
	
}
