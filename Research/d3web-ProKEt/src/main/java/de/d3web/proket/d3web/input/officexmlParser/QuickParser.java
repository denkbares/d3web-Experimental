package de.d3web.proket.d3web.input.officexmlParser;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;

import de.uniwue.abstracttools.Pair;
import de.uniwue.abstracttools.StringUtils;
import java.io.BufferedWriter;
import java.util.ArrayList;

public class QuickParser {

	
	public static final char LINE_BREAK = '\n';
        private static String wordhtmlFinalOutput =
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_Mietrecht_Teil1.html";
    private static String parsedJuriXml =
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/juriMietrechtT1.xml";
	
      /*  private static String wordhtmlFinalOutput =
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_VTEST.html";
    private static String parsedJuriXml =
            "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/juriVTEST.xml";
    */
    
	public QuickParser() {
	}
	
	private void handleError(String msg) {
		System.err.println(msg);
	}
	
	
	public void extractQuestionInformations(String text, QuestionManager qm) {
            
                /* preprocess pdf link definitions : replace with HTML representation */
                if (text.contains("[LINK:")){
                    
                    // get the linked-doc name
                    ArrayList pdfs = new ArrayList();
                    String linksplits[] = text.split("\\[LINK: ");
                    for(String s: linksplits){
                        String innersplits[] = s.split("\\.pdf]");
                        pdfs.add(innersplits[0]);
                    }
                    
                    for(Object pdf: pdfs){
                        String infile = "[LINK: " + pdf.toString() + ".pdf]";
                        String replaced = "&#60;a href=\"/resources/" + pdf.toString()  + ".pdf\"&#62; " +
                                pdf.toString() + "&#60;/a&#62;";
                        text = text.replace(infile, replaced);
                    }
                }
                
                /* preprocess links to other wepapp parts */
                if (text.contains("[WEBLINK:")){
                    
                    // get the linked-doc name
                    ArrayList links = new ArrayList();
                    String linksplits[] = text.split("\\[WEBLINK: ");
                    for(String s: linksplits){
                        String innersplits[] = s.split("\\.weblink]");
                        links.add(innersplits[0]);
                    }
                    
                    for(Object link: links){
                        String infile = "[WEBLINK: " + link.toString() + ".weblink]";
                        String replaced = "&#60;a href=\"/Dialog?src=" + link.toString()  + "\"&#62; " +
                                link.toString() + "&#60;/a&#62;";
                        text = text.replace(infile, replaced);
                    }
                    
                }
            
                String[] questionTexts = text.split("FRAGE");
		for (String f : questionTexts) {
			f = f.trim();
			if (!"".equals(f)) {
				String[] z = f.split("\\[.*\\]");
				String questionID = z[0].trim();
				Question q = qm.getQuestion(questionID);
				if (q == null) handleError("No question '" + questionID + "' in tree, but info for this question was found.");
				else {
					String questionInfo = z[1].trim();
                                       
					String[] y = questionInfo.split("Erläuterung( )*(:)?( )*\n");
					
					String prompt = null;
					
					String erl = y[y.length-1];
					if (y.length > 1) {
						for (int i=0; i<y.length; i++) {
							if (y[i].trim().startsWith("Prompt")) {
								prompt = y[i].substring(6);
								if (prompt.startsWith(":")) prompt = prompt.substring(1);
								prompt = prompt.trim();
							} 
						}
					}
                                        
                                        q.setExplanation(erl);
					q.setPrompt(prompt);
				}
			}
		}
	}
	
	
	
