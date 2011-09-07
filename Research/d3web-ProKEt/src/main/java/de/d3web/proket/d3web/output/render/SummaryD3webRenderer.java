package de.d3web.proket.d3web.output.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.utilities.Pair;
import de.d3web.proket.d3web.input.D3webConnector;

public class SummaryD3webRenderer extends AbstractD3webRenderer {

	public enum SummaryType {
		QUESTIONNAIRE,
		GRID
	}

	public String renderSummaryDialog(Session d3webSession, SummaryType type) {

		StringBuilder bui = new StringBuilder();

		if (type == SummaryType.GRID) {
			fillGridSummary(d3webSession, bui);
		}
		else if (type == SummaryType.QUESTIONNAIRE) {
			TerminologyObject root = d3webSession.getKnowledgeBase().getRootQASet();
			fillQuestionnaireSummaryChildren(d3webSession, bui, root);
		}

		return bui.toString();
	}

	private void fillGridSummary(Session d3webSession, StringBuilder bui) {
		KnowledgeBase knowledgeBase = d3webSession.getKnowledgeBase();
		for (Resource resource : knowledgeBase.getResources()) {
			String pathName = resource.getPathName();

			boolean isGrid = pathName.endsWith(".txt");
			if (!isGrid) continue;

			int lastSlash = pathName.lastIndexOf('/') + 1;
			isGrid = pathName.substring(lastSlash).toLowerCase().startsWith("grid");
			if (!isGrid) continue;

			StringBuilder content = getText(resource);

			List<Pair<Pair<Integer, Integer>, Pair<String, Object>>> result = parse(content.toString());
			if (result.isEmpty()) continue;

			boolean nothingAnswered = enrichGrid(d3webSession, content, result);
			if (nothingAnswered) continue;

			bui.append(content.toString());
		}
		if (bui.length() == 0) {
			bui.append("No data available yet.");
		}
	}

	private boolean enrichGrid(Session d3webSession,
			StringBuilder content,
			List<Pair<Pair<Integer, Integer>, Pair<String, Object>>> result) {

		Blackboard bb = d3webSession.getBlackboard();
		KnowledgeBase knowledgeBase = d3webSession.getKnowledgeBase();
		boolean nothingAnswered = true;
		for (Pair<Pair<Integer, Integer>, Pair<String, Object>> parsePair : result) {
			int matchStart = parsePair.getA().getA();
			int matchEnd = parsePair.getA().getB();

			String questionName = parsePair.getB().getA();
			Question question = knowledgeBase.getManager().searchQuestion(questionName);
			if (question == null) continue;

			Value value = bb.getValue(question);
			if (UndefinedValue.isUndefinedValue(value)) {
				content.replace(matchStart, matchEnd, "<div> </div>");
				continue;
			}

			Object answerTypeObject = parsePair.getB().getB();

			if (answerTypeObject instanceof String[]) {
				if (!isCorrectChoiceValue((String[]) answerTypeObject, value)) {
					content.replace(matchStart, matchEnd, "<div> </div>");
					continue;
				}
				content.replace(matchStart, matchEnd, "X");
			}
			else {
				@SuppressWarnings("unchecked")
				Pair<Integer, Integer> interval = (Pair<Integer, Integer>) answerTypeObject;
				if (!isCorrectNumValue(interval, value)) {
					content.replace(matchStart, matchEnd, "<div> </div>");
					continue;
				}
				content.replace(matchStart, matchEnd, String.valueOf(value.getValue()));
			}

			nothingAnswered = false;

		}
		return nothingAnswered;
	}

	private boolean isCorrectChoiceValue(String[] answerNames, Value value) {
		for (String answerName : answerNames) {
			if (value instanceof MultipleChoiceValue) {
				if (((MultipleChoiceValue) value).contains(new ChoiceID(answerName))) {
					return true;
				}
			}
			if (value instanceof ChoiceValue) {
				if (((ChoiceValue) value).getChoiceID().equals(new ChoiceID(answerName))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCorrectNumValue(Pair<Integer, Integer> interval, Value value) {
		if (value instanceof NumValue) {
			Double doubleValue = ((NumValue) value).getDouble();
			int intervalStart = interval.getA();
			int intervalEnd = interval.getB();
			if (doubleValue >= intervalStart
					&& doubleValue < intervalEnd) {
				return true;
			}
		}
		return false;
	}

	private List<Pair<Pair<Integer, Integer>, Pair<String, Object>>> parse(String content) {

		Matcher matcher = Pattern.compile("##(.+?)\\|(?:choice\\|(.+?)|num\\|(\\d*),(\\d*))##").matcher(
				content);
		LinkedList<Pair<Pair<Integer, Integer>, Pair<String, Object>>> result = new LinkedList<Pair<Pair<Integer, Integer>, Pair<String, Object>>>();
		while (matcher.find()) {

			Pair<Integer, Integer> startEndPair = new Pair<Integer, Integer>(matcher.start(),
					matcher.end());

			String question = matcher.group(1);
			String choice = matcher.group(2);
			String intervalStart = matcher.group(3);
			String intervalEnd = matcher.group(4);

			Pair<String, Object> questionAnswerPair = null;
			if (choice != null) {
				String[] choiceSplit = choice.split("\\|");
				questionAnswerPair = new Pair<String, Object>(question, choiceSplit);
			}
			else {
				int start = intervalStart.isEmpty()
						? Integer.MIN_VALUE
						: Integer.parseInt(intervalStart);
				int end = intervalEnd.isEmpty()
						? Integer.MAX_VALUE
						: Integer.parseInt(intervalEnd);
				questionAnswerPair = new Pair<String, Object>(question,
						new Pair<Integer, Integer>(start, end));
			}
			// Pair 1: start of the match + end of the match
			// Pair 2: question + answer(s) or interval
			result.addFirst(new Pair<Pair<Integer, Integer>, Pair<String, Object>>(startEndPair,
					questionAnswerPair));
		}
		return result;
	}

	private StringBuilder getText(Resource resource) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream inputStream = resource.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

	private void fillQuestionnaireSummaryChildren(Session d3webSession, StringBuilder bui, TerminologyObject to) {

		if (to instanceof QContainer && !to.getName().contains("Q000")) {
			bui.append("<div style='margin-top:10px;'><b>" + D3webConnector.getInstance().getID(to)
					+ " " + to.getName()
					+ "</b></div>\n");
		}
		else if (to instanceof Question) {
			Value val = d3webSession.getBlackboard().getValue((ValueObject) to);
			if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
				bui.append("<div style='margin-left:10px;'>"
						+ D3webConnector.getInstance().getID(to) + " " + to.getName()
						+ " -- " + val + "</div>\n");
			}
		}

		if (to.getChildren() != null && to.getChildren().length != 0) {
			for (TerminologyObject toc : to.getChildren()) {
				fillQuestionnaireSummaryChildren(d3webSession, bui, toc);
			}
		}

	}

}
