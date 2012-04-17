/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.input.officexmlParser;

import java.util.LinkedList;

import com.sun.org.apache.xpath.internal.axes.ChildIterator;

public class Question {

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
    private LinkedList<Question> subQuestions;
    private Question parent;
    private int id;
    private static int idCounter = 0;
    private boolean invertiereNein;
    private boolean isDummy;

    public Question(String content, String type) {
        super();
        this.content = content;
        this.type = type;
        subQuestions = new LinkedList<Question>();
        parent = null;
        score = 0;
        id = idCounter++;
        invertiereNein = false;
        isDummy = false;
    }

    /*
     * adds modifiers to questions which must have been in [] in the spec
     */
    public void addModifier(String m) {
        if (ODER_MOD.equals(m)) {
            type = m;
        }
        if (UND_MOD.equals(m)) {
            type = m;
        }
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

    public void addChild(Question c) {
        subQuestions.add(c);
    }

    public LinkedList<Question> getChildren() {
        return subQuestions;
    }

    public Question getParent() {
        return parent;
    }

    public void setParent(Question q) {
        this.parent = q;
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

    public Question getRoot() {
        if (parent == null) {
            return this;
        } else {
            return (parent.getRoot());
        }
    }

    private String getTypeString() {
        if (UND_MOD.equals(type)) {
            return "AND";
        }
        if (ODER_MOD.equals(type)) {
            return "OR";
        }
        if (SCORE_MOD.equals(type)) {
            return "SCORE " + score;
        }
        return null;
    }

    public String getXml() {
        String pid = "";
        if (parent != null) {
            pid = " parent-id='" + parent.getId() + "'";
        }
        String pt = "";
        if ((prompt != null) && (!"".equals(prompt))) {
            pt = " prompt='" + prompt;
        }
        String at = " and-or-type='" + getTypeString() + "'";
        if ((getTypeString() == null) || ("".equals(getTypeString()))) {
            at = "";
        }
        String dummyString = "";
        if (isDummy) {
            dummyString = " dummy='true'";
        }
        String negierung = "";
        if (invertiereNein) {
            negierung = " defining='nein'";
        }
        String xml =
                "<legalQuestion ";
        xml += " title='" + content + "'";
        xml += " id='" + id + "'" + pid + pt + at + dummyString + negierung + " bonus-text='" + explanation + "'" + "/>";

        return xml;
    }
}