	private QuestionManager parseQuestionTree(String text) {
		QuestionManager qm = new QuestionManager();
		LinkedList<Pair<String, Integer>> l = getAllDashedSubTexts(text);
		LinkedList<Question> stack = new LinkedList<Question>(); 
		Question lastQuestion = null;
		int lastDepth = 0;
		int count = 1;
		for (Pair<String, Integer> p : l) {
			int depth = p.getLast();
			String content = p.getFirst();
			System.out.println("Bearbeite Frage Nr " + count++ + " im Baum.");
			Question actQuestion = buildQuestion(content, qm);
			Question q = stack.peek();
			if (q == null) {
				if (depth == 1) stack.push(actQuestion);
				else throw new RuntimeException("No parent for Question '" + content + "'");
				lastQuestion = actQuestion;
				lastDepth = depth;
			} else {
				if (lastDepth > depth) {
					for (int j=0; j<lastDepth-depth; j++) stack.pop();
					q = stack.peek();
					if (q==null) throw new RuntimeException("No parent for Question '" + content + "'");
					lastDepth = depth;
				} else if (lastDepth < depth) {
					if (lastDepth == depth-1) {
						if (lastDepth > 1) stack.push(lastQuestion);
						lastDepth = depth;
						q = lastQuestion;
					} else throw new RuntimeException("Too large dash-step in question '" + content + "'");
				}
				q.addChild(actQuestion);
				actQuestion.addParent(q);
				lastQuestion = actQuestion;
			}
		}
		return qm;
	}
	
	
	
	private LinkedList<Pair<String, Integer>> getAllDashedSubTexts(String text) {
		LinkedList<Pair<String, Integer>> l = new LinkedList<Pair<String, Integer>>();
		int i = text.indexOf('-');
		if (i != -1) {
			text = text.substring(i);
		} else return l;
		Pair<String, Integer> p = readFirstDashedString(text);
		while (p!= null) {
			int size = p.getLast() / 100;
			if (size != 0) p.setLast(p.getLast() % 100);
			l.add(p);
			text = text.substring(size).trim();
			p = readFirstDashedString(text);
		}
		return l;
	}

	
	private Pair<String, Integer> readFirstDashedString(String text) {
		int i = 0;
		text = text.trim();
		if (!text.startsWith("-")) return null;
		while (text.startsWith("-")) {
			i++;
			text = text.substring(1);
		}
		int j = text.indexOf("\n-");
		if (j==-1) {
			String[] parts = text.split("\n\n\n");
			return new Pair(parts[0], i+(parts[0].length()+i)*100);
		} else {
			String s = text.substring(0, j).trim();
			return new Pair(s, i+(j+i)*100);
		}
	}
	
	
	
	private Question buildQuestion(String s, QuestionManager qm) {
		Pair<String, LinkedList<String>> p = getAllDelimitedTerms(s, "[", "]");
		String rumpf = p.getFirst();
		Question knownQ = qm.getQuestion(rumpf);
		if (knownQ != null) return knownQ;
		LinkedList<String> modifiers = p.getLast();
		Question q = new Question(rumpf, Question.UND_MOD);
		for (String m : modifiers) q.addModifier(m);
		qm.addQuestion(q);
		return q;
	}
	
	
	private Pair<String, LinkedList<String>> getAllDelimitedTerms(String source, String leftDelim, String rightDelim) {
		LinkedList<String> l = new LinkedList<String>();
		String r = "";
		int start = 0;
		int i = source.indexOf(leftDelim);
		while (i!=-1) {
			int j = source.indexOf(rightDelim, i + 1);
			if (j!=-1) {
				r = r + source.substring(start, i); 
				l.add(source.substring(i+1, j).trim().toLowerCase());
				start = j + 1;
				i = source.indexOf(leftDelim, start);
			}
		}
		r = r + source.substring(start, source.length());
		return new Pair<String, LinkedList<String>>(r, l);
	}
	

	
	
	private String getTreePart(String text) {
		int i = text.indexOf("FRAGE");
		if (i==-1) return "";
		else return text.substring(0,i-1);
	}
	
	private String getInfoPart(String text) {
		int i = text.indexOf("FRAGE");
		if (i==-1) return "";
		else return text.substring(i);
	}
	
	public static void main(String[] args) {
		QuickParser qp = new QuickParser();
		try {
			String text = StringUtils.readFileString(wordhtmlFinalOutput);
			QuestionManager qm = qp.parseQuestionTree(qp.getTreePart(text));			 
			text = qp.getInfoPart(text);
                        qp.extractQuestionInformations(text, qm);
			
			Writer out = new BufferedWriter(new OutputStreamWriter(
			        new FileOutputStream(parsedJuriXml), "UTF8"));
			out.write(qm.getXmlEncoding());
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
      
	
	
}
