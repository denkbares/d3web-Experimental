package de.d3web.KnOfficeParser.table;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.KnOfficeParser.IDObjectManagement;
import de.d3web.KnOfficeParser.KnOfficeParameterSet;
import de.d3web.KnOfficeParser.KnOfficeParser;
import de.d3web.KnOfficeParser.util.D3webQuestionFactory;
import de.d3web.report.Message;
import de.d3web.KnOfficeParser.util.MessageKnOfficeGenerator;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreater;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreaterEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLessEqual;
import de.d3web.kernel.domainModel.ruleCondition.TerminalCondition;
/**
 * Builder um d3web Wissen mithilfe des TableParsers zu generieren
 * @author Markus Friedrich
 *
 */
public class D3webBuilder implements Builder, KnOfficeParser {

	private boolean lazy=false;
	private boolean lazydiag=false;
	private List<Message> errors = new ArrayList<Message>();
	private QContainer currentqclass;
	private String file;
	private Question currentquestion;
	private AnswerChoice currentanswer;
	private CellKnowledgeBuilder ckb;
	private TableParser tb;
	private int counter = 0;
	private int errorcount =0;
	private IDObjectManagement idom;
	
	public D3webBuilder(String file, CellKnowledgeBuilder ckb, IDObjectManagement idom) {
		this(file, ckb, 0, 0, idom);
	}
	
	public D3webBuilder(String file, CellKnowledgeBuilder ckb, int startcolumn, int startrow, IDObjectManagement idom) {
		this(file, ckb, startcolumn, startrow, null, idom);
		tb=new TableParser(this, startcolumn, startrow);
	}
	
	public D3webBuilder(String file, CellKnowledgeBuilder ckb, int startcolumn, int startrow, TableParser tbin, IDObjectManagement idom) {
		this.file=file;
		this.ckb=ckb;
		this.idom=idom;
		tb = tbin;
		tb.startcolumn=startcolumn;
		tb.startrow=startrow;
		tb.builder=this;
	}
	
