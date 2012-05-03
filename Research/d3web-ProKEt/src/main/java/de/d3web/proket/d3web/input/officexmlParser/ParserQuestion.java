package de.d3web.proket.d3web.input.officexmlParser;


import java.util.HashSet;
import java.util.LinkedList;

import com.sun.org.apache.xpath.internal.axes.ChildIterator;

public class ParserQuestion {

	
	public static String ODER_MOD = "oder";
	public static String UND_MOD = "und";
	public static String SCORE_MOD = "score";
	public static String NEGIEREND_MOD = "nein";
	public static String DUMMY_MOD = "dummy";
	private String content;
	private String type;
	private int score;
	private String explanation;
	private String prompt;
	private LinkedList<ParserQuestion> subQuestions;
	private LinkedList<ParserQuestion> parents;
	private int id;
	private static int idCounter = 0;
	private boolean invertiereNein;
	private boolean isDummy;

	
	public ParserQuestion(String content, String type) {
		this.parents = new LinkedList<ParserQuestion>();
		this.content = content;
		this.type = type;
		subQuestions = new LinkedList<ParserQuestion>();
		score = 0;
		id = idCounter++;
		invertiereNein = false;
		isDummy = false;
	}
	
	
	public void addModifier(String m) {
		if (ODER_MOD.equals(m)) type = m;
		if (UND_MOD.equals(m)) type = m;
		if (m.startsWith(SCORE_MOD)) {
			type = SCORE_MOD;
			m = m.substring(SCORE_MOD.length()).trim();
			score = Integer.parseInt(m);
		}
		if (NEGIEREND_MOD.equals(m)) {
			invertiereNein = true;
		}
		if (DUMMY_MOD.equals(m)) {
			isDummy = true;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getPrompt() {
		return prompt;
	}

	
	public void setScore(int s) {
		score = s;
	}
	
	public int getScore() {
		return score;
	}
	

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public void addChild(ParserQuestion c) {
		if (!subQuestions.contains(c)) {
			subQuestions.add(c);
			c.addParent(this);
		}
	}
	
	public LinkedList<ParserQuestion> getChildren() {
		return subQuestions;
	}
	
	public LinkedList<ParserQuestion> getParents() {
		return parents;
	}
	
	public void addParent(ParserQuestion q) {
		if (!parents.contains(q)) {
			q.addChild(this);
			parents.add(q);
		}
	}
	
	public boolean isInvertiereNein() {
		return invertiereNein;
	}

	public void setInvertiereNein(boolean invertiereNein) {
		this.invertiereNein = invertiereNein;
	}

	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public HashSet<ParserQuestion> getRoots() {
		HashSet<ParserQuestion> roots = new HashSet<ParserQuestion>();
		if (parents.size() == 0) roots.add(this);
		else {
			for (ParserQuestion p : parents) {
				roots.addAll(p.getRoots());
			}
		}
		return roots;
	}
	
	private String getTypeString() {
		if (UND_MOD.equals(type)) return "AND";
		if (ODER_MOD.equals(type)) return "OR";
		if (SCORE_MOD.equals(type)) return "SCORE " + score;
		return null;
	}
	
	public String getXml(int parentId, int customId) {
		String pid = "";
		if (parentId != -1) pid = " parent-id='" + parentId + "'";
		String pt = "";
		if ((prompt != null) && (!"".equals(prompt))) pt = " prompt='" + prompt;
		String at = " and-or-type='" + getTypeString() + "'";
		if ((getTypeString() == null) || ("".equals(getTypeString()))) at = "";
		String dummyString = "";
		if (isDummy) dummyString = " dummy='true'";
		String negierung = "";
		if (invertiereNein) negierung = " defining='nein'";
		return "<legalQuestion title='" + content + "' id='" + customId + "'" + pid + pt + at + dummyString + negierung + " bonus-text='" + explanation + "'" + "/>\n"; 
	}
	
	

	
	
	
}