	private void finish() {
		if (errors.size()==0) {
			errors.add(MessageKnOfficeGenerator.createXLSFileParsed(file, counter));
		}
	}
	
	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}
	
	public void setLazyDiag(boolean lazy) {
		this.lazydiag=lazy;
	}
	
	@Override
	public void addKnowledge(String question, String answer, String solution,
			String value, int line, int column) {
		counter++;
		errorcount=errors.size();
		if (answer!=null) answer = answer.trim();
		boolean typedef=false;
		String type ="";
		if (question.endsWith("]")) {
			type=question.substring(question.lastIndexOf('[')+1, question.length()-1);
			if (type.equals("y/n")) {
				type="yn";
			}
			question=question.substring(0, question.lastIndexOf('[')).trim();
			typedef=true;
		}
		if (currentquestion==null||!currentquestion.getText().equals(question)) {
			currentquestion = idom.findQuestion(question);
			if (currentquestion==null) {
				if (lazy) {
					if (currentqclass==null) {
						currentqclass=(QContainer) idom.getKnowledgeBase().getRootQASet();
					}
					if (typedef) {
						currentquestion=D3webQuestionFactory.createQuestion(idom, currentqclass, question, type);
					} else if (answer==null) {
						currentquestion=idom.createQuestionYN(question, currentqclass);
					} else if (answer.startsWith("<")||answer.startsWith("[")||answer.startsWith(">")||answer.startsWith("=")) {
						currentquestion=idom.createQuestionNum(question, currentqclass);
					} else {
						currentquestion=idom.createQuestionOC(question, currentqclass, new AnswerChoice[0]);
					}
				} else {
					errors.add(MessageKnOfficeGenerator.createQuestionNotFoundException(file, line, column, "", question));
					return;
				}
			} else {
				if (!D3webQuestionFactory.checkType(currentquestion, type)) {
					errors.add(MessageKnOfficeGenerator.createTypeMismatchWarning(file, line, column, "", currentquestion.getText(), type));
				}
			}
		}
		Diagnosis diag = idom.findDiagnosis(solution);
		if (diag==null) {
			if (lazydiag) {
				diag=idom.createDiagnosis(solution, idom.getKnowledgeBase().getRootDiagnosis());
			} else {
				errors.add(MessageKnOfficeGenerator.createDiagnosisNotFoundException(file, line, column, "", solution));
				return;
			}
		}
		TerminalCondition cond;
		if (currentquestion instanceof QuestionNum) {
			QuestionNum qnum = (QuestionNum) currentquestion;
			String s;
			if (answer.startsWith("<=")) {
				s=answer.substring(2).trim();
				Double d=Double.parseDouble(s);
				cond = new CondNumLessEqual(qnum,d);
			} else if (answer.startsWith("<")) {
				s=answer.substring(1).trim();
				Double d=Double.parseDouble(s);
				cond = new CondNumLess(qnum,d);
			} else if (answer.startsWith("=")) {
				s=answer.substring(1).trim();
				Double d=Double.parseDouble(s);
				cond = new CondNumEqual(qnum,d);
			} else if (answer.startsWith(">=")) {
				s=answer.substring(2).trim();
				Double d=Double.parseDouble(s);
				cond = new CondNumGreaterEqual(qnum,d);
			} else if (answer.startsWith(">")) {
				s=answer.substring(1).trim();
				Double d=Double.parseDouble(s);
				cond = new CondNumGreater(qnum,d);
			} else if (answer.startsWith("[")) {
				s=answer.substring(1, answer.length()-1).trim();
				int i = s.lastIndexOf(' ');
				Double d1;
				Double d2;
				try {
					d1 = Double.parseDouble(s.substring(0, i));
					d2 = Double.parseDouble(s.substring(i+1));
				} catch (NumberFormatException e) {
					errors.add(MessageKnOfficeGenerator.createAnswerNotNumericException(file, line, column, "", s));
					return;
				}
				cond = new CondNumIn(qnum, d1, d2);
			} else {
				cond=null;
				errors.add(MessageKnOfficeGenerator.createAnswerNotNumericException(file, line, column, "", answer));
				return;
			}
		} else if (currentquestion instanceof QuestionYN) {
			QuestionYN qyn = (QuestionYN) currentquestion;
			AnswerChoice ac;
			if (answer==null||answer.equalsIgnoreCase("ja")||answer.equalsIgnoreCase("yes")) {
				ac=qyn.yes;
			} else if (answer.equalsIgnoreCase("nein")||answer.equalsIgnoreCase("no")){
				ac=qyn.no;
			} else {
				errors.add(MessageKnOfficeGenerator.createAnswerNotYNException(file, line, column, "", answer));
				return;
			}
			cond = new CondEqual(qyn, ac);
		} else  if (currentquestion instanceof QuestionChoice){
			QuestionChoice qc = (QuestionChoice) currentquestion;
			if (currentanswer==null||!currentanswer.getText().equals(answer)) {
				currentanswer = idom.findAnswerChoice(qc, answer);
				if (currentanswer==null) {
					if (lazy) {
						if (answer==null) {
							errors.add(MessageKnOfficeGenerator.createAnswerCreationUnambiguousException(file, line, column, "", answer));
							return;
						} else {
							currentanswer=(AnswerChoice) idom.addChoiceAnswer(qc, answer);
						}
					} else {
						errors.add(MessageKnOfficeGenerator.createAnswerNotFoundException(file, line, column, "", answer, answer));
						return;
					}
				}
			}
			cond = new CondEqual(qc, currentanswer);
		} else {
			cond=null;
			errors.add(MessageKnOfficeGenerator.createQuestionTypeNotSupportetException(file, line, column, "", question));
			return;
		}
		boolean errorOccured = false;
		if (errorcount!=errors.size()) {
			errorOccured = true;
		}
		Message msg= ckb.add(idom, line, column, file, cond, value, diag, errorOccured);
		if (msg!=null) {
			errors.add(msg);
		}
	}

	@Override
	public void setQuestionClass(String name, int line, int column) {
		currentqclass = idom.findQContainer(name);
		if (currentqclass == null) {
			if (lazy) {
				currentqclass = idom.createQContainer(name, idom.getKnowledgeBase().getRootQASet());
				if (idom.getKnowledgeBase().getInitQuestions().isEmpty()) {
					ArrayList<QASet> tmp = new ArrayList<QASet>();
					tmp.add(currentqclass);
					idom.getKnowledgeBase().setInitQuestions(tmp);
				}
			} else {
				errors.add(MessageKnOfficeGenerator.createQuestionClassNotFoundException(file, line, column, "", name));
			}
		}
	}

	@Override
	public Collection<Message> addKnowledge(Reader r,
			IDObjectManagement idom, KnOfficeParameterSet s) {
		this.idom=idom;
		tb.parse(new File(file));
		finish();
		return errors;
	}

	@Override
	public List<Message> checkKnowledge() {
		finish();
		return errors;
	}

	@Override
	public void addXlsError() {
		errors.add(MessageKnOfficeGenerator.createNoXlsFileException(file, 0, ""));
	}

	@Override
	public void addNoDiagsError(int startrow) {
		errors.add(MessageKnOfficeGenerator.createNoDiagsError(file, startrow));
	}

	@Override
	public void addNoQuestionError(int i, int j) {
		errors.add(MessageKnOfficeGenerator.createNoQuestionOnStack(file, i, j, ""));
	}

}
